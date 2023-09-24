package com.tuturing.api.location.mapper

import com.tuturing.api.location.dto.CoordinatesSearchRequestDto
import com.tuturing.api.location.valueobject.CoordinatesSearchParams
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
abstract class CoordinatesSearchRequestMapper {
    abstract fun convertToParams(dto: CoordinatesSearchRequestDto): CoordinatesSearchParams
}
