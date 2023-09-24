package com.tuturing.api.paymentmethod.entity

import com.tuturing.api.location.entity.CountryEntity
import com.tuturing.api.location.entity.SubdivisionEntity
import com.tuturing.api.paymentmethod.valueobject.CardNetwork
import com.tuturing.api.paymentmethod.valueobject.PaymentCardType
import com.tuturing.api.shared.entity.PersonalEntity
import com.tuturing.api.user.entity.SabreTravelerProfileSyncListener
import com.tuturing.api.user.entity.UserEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "payment_method_personal_card")
@EntityListeners(AuditingEntityListener::class, SabreTravelerProfileSyncListener::class)
class PersonalCardEntity(
    @Column(name = "card_nickname", nullable = true, columnDefinition = "VARCHAR(200)")
    var cardNickname: String?,

    @Column(name = "name_on_card", nullable = false, columnDefinition = "VARCHAR(200)")
    var nameOnCard: String,

    @Column(name = "card_number", nullable = false, columnDefinition = "VARCHAR(20)")
    var cardNumber: String,

    @Column(name = "stripe_payment_method", nullable = true, columnDefinition = "VARCHAR(200)")
    var stripePaymentMethod: String,

    @Column(name = "address_one", nullable = false, columnDefinition = "VARCHAR(200)")
    var addressOne: String,

        // Optional field for unit, suite, building etc
    @Column(name = "address_two", nullable = true, columnDefinition = "VARCHAR(200)")
    var addressTwo: String?,

    @Column(name = "city", nullable = false, columnDefinition = "VARCHAR(200)")
    var city: String,

    @Column(name = "zip_code", nullable = false, columnDefinition = "VARCHAR(10)")
    var zipCode: String,

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN")
    var isDeleted: Boolean,

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime?,

    @Column(name = "card_network", nullable = false, columnDefinition = "varchar(25) default 'UNKNOWN'")
    @Enumerated(EnumType.STRING)
    var cardNetwork: CardNetwork,

    @Column(name = "payment_card_type", nullable = false, columnDefinition = "varchar(25) default 'PERSONAL_CREDIT_CARD'")
    @Enumerated(EnumType.STRING)
    var paymentCardType: PaymentCardType
) : PersonalEntity() {
    @Column(name = "expiration_date", nullable = true)
    var expirationDate: LocalDate = LocalDate.now()
        set(value) {
            field = value.plusMonths(1).minus(Period.ofDays(1))
        }

    // TODO Figure out how frequently country and subdivision entities are needed and update fetchType as needed
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdivision_id", referencedColumnName = "id", nullable = false)
    lateinit var subdivision: SubdivisionEntity

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id")
    lateinit var country: CountryEntity

    @OneToOne(fetch = FetchType.LAZY)
    lateinit var deletedBy: UserEntity

    @OneToOne(fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "secured_card_id", referencedColumnName = "id", nullable = false, unique = true)
    lateinit var securedCard: SecuredCardEntity

    constructor() : this(
            "",
            "",
            "",
            "",
            "",
            null,
            "",
            "",
            false,
            null,
            CardNetwork.UNKNOWN,
            PaymentCardType.PERSONAL_CREDIT_CARD
    ) {}

    fun isExpired(): Boolean {
        return expirationDate.isBefore(LocalDate.now())
    }

    companion object {
        public const val SAFE_TO_SAVE_DIGITS_LENGTH = 4
    }
}
