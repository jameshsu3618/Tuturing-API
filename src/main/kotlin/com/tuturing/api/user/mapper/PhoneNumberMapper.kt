package com.tuturing.api.user.mapper

import com.tuturing.api.location.repository.CountryRepository
import com.tuturing.api.user.dto.resource.PhoneNumberDto
import com.tuturing.api.user.entity.PhoneNumberEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring")
abstract class PhoneNumberMapper() {
    @Autowired protected lateinit var countryRepository: CountryRepository

    @Mappings(
        Mapping(target = "countryCode", source = "country.isoCodeAlpha2"),
        Mapping(target = "phoneNumberCountryCode", source = "country.phoneNumberCountryCode")
    )
    abstract fun convertToDto(entity: PhoneNumberEntity): PhoneNumberDto

    @Mappings(
        Mapping(target = "country", expression = "java(this.getCountryRepository().findByIsoCodeAlpha2(dto.getCountryCode()))"),
        Mapping(target = "id", ignore = true),
        Mapping(target = "userProfile", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: PhoneNumberDto, @MappingTarget entity: PhoneNumberEntity): PhoneNumberEntity
}
