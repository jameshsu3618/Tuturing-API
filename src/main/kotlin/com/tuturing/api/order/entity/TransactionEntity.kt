package com.tuturing.api.order.entity

import com.tuturing.api.order.valueobject.TransactionType
import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import com.tuturing.api.shared.entity.CorporateEntity
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "order_transaction")
@EntityListeners(AuditingEntityListener::class)
class TransactionEntity(
    @Column(name = "index", nullable = true, columnDefinition = "INTEGER")
    var index: Int?,

    @Column(name = "action", nullable = true, columnDefinition = "VARCHAR(3)")
    val action: String?,

    @Column(name = "airline_code", nullable = true, columnDefinition = "VARCHAR(3)")
    val airlineCode: String?,

    @Column(name = "amount_subtotal", nullable = false, columnDefinition = "DECIMAL(16,4)")
    val amountSubtotal: BigDecimal,

    @Column(name = "amount_tax", nullable = false, columnDefinition = "DECIMAL(16,4)")
    val amountTax: BigDecimal,

    @Column(name = "amount_fee", nullable = false, columnDefinition = "DECIMAL(16,4)")
    var amountFee: BigDecimal,

    @Column(name = "amount_total", nullable = false, columnDefinition = "DECIMAL(16,4)")
    val amountTotal: BigDecimal,

    @Column(name = "currency", nullable = false, columnDefinition = "CHAR(3)")
    val currency: String,

    @Column(name = "ticket_number", nullable = true, columnDefinition = "VARCHAR(25)")
    val ticketNumber: String?,

    @Column(name = "original_ticket_number", nullable = true, columnDefinition = "VARCHAR(25)")
    val originalTicketNumber: String?,

    @Column(name = "invoice_number", nullable = true, columnDefinition = "VARCHAR(25)")
    val invoiceNumber: String?,

    @Column(name = "original_invoice_number", nullable = true, columnDefinition = "VARCHAR(25)")
    val originalInvoiceNumber: String?,

    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(25)")
    @Enumerated(EnumType.STRING)
    val type: TransactionType
) : CorporateEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, referencedColumnName = "id")
    lateinit var order: OrderEntity

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_card_id", nullable = true, referencedColumnName = "id")
    var personalCard: PersonalCardEntity? = null

}
