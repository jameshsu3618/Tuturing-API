package com.tuturing.api.location.mapper

import com.tuturing.api.location.domain.SubdivisionService
import com.tuturing.api.location.dto.CountryDto
import com.tuturing.api.location.dto.SubdivisionDto
import com.tuturing.api.location.entity.CountryEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring", uses = [SubdivisionDto::class])
abstract class CountryMapper() {
    @Autowired protected lateinit var subdivisionService: SubdivisionService

    @Mappings(Mapping(target = "shortName", ignore = true))
    abstract fun convertToDto(entity: CountryEntity): CountryDto

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "subdivisions", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: CountryDto, @MappingTarget entity: CountryEntity): CountryEntity

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "fullName", ignore = true),
        Mapping(target = "subdivisions", ignore = true),
        Mapping(target = "isoCodeAlpha2", expression = "java(dto.getCountryCode())"),
        Mapping(target = "isoCodeAlpha3", ignore = true),
        Mapping(target = "phoneNumberCountryCode", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: SubdivisionDto, @MappingTarget entity: CountryEntity): CountryEntity
}
