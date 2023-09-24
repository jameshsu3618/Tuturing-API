package com.tuturing.api.user.controller.superadmin

import com.tuturing.api.itinerary.dto.TripsDto
import com.tuturing.api.itinerary.mapper.FlightTripMapper
import com.tuturing.api.itinerary.mapper.HotelTripMapper
import com.tuturing.api.itinerary.service.TripService
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.UserProfileService
import com.tuturing.api.user.domain.UserSearchService
import com.tuturing.api.user.dto.resource.UserDto
import com.tuturing.api.user.mapper.UserMapper
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/users")
@Validated
class AdminUsersController(
    @Autowired val userSearchService: UserSearchService,
    @Autowired val userProfileService: UserProfileService,
    @Autowired val tripService: TripService,
    @Autowired val userMapper: UserMapper,
    @Autowired val hotelTripMapper: HotelTripMapper,
    @Autowired val flightTripMapper: FlightTripMapper,
    @Autowired private val authenticationFacade: AuthenticationFacade
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.hasScope('superadmin.user:read')")
    @GetMapping("")
    fun findAll(
        @RequestParam(name = "id", required = false) id: UUID?,
        @RequestParam(name = "email", required = false) email: String?,
        @RequestParam(name = "firstName", required = false) firstName: String?,
        @RequestParam(name = "lastName", required = false) lastName: String?
    ): List<UserDto> {
        val principal = authenticationFacade.authentication.principal as String
        val details = authenticationFacade.authentication.details as OAuth2AuthenticationDetails

        logger.debug("Principal: {} details: {}", principal::class.java, details::class.java)

        return userSearchService.findAll(id, email?.toLowerCase(), firstName, lastName).map {
            userMapper.convertToDto(it)
        }
    }

    @PreAuthorize("#oauth2.hasScope('superadmin.user:read')")
    @GetMapping("/{id}/itineraries")
    fun findItineraries(@PathVariable id: UUID): TripsDto {
        val userProfile = userProfileService.findAllByUserIds(listOf(id)).first()
        val hotels = tripService.findHotelsByUserProfile(userProfile)
        val flights = tripService.findFlightsByUserProfile(userProfile)

        logger.debug("Hotels {}", hotels)
        logger.debug("Flights {}", flights)

        return TripsDto(
            hotels = hotels.map { hotelTripMapper.convertToDto(it) },
            flights = flights.map { flightTripMapper.convertToDto(it) }
        )
    }
}
