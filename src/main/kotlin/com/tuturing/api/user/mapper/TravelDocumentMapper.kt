package com.tuturing.api.user.mapper

import com.tuturing.api.location.repository.CountryRepository
import com.tuturing.api.user.dto.resource.TravelDocumentDto
import com.tuturing.api.user.entity.TravelDocumentEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring")
abstract class TravelDocumentMapper {
    @Autowired
    protected lateinit var countryRepository: CountryRepository

    @Mappings(
        Mapping(target = "nationalityCountryCode", source = "nationality.isoCodeAlpha2"),
        Mapping(target = "issuingCountryCode", source = "issuingCountry.isoCodeAlpha2")
    )
    abstract fun convertToDto(entity: TravelDocumentEntity): TravelDocumentDto

    @Mappings(
        Mapping(target = "nationality", expression = "java(this.getCountryRepository().findByIsoCodeAlpha2(dto.getNationalityCountryCode()))"),
        Mapping(target = "issuingCountry", expression = "java(this.getCountryRepository().findByIsoCodeAlpha2(dto.getIssuingCountryCode()))"),
        Mapping(target = "id", ignore = true),
        Mapping(target = "userProfile", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: TravelDocumentDto, @MappingTarget entity: TravelDocumentEntity): TravelDocumentEntity
}
