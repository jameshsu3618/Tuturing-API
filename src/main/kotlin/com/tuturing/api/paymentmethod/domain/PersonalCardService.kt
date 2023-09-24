package com.tuturing.api.paymentmethod.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.paymentmethod.api.KmsClient
import com.tuturing.api.paymentmethod.api.StripeClient
import com.tuturing.api.paymentmethod.dto.resource.SecuredCardDto
import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import com.tuturing.api.paymentmethod.entity.SecuredCardEntity
import com.tuturing.api.paymentmethod.repository.PersonalCardRepository
import com.tuturing.api.paymentmethod.repository.SecuredCardRepository
import com.tuturing.api.paymentmethod.valueobject.CardNetwork
import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.entity.UserProfileEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class PersonalCardService(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val personalCardRepository: PersonalCardRepository,
    @Autowired private val securedCardRepository: SecuredCardRepository,
    @Autowired private val kmsClient: KmsClient,
    @Autowired private val stripeClient: StripeClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findById(id: UUID): PersonalCardEntity? {
        return personalCardRepository.findByIdOrNull(id)
    }

//    @PreAuthorize("#tuturing.isPrincipal(#userProfile)")
//    fun findByUserProfile(userProfile: UserProfileEntity, isDeleted: Boolean, sort: Sort): MutableIterable<PersonalCardEntity> {
//        return personalCardRepository.findAllByUserProfileIdAndIsDeleted(userProfile.id!!, isDeleted, sort)
//    }

    @PreAuthorize("#tuturing.isPersonal(#personalCard)")
    fun softDelete(personalCard: PersonalCardEntity): Boolean {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        if (!personalCard.isDeleted) {
            personalCard.isDeleted = true
            personalCard.deletedAt = LocalDateTime.now()
            personalCard.deletedBy = principal.user

            personalCardRepository.save(personalCard)
            return true
        }
        return false
    }

    @PreAuthorize("#tuturing.isPersonal(#personalCard)")
    private fun validate(personalCard: PersonalCardEntity): Boolean {
        val paymentMethod = personalCard.stripePaymentMethod
        val stripePaymentMethod = stripeClient.validatePaymentCard(paymentMethod)

        if (stripePaymentMethod.status == "succeeded") {
            return true
        }
        return false
    }

    @PreAuthorize("#tuturing.isPersonal(#oldPersonalCard) and #tuturing.isPersonal(#newPersonalCard)")
    fun update(oldPersonalCard: PersonalCardEntity, newPersonalCard: PersonalCardEntity, cardNumber: String, cvv: String, expirationMonth: Long, expirationYear: Long): Boolean {
        return if (!oldPersonalCard.isDeleted) {
            // Soft-delete the old card once new card is validated and saved
            this.add(newPersonalCard, cardNumber, cvv, expirationMonth, expirationYear)
            softDelete(oldPersonalCard)
        } else {
            return false
        }
    }

    private fun save(personalCard: PersonalCardEntity, cardNumber: String, cvv: String, expirationMonth: Long, expirationYear: Long): Boolean {
        // TODO: add stripe validation before encrypting card object
        val secureDto = SecuredCardDto(cardNumber, cvv, expirationMonth, expirationYear)
        var creditCardJson: String = jacksonObjectMapper().writeValueAsString(secureDto)

        val securedCard = kmsClient.encrypt(creditCardJson)

        if (securedCard.isNotEmpty()) {
            val securedPersonalCard = SecuredCardEntity()

            securedPersonalCard.encryptedData = securedCard
            securedPersonalCard.personalCard = personalCard
            saveEncrypted(personalCard, securedPersonalCard)
            return true
        }
        return false
    }

    // TODO: Move method to AspectJ
    @Transactional
    private fun saveEncrypted(personalCard: PersonalCardEntity, securedPersonalCard: SecuredCardEntity) {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        personalCard.userProfile = principal.user.profile
        personalCard.securedCard = securedPersonalCard

        personalCardRepository.save(personalCard)

        securedPersonalCard.personalCard = personalCard
        securedPersonalCard.personalCard.userProfile = principal.user.profile
        securedPersonalCard.paymentCardType = personalCard.paymentCardType

        securedCardRepository.save(securedPersonalCard)
    }

    @PreAuthorize("#tuturing.isPersonal(#personalCard)")
    fun add(personalCard: PersonalCardEntity, cardNumber: String, cvv: String, expirationMonth: Long, expirationYear: Long): Boolean {
        if (personalCard.stripePaymentMethod.isEmpty()) {
            val stripePaymentMethod = stripeClient.createPaymentMethod(cardNumber, cvv, expirationMonth, expirationYear)
            personalCard.stripePaymentMethod = stripePaymentMethod.id
            personalCard.cardNumber = stripePaymentMethod.card.last4
            personalCard.cardNetwork = CardNetwork.valueOf(stripePaymentMethod.card.brand.toUpperCase())

            /*
            Assign the expirationDate's month and year values to the card.expYear & card.expMonth values of the stripe payment method object
            The PersonalCardEntity will update the expiration date's value to the last date of the expiration month
            */

            personalCard.expirationDate = LocalDate.of(stripePaymentMethod.card.expYear.toInt(), stripePaymentMethod.card.expMonth.toInt(), 1)
        }
        return if (this.validate(personalCard)) {
            this.save(personalCard, cardNumber, cvv, expirationMonth, expirationYear)
        } else false
    }

    fun getSecuredCard(card: PersonalCardEntity): SecuredCardDto {
        // TODO: implement card decryption for checkout/payment requests
        val encryptedData = card.securedCard.encryptedData
        val decryptedJson = kmsClient.decrypt(encryptedData)

        return jacksonObjectMapper().readValue(decryptedJson, SecuredCardDto::class.java)
    }

    fun findByCardNumber(userProfile: UserProfileEntity, number: String): PersonalCardEntity? {
        val lastFour = number.takeLast(PersonalCardEntity.SAFE_TO_SAVE_DIGITS_LENGTH)

        val cards = personalCardRepository
            .findAllByUserProfileAndCardNumberOrderByExpirationDateDesc(userProfile, lastFour)

        return if (cards.size == 1) {
            cards.first()
        } else if (number.length > PersonalCardEntity.SAFE_TO_SAVE_DIGITS_LENGTH) {
            val decryptedCards = cards.map { card ->
                Pair(card, getSecuredCard(card))
            }.filter { pair ->
                pair.second.cardNumber == number
            }.map { pair ->
                pair.first
            }

            if (decryptedCards.size > 0) {
                decryptedCards.first()
            } else {
                null
            }
        } else {
            null
        }
    }
}
