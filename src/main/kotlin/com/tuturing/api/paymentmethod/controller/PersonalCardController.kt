package com.tuturing.api.paymentmethod.controller

import com.tuturing.api.paymentmethod.domain.PersonalCardService
import com.tuturing.api.paymentmethod.dto.resource.PaymentCardDto
import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import com.tuturing.api.paymentmethod.mapper.PersonalCardMapper
import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/paymentmethod/user")
class PersonalCardController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val personalCardService: PersonalCardService,
    @Autowired private val personalCardMapper: PersonalCardMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    @PreAuthorize("#oauth2.isUser()")
//    @GetMapping("/personal-cards")
//    fun getPersonalCards(): ResponseEntity<List<PaymentCardDto>> {
//        val principal = authenticationFacade.authentication.principal as CustomUserDetails
//
//        val userProfile = principal.user.profile
//
//        val personalCards = personalCardService.findByUserProfile(userProfile, isDeleted = false, sort = Sort.by(Sort.Direction.DESC, "auditable.createdAt"))
//
//        val dto = personalCards.map { personalCard -> personalCardMapper.convertToDto(personalCard) }
//
//        return ResponseEntity.ok(dto)
//    }
//
//    @PreAuthorize("#oauth2.isUser()")
//    @PostMapping("/personal-cards")
//    fun addPersonalCard(@RequestBody personalCardDto: PaymentCardDto): ResponseEntity<PaymentCardDto> {
//        val principal = authenticationFacade.authentication.principal as CustomUserDetails
//        val userProfile = principal.user.profile
//
//        var personalCard = PersonalCardEntity()
//
//        return try {
//            personalCard = personalCardMapper.convertToEntity(personalCardDto, personalCard)
//            personalCard.userProfile = userProfile
//
//            personalCardService.add(personalCard, personalCardDto.secureCardDetails.cardNumber, personalCardDto.secureCardDetails.cvv, personalCardDto.secureCardDetails.expirationMonth, personalCardDto.secureCardDetails.expirationYear)
//
//            ResponseEntity.ok(personalCardMapper.convertToDto(personalCard))
//        } catch (iae: IllegalArgumentException) {
//            // could not map, most likely due to invalid request, e.g. incorrect formatting of card details
//            ResponseEntity.badRequest().build()
//        }
//    }
//
//    @PreAuthorize("#oauth2.isUser()")
//    @DeleteMapping("/personal-cards/{id}")
//    fun softDeletePersonalCard(@PathVariable id: UUID): ResponseEntity<Any> {
//        val personalCard = personalCardService.findById(id)
//
//        return personalCard?.let {
//            if (personalCardService.softDelete(it)) {
//                ResponseEntity.ok().build<Any>()
//            } else { ResponseEntity.notFound().build() }
//        } ?: return ResponseEntity.notFound().build()
//    }
//
//    @PreAuthorize("#oauth2.isUser()")
//    @PutMapping("/personal-cards/{id}")
//    fun updateCompanyCard(@PathVariable id: UUID, @RequestBody personalCardDto: PaymentCardDto): ResponseEntity<PaymentCardDto> {
//
//        val principal = authenticationFacade.authentication.principal as CustomUserDetails
//        val userProfile = principal.user.profile
//
//        var oldPersonalCard = personalCardService.findById(id)
//        var newPersonalCard = PersonalCardEntity()
//
//        return try {
//            newPersonalCard = personalCardMapper.convertToEntity(personalCardDto, newPersonalCard)
//            newPersonalCard.userProfile = userProfile
//
//            oldPersonalCard?.let {
//                personalCardService.update(oldPersonalCard, newPersonalCard, personalCardDto.secureCardDetails.cardNumber, personalCardDto.secureCardDetails.cvv, personalCardDto.secureCardDetails.expirationMonth, personalCardDto.secureCardDetails.expirationYear)
//
//                ResponseEntity.ok(personalCardMapper.convertToDto(newPersonalCard))
//            } ?: ResponseEntity.notFound().build()
//        } catch (iae: IllegalArgumentException) {
//            // could not map, most likely due to invalid request, e.g. incorrect formatting of card details
//            ResponseEntity.badRequest().build()
//        }
//    }
}
