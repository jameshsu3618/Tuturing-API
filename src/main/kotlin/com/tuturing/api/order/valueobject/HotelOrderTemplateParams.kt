package com.tuturing.api.order.valueobject

import com.fasterxml.jackson.annotation.JsonProperty

data class HotelOrderTemplateParams(
    val subject: String,
    val cdn_url: String,
    @get:JsonProperty("is_confirmation") val is_confirmation: Boolean,
    @get:JsonProperty("is_cancellation") val is_cancellation: Boolean,
    @get:JsonProperty("is_modification") val is_modification: Boolean,
    val cancellation: Cancellation,
    val first_name: String,
    val hotel_checkin: String,
    val hotel_checkout: String,
    val city: String?,
    val subdivision_code: String?,
    val hotel_name: String?,
    val image_href: String?,
    val street_address: String?,
    val postal_code: String?,
    val country_code: String?,
    val phone: String?,
    val amenity: List<AmenityIcon>?,
    val bed_group_description: String?,
    val checkin_time: String?,
    val checkout_time: String?,
    val trip_id: String?,
    val order_number: String?,
    val confirmation_number: String?,
    val booked_on: String?,
    val booker_name: String?,
    val traveler_name: String?,
    val policy_name: String?,
    val quantity: String?,
    val hotel_information: String?,
    val charge: Charge
)
