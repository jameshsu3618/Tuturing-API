package com.tuturing.api.order.controller

import com.tuturing.api.itinerary.dto.OrderDto
import com.tuturing.api.itinerary.mapper.OrderMapper
import com.tuturing.api.order.dto.CancellationDetailsDto
import com.tuturing.api.order.dto.CancellationRequestDto
import com.tuturing.api.order.mapper.CancellationDetailsMapper
import com.tuturing.api.order.service.OrderCancellationService
import com.tuturing.api.order.service.OrderService
import com.tuturing.api.order.service.exception.CancellationException
import com.tuturing.api.shared.controller.clientIpFromRequest
import com.tuturing.api.shared.dto.error.Error
import com.tuturing.api.shared.valueobject.Money
import java.util.*
import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class CancellationController(
    @Autowired private val orderService: OrderService,
    @Autowired private val orderCancellationService: OrderCancellationService,
    @Autowired private val cancellationDetailsMapper: CancellationDetailsMapper,
    @Autowired private val orderMapper: OrderMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("/{orderId}/cancellation-details")
    fun getCancellationDetails(@PathVariable orderId: UUID, request: HttpServletRequest): ResponseEntity<CancellationDetailsDto> {
        return orderService.findById(orderId)?.let { order ->
            val customerIp = clientIpFromRequest(request) ?: ""
            orderCancellationService.getCancellationDetails(order, customerIp)?.let {
                ResponseEntity.ok(cancellationDetailsMapper.convertToDto(it))
            } ?: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @PutMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: UUID, @RequestBody requestDto: CancellationRequestDto?, request: HttpServletRequest): ResponseEntity<OrderDto> {
        return orderService.findById(orderId)?.let { order ->
            val customerIp = clientIpFromRequest(request) ?: ""
            val expectedRefund = requestDto?.let {
                Money(
                    requestDto.expectedRefundAmount.toBigDecimal(),
                    requestDto.expectedRefundCurrency
                )
            }
            orderCancellationService.cancelOrder(order, expectedRefund, customerIp).fold(
                {
                    ResponseEntity.ok(orderMapper.convertToDto(order))
                },
                {
                    when (it) {
                        is CancellationException -> throw it
                        else -> {
                            logger.error("Order Cancellation Error: ${it.message}")
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                        }
                    }
                }
            )
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @ExceptionHandler(CancellationException::class)
    fun handleCancellationException(e: CancellationException): ResponseEntity<Error> {
        val error = Error(e.reason, e.message, null)
        return when (e) {
            is CancellationException.APIError -> {
                // Log error and remove before sending to client
                logger.error(e.message)
                ResponseEntity(Error(e.reason, null, null), HttpStatus.UNPROCESSABLE_ENTITY)
            }
            is CancellationException.NotFound -> {
                ResponseEntity(error, HttpStatus.NOT_FOUND)
            }
            is CancellationException.RefundMismatch,
            is CancellationException.UnprocessableEntity -> {
                ResponseEntity(error, HttpStatus.UNPROCESSABLE_ENTITY)
            }
        }
    }
}
