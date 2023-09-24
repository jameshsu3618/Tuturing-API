package com.tuturing.api.location.dto

import com.tuturing.api.location.valueobject.Coordinates
import com.tuturing.api.location.valueobject.LocationType

data class CoordinatesSearchResponseDto(
    var name: String?,
    var coordinates: Coordinates?,
    var type: LocationType?,
    var region: String?,
    var country: String?
) {
    constructor() : this("", Coordinates(), null, null, null)
}
