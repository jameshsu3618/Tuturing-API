package com.tuturing.api.location.mapper

import com.tuturing.api.location.valueobject.Airport
import com.tuturing.api.location.valueobject.Coordinates
import com.tuturing.api.location.valueobject.CoordinatesSearchResponse
import java.math.BigDecimal
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
abstract class AutocompleteMapper {
    @Mappings(
            Mapping(target = "coordinates", expression = "java(coordinates(airport.getLongitude(), airport.getLatitude()))")
    )
    abstract fun convertToResponse(airport: Airport): CoordinatesSearchResponse

    fun coordinates(lon: BigDecimal, lat: BigDecimal): Coordinates {
        return Coordinates(lon.toDouble(), lat.toDouble())
    }
}
