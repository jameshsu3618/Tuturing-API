package com.tuturing.api.paymentmethod.mapper

import com.tuturing.api.location.domain.CountryService
import com.tuturing.api.location.domain.SubdivisionService
import com.tuturing.api.paymentmethod.dto.resource.PaymentCardAddressDto
import com.tuturing.api.paymentmethod.dto.resource.PaymentCardDto
import com.tuturing.api.paymentmethod.dto.resource.SecuredCardDto
import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring", uses = [PaymentCardAddressDto::class, SecuredCardDto::class])
abstract class PersonalCardMapper() {
    @Autowired protected lateinit var countryService: CountryService
    @Autowired protected lateinit var subdivisionService: SubdivisionService

    @Mappings(
            Mapping(target = "billingAddress.addressOne", source = "addressOne"),
            Mapping(target = "billingAddress.addressTwo", source = "addressTwo"),
            Mapping(target = "billingAddress.city", source = "city"),
            Mapping(target = "billingAddress.subdivisionCode", source = "subdivision.isoSubdivisionCode"),
            Mapping(target = "billingAddress.countryCode", source = "country.isoCodeAlpha2"),
            Mapping(target = "billingAddress.zipCode", source = "zipCode"),
            Mapping(target = "secureCardDetails.cardNumber", source = "cardNumber"),
            Mapping(target = "secureCardDetails.expirationMonth", expression = "java(personalCardEntity.getExpirationDate().getMonthValue())"),
            Mapping(target = "secureCardDetails.expirationYear", expression = "java(personalCardEntity.getExpirationDate().getYear())")
    )
    abstract fun convertToDto(entity: PersonalCardEntity): PaymentCardDto
    @Mappings(
            Mapping(target = "id", ignore = true),
            Mapping(target = "deleted", ignore = true),
            Mapping(target = "userProfile", ignore = true),
            Mapping(target = "securedCard", ignore = true),
            Mapping(target = "stripePaymentMethod", ignore = true),
            Mapping(target = "deletedBy", ignore = true),
            Mapping(target = "cardNetwork", ignore = true),
            Mapping(target = "expirationDate", ignore = true),
            Mapping(target = "cardNumber", expression = "java(dto.getSecureCardDetails().getCardNumber())"),
            Mapping(target = "addressOne", expression = "java(dto.getBillingAddress().getAddressOne())"),
            Mapping(target = "addressTwo", expression = "java(dto.getBillingAddress().getAddressTwo())"),
            Mapping(target = "city", expression = "java(dto.getBillingAddress().getCity())"),
            Mapping(target = "zipCode", expression = "java(dto.getBillingAddress().getZipCode())"),
            Mapping(target = "country", expression = "java(this.getCountryService().findByCountryCode(dto.getBillingAddress().getCountryCode()))"),
            Mapping(target = "subdivision", expression = "java(this.getSubdivisionService().findBySubdivisionCode(dto.getBillingAddress().getSubdivisionCode()))"),
            Mapping(target = "deletedAt", ignore = true),
            Mapping(target = "createdAt", ignore = true),
            Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: PaymentCardDto, @MappingTarget entity: PersonalCardEntity): PersonalCardEntity
}
