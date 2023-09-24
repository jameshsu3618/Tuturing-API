package com.tuturing.api.location.mapper

import com.tuturing.api.location.dto.CoordinatesSearchResponseDto
import com.tuturing.api.location.valueobject.CoordinatesSearchResponse
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
abstract class CoordinatesSearchResponseMapper {
    abstract fun convertToDto(coordinatesSearchResponse: CoordinatesSearchResponse): CoordinatesSearchResponseDto
}
