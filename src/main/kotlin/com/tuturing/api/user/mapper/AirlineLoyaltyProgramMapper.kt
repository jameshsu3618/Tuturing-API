package com.tuturing.api.user.mapper

import com.tuturing.api.loyalty.mapper.AirlineMapper
import com.tuturing.api.loyalty.service.AirlineService
import com.tuturing.api.user.dto.resource.AirlineLoyaltyProgramDto
import com.tuturing.api.user.entity.AirlineLoyaltyProgramEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring", uses = [AirlineMapper::class])
abstract class AirlineLoyaltyProgramMapper {
    @Autowired
    protected lateinit var airlineService: AirlineService

    abstract fun convertToDto(entity: AirlineLoyaltyProgramEntity): AirlineLoyaltyProgramDto

    @Mappings(
        Mapping(target = "airline", expression = "java(this.getAirlineService().findById(dto.getAirline().getId()))"),
        Mapping(target = "userProfile", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: AirlineLoyaltyProgramDto, @MappingTarget entity: AirlineLoyaltyProgramEntity): AirlineLoyaltyProgramEntity
}
