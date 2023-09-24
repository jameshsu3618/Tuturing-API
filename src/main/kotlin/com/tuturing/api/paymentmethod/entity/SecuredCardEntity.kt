package com.tuturing.api.paymentmethod.entity

import com.tuturing.api.paymentmethod.valueobject.PaymentCardType
import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "payment_method_secured_card")
@EntityListeners(AuditingEntityListener::class)
class SecuredCardEntity(
    @Lob
    @Column(name = "encrypted_data", nullable = false, columnDefinition = "BLOB")
    var encryptedData: ByteArray,

    @Column(name = "payment_card_type", nullable = false, columnDefinition = "varchar(25) default 'PERSONAL_CREDIT_CARD'")
    @Enumerated(EnumType.STRING)
    var paymentCardType: PaymentCardType
) : BaseEntity() {
    @OneToOne(mappedBy = "securedCard", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
    lateinit var personalCard: PersonalCardEntity

    constructor() : this(
            byteArrayOf(), PaymentCardType.PERSONAL_CREDIT_CARD
    ) {}
}
