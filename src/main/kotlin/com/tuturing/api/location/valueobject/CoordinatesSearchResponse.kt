package com.tuturing.api.location.valueobject

data class CoordinatesSearchResponse(
    var name: String?,
    var coordinates: Coordinates?,
    var type: LocationType?,
    var region: String?,
    var country: String?
) {
    constructor() : this("", Coordinates(), null, null, null)
}
