package com.tuturing.api.location.mapper

import com.tuturing.api.location.dto.AirportDto
import com.tuturing.api.location.entity.AirportEntity
import com.tuturing.api.location.valueobject.Airport
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface AirportMapper {
    @Mappings()
    fun convertToDto(airport: Airport): AirportDto

    @Mappings()
    fun convertToDtoList(airports: List<Airport>): List<AirportDto>

    @Mappings(
        Mapping(target = "fullName", source = "name"),
        Mapping(target = "id", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true),
        Mapping(target = "timezone", ignore = true)
    )
    fun convertToEntity(dto: Airport): AirportEntity
}
