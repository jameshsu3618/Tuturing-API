package com.tuturing.api.location.mapper

import com.tuturing.api.location.domain.CountryService
import com.tuturing.api.location.dto.CountryDto
import com.tuturing.api.location.dto.SubdivisionDto
import com.tuturing.api.location.entity.SubdivisionEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring", uses = [CountryDto::class])
abstract class SubdivisionMapper() {
    @Autowired protected lateinit var countryService: CountryService

    @Mappings()
    abstract fun convertToDto(entity: SubdivisionEntity): SubdivisionDto

    @Mappings(
            Mapping(target = "country", expression = "java(this.getCountryService().findByCountryCode(dto.getCountryCode()))"),
            Mapping(target = "createdAt", ignore = true),
            Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: SubdivisionDto, @MappingTarget entity: SubdivisionEntity): SubdivisionEntity
}
