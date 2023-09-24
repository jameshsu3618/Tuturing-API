package com.tuturing.api.loyalty.mapper

import com.tuturing.api.loyalty.dto.AirlineDto
import com.tuturing.api.loyalty.entity.AirlineEntity
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface AirlineMapper {
    @Mappings()
    fun convertToDto(entity: AirlineEntity): AirlineDto
}
