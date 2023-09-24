package com.tuturing.api.order.service

import com.tuturing.api.eps.rapid.service.HotelCancellationService
import com.tuturing.api.flight.service.FlightCancellationService
import com.tuturing.api.order.entity.OrderEntity
import com.tuturing.api.order.entity.TransactionEntity
import com.tuturing.api.order.service.exception.CancellationException
import com.tuturing.api.order.valueobject.*
import com.tuturing.api.shared.valueobject.Money
import java.math.BigDecimal
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderCancellationService(
    @Autowired private val hotelCancellationService: HotelCancellationService,
    @Autowired private val transactionService: TransactionService,
    @Autowired private val flightCancellationService: FlightCancellationService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    fun getCancellationDetails(order: OrderEntity, customerIp: String): CancellationDetails? {
        return when {
            null != order.bookedHotel -> {
                hotelCancellationService.getCancellationDetails(order, customerIp).getOrNull()
            }
            else -> null
        }
    }

    fun cancelOrder(order: OrderEntity, expectedRefund: Money?, customerIp: String): Result<OrderEntity> {
        return when {
            uncancellableOrderStatuses.any { order.status == it } -> Result.failure(CancellationException.UnprocessableEntity("Can't cancel order with status: ${order.status}"))
            null != order.bookedHotel
                && null != expectedRefund -> {
                validateExpectedRefund(order, expectedRefund, customerIp).mapCatching {
                    hotelCancellationService.cancelOrder(order, expectedRefund, customerIp).getOrThrow()
                    order
                }
            }
            null != order.bookedFlight -> {
                flightCancellationService.cancelOrder(order)
            }
            else -> Result.failure(CancellationException.UnprocessableEntity("Missing bookedHotel/bookedFlight record"))
        }
    }

    fun validateExpectedRefund(order: OrderEntity, expectedRefund: Money?, customerIp: String): Result<Boolean> {
        return hotelCancellationService.getCancellationDetails(order, customerIp).mapCatching {
            if (it.refundAmount == expectedRefund) {
                true
            } else {
                throw CancellationException.RefundMismatch
            }
        }
    }

    private fun saveRefundTransaction(
        refundAmount: Money,
        order: OrderEntity
    ): Result<TransactionEntity> {
        return kotlin.runCatching {
            val negativeAmount = refundAmount.amount.abs().negate()
            val transaction = TransactionEntity(
                amountSubtotal = negativeAmount,
                amountTax = BigDecimal("0.00"),
                amountFee = BigDecimal("0.00"),
                amountTotal = negativeAmount,
                currency = refundAmount.currency,
                action = null,
                airlineCode = null,
                index = null,
                invoiceNumber = null,
                originalInvoiceNumber = null,
                originalTicketNumber = null,
                ticketNumber = null,
                type = TransactionType.REFUND
            ).apply {
                this.order = order
                this.company = order.company
            }
            if (null != order.personalCard) {
                transaction.personalCard = order.personalCard
            } else {
                transaction.companyCard = order.companyCard
            }
            transactionService.save(transaction)
            logger.debug("Saved refund amount: ${transaction.amountTotal} ${transaction.currency}")
            transaction
        }
    }

    companion object {
        private val uncancellableOrderStatuses = listOf(
            OrderStatus.PENDING_CANCELLATION,
            OrderStatus.CANCELED,
            OrderStatus.DECLINED
        )
    }
}
