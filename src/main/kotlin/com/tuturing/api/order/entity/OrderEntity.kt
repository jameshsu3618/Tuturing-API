package com.tuturing.api.order.entity

import com.tuturing.api.order.valueobject.CancelledByType
import com.tuturing.api.order.valueobject.OrderStatus
import com.tuturing.api.order.valueobject.TransactionType
import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import com.tuturing.api.paymentmethod.valueobject.CardNetwork
import com.tuturing.api.shared.entity.CorporateEntity
import com.tuturing.api.shared.iterable.sumByBigDecimal
import com.tuturing.api.shared.valueobject.Money
import com.tuturing.api.user.entity.UserEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "order")
@EntityListeners(AuditingEntityListener::class)
class OrderEntity(
    @Column(name = "public_id", nullable = false, unique = true, columnDefinition = "CHAR(16)")
    var publicId: String,

    @Column(name = "is_tuturing", nullable = false, columnDefinition = "BOOLEAN")
    var istuturing: Boolean,

    @Column(name = "external_order_id", nullable = true, columnDefinition = "VARCHAR(30)")
    var externalOrderId: String?,

    @Column(name = "external_confirmation_id", nullable = true, columnDefinition = "VARCHAR(30)")
    var externalConfirmationId: String?,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: OrderStatus,

    @Column(name = "purchaser_first_name", nullable = false, columnDefinition = "VARCHAR(200)")
    var purchaserFirstName: String,

    @Column(name = "purchaser_last_name", nullable = false, columnDefinition = "VARCHAR(200)")
    var purchaserLastName: String,

    @Column(name = "account_last_four", nullable = true, columnDefinition = "CHAR(4)")
    var accountLastFour: String?,

    @Column(name = "card_issuer", nullable = true)
    @Enumerated(EnumType.STRING)
    var cardIssuer: CardNetwork?,

    @Column(name = "payment_method_type", nullable = true, columnDefinition = "VARCHAR(20)")
    var paymentMethodType: String?,

    @Column(name = "amount_base", nullable = false, columnDefinition = "DECIMAL(16,4)")
    var amountSubtotal: BigDecimal,

    @Column(name = "amount_tax", nullable = false, columnDefinition = "DECIMAL(16,4)")
    var amountTax: BigDecimal,

    @Column(name = "amount_fee", nullable = false, columnDefinition = "DECIMAL(16,4)")
    var amountFee: BigDecimal,

    @Column(name = "amount_total", nullable = false, columnDefinition = "DECIMAL(16,4)")
    var amountTotal: BigDecimal,

    @Column(name = "currency", nullable = false, columnDefinition = "VARCHAR(3)")
    var currency: String,

    @Column(name = "ordered_at", nullable = true)
    var orderedAt: LocalDateTime?,

    @Column(name = "cancelled_at", nullable = true)
    var cancelledAt: LocalDateTime?,

    @Column(name = "cancelled_by", nullable = true)
    @Enumerated(EnumType.STRING)
    var cancelledBy: CancelledByType?
) : CorporateEntity() {
    constructor() : this("", true, null, null,
            OrderStatus.CREATED, "", "", null, null,
            null, BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0),
            "USD", null, null, null)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_card_id", nullable = true, referencedColumnName = "id")
    var personalCard: PersonalCardEntity? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaser_id", nullable = false, referencedColumnName = "id")
    lateinit var purchaser: UserEntity

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    var transactions: MutableSet<TransactionEntity> = HashSet()

    fun isCanceled(): Boolean {
        return OrderStatus.CANCELED == status
    }

    fun publicIdWithDashes(): String {
        return publicId.chunked(4).joinToString("-")
    }

    /*
     *  returns Money object with sum of all transaction.amountTotal values where transaction.currency matches param
     */
    fun amountTotal(currency: String = CURRENCY_USD): Money {
        return Money(
            transactions.filter { it.currency == currency }.map { it.amountTotal }.sumByBigDecimal { it },
            currency
        )
    }

    /*
     *  returns Money object with sum of all transaction.amountTotal values where transaction.currency matches param
     *  and amountTotal is negative
     */
    fun amountRefund(currency: String = CURRENCY_USD): Money {
        return Money(
            transactions.filter {
                it.currency == currency && it.type == TransactionType.REFUND
            }.map { it.amountTotal }.sumByBigDecimal { it },
            currency
        )
    }

    companion object {
        private const val CURRENCY_USD = "USD"
    }
}
