package com.tuturing.api.order.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.location.domain.AirportService
import com.tuturing.api.loyalty.service.AirlineService
import com.tuturing.api.order.entity.OrderEntity
import com.tuturing.api.order.repository.OrderRepository
import com.tuturing.api.order.valueobject.*
import com.tuturing.api.policy.service.BasicPolicyService
import com.tuturing.api.sabre.valueobject.FlightNotificationType
import com.tuturing.api.sabre.valueobject.email.flightbooking.*
import com.tuturing.api.shared.iterable.sumByBigDecimal
import com.tuturing.api.shared.jms.EmailAddress
import com.tuturing.api.shared.jms.SendEmailMessage
import com.tuturing.api.shared.jms.SendEmailProducer
import com.tuturing.api.shared.valueobject.Money
import com.tuturing.api.user.entity.UserEntity
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class OrderService(
    @Autowired private val orderRepository: OrderRepository,
    @Autowired private val basicPolicyService: BasicPolicyService,
    @Autowired private val orderHotelEmailParams: OrderHotelEmailParams,
    @Autowired private val orderFlightTemplateParams: OrderFlightTemplateParams,
    @Autowired private val sendEmailProducer: SendEmailProducer,
    @Autowired private val airportService: AirportService,
    @Autowired private val airlineService: AirlineService
) {
    fun save(order: OrderEntity) {
        orderRepository.save(order)
    }

    fun findByPublicId(publicId: String): OrderEntity? {
        return orderRepository.findByPublicId(publicId)
    }

    fun findById(id: UUID): OrderEntity? {
        return orderRepository.findByIdOrNull(id)
    }

    fun amountTotalNet(order: OrderEntity, currency: String = CURRENCY_USD): Money {
        return Money(
            order.transactions.filter {
                it.currency == currency
            }.map { it.amountTotal }.sumByBigDecimal { it },
            currency
        )
    }

    fun amountSubtotal(order: OrderEntity, transactionType: TransactionType, currency: String = CURRENCY_USD): Money {
        return Money(
            order.transactions.filter {
                it.currency == currency && it.type == transactionType
            }.map { it.amountSubtotal }.sumByBigDecimal { it },
            currency
        )
    }

    fun amountTotal(order: OrderEntity, transactionType: TransactionType, currency: String = CURRENCY_USD): Money {
        return Money(
            order.transactions.filter {
                it.currency == currency && it.type == transactionType
            }.map { it.amountTotal }.sumByBigDecimal { it },
            currency
        )
    }

    fun amountTax(order: OrderEntity, transactionType: TransactionType, currency: String = CURRENCY_USD): Money {
        return Money(
            order.transactions.filter {
                it.currency == currency && it.type == transactionType
            }.map { it.amountTax }.sumByBigDecimal { it },
            currency
        )
    }

    fun amountFee(order: OrderEntity, transactionType: TransactionType, currency: String = CURRENCY_USD): Money {
        return Money(
            order.transactions.filter {
                it.currency == currency && it.type == transactionType
            }.map { it.amountFee }.sumByBigDecimal { it },
            currency
        )
    }

    fun getFlightOrderTemplateParams(order: OrderEntity, recipient: UserEntity?, notificationType: FlightNotificationType): FlightBookingEmailTemplate? {
        val lastSegmentIndex = order.bookedFlight?.itinerary?.flightSegments?.size?.let { it } ?: 0

        return FlightBookingEmailTemplate(
            subject = if (notificationType == FlightNotificationType.CONFIRMATION) {
                orderFlightTemplateParams.orderFlightSubjectComplete
            } else { orderFlightTemplateParams.orderFlightSubjectModification },
            cdnUrl = orderFlightTemplateParams.cdnBaseUrl,
            firstName = recipient?.profile?.firstName ?: "",
            showConfirmationMessage = notificationType == FlightNotificationType.CONFIRMATION,
            showCancellationMessage = notificationType == FlightNotificationType.CANCELLATION,
            showModificationMessage = notificationType == FlightNotificationType.UPDATE,
            orderNumber = order.externalOrderId,
            tripId = order.publicIdWithDashes(),
            confirmationNumber = order.externalConfirmationId,
            bookedOn = formatSlashDate(order.orderedAt),
            bookerName = order.purchaser.profile.fullName(),
            travelerName = order.bookedFlight?.traveler?.fullName,
            policyName = order.bookedFlight?.traveler?.let { traveler ->
                basicPolicyService.findByCompanyAndRoleForReceipt(
                    traveler.company,
                    traveler.user.role
                )?.let { it.roleFormatted() + " Policy" }
            },
            charge = getCharge(order),
            cancellation = if (notificationType == FlightNotificationType.CANCELLATION) {
                getCancellation(order)
            } else { null },
            flightSegments = order.bookedFlight?.itinerary?.flightSegments?.mapIndexed { segmentIndex, flightSegment ->
                FlightSegment(
                    destination = flightSegment.flightLegs.last().let { lastLeg ->
                        val airport = airportService.findByIataCode(lastLeg.arrivalAirport)
                        FlightSegmentDestination(
                            city = airport?.city ?: "",
                            subdivisionCode = airport?.region,
                            country = airport?.country
                        )
                    },
                    flightLegs = flightSegment.flightLegs.mapIndexed { legIndex, flightLeg ->
                        val departureAirportEntity = airportService.findByIataCode(flightLeg.departureAirport)
                        val arrivalAirportEntity = airportService.findByIataCode(flightLeg.arrivalAirport)
                        val marketingAirline = flightLeg.marketingAirlineCode?.let { airlineService.findByIataCode(it) }

                        FlightLeg(
                            departureAirport = Airport(
                                code = flightLeg.departureAirport,
                                city = departureAirportEntity?.city ?: "",
                                subdivisionCode = departureAirportEntity?.region,
                                country = departureAirportEntity?.country
                            ),
                            arrivalAirport = Airport(
                                code = flightLeg.arrivalAirport,
                                city = arrivalAirportEntity?.city ?: "",
                                subdivisionCode = arrivalAirportEntity?.region,
                                country = arrivalAirportEntity?.country
                            ),
                            cdnUrl = orderFlightTemplateParams.cdnBaseUrl,
                            airlineMarketingIata = flightLeg.marketingAirlineCode ?: "",
                            airlineOperatingFlightNumber = flightLeg.operatingAirlineFlightNumber ?: "",
                            departureDate = formatLongDate(flightLeg.departureAt),
                            departureTime = formatTime(flightLeg.departureAt),
                            arrivalTime = formatTime(flightLeg.arrivalAt),
                            flightDuration = flightLeg.durationFormatted(),
                            airlineMarketingName = marketingAirline?.shortName ?: "",
                            cabinName = flightLeg.cabinName() ?: "",
                            bookingCode = flightLeg.bookingCode ?: "",
                            hasLayover = flightSegment.flightLegs.size > 1 && legIndex != flightSegment.flightLegs.size - 1,
                            layoverDuration = flightLeg.layoverDurationFormatted(),
                            isNextDayArrival = flightLeg.isNextDayArrival(),
                            isNextDayDeparture = flightLeg.isNextDayDeparture()
                        )
                    },
                    totalDuration = flightSegment.durationFormatted(),
                    isLastSegment = lastSegmentIndex == segmentIndex
                )
            } ?: listOf()
        )
    }

    fun getHotelOrderTemplateParams(
        order: OrderEntity,
        isCancellation: Boolean = false,
        isModification: Boolean = false,
        isConfirmation: Boolean = false
    ): HotelOrderTemplateParams? {
        return order.bookedHotel?.let { bookedHotel ->
            bookedHotel.traveler.let { traveler ->
                val policy = basicPolicyService.findByCompanyAndRoleForReceipt(traveler.company, traveler.user.role)
                val amenity = getAmenityIcons(bookedHotel.amenities)
                HotelOrderTemplateParams(
                    subject = if (isConfirmation) {
                        orderHotelEmailParams.subjectConfirmation
                    } else {
                        orderHotelEmailParams.subjectModification
                    },
                    cdn_url = orderHotelEmailParams.cdnBaseUrl,
                    first_name = traveler.firstName,
                    hotel_checkin = bookedHotel.checkIn?.let { formatLongDate(it) } ?: "",
                    hotel_checkout = bookedHotel.checkOut?.let { formatLongDate(it) } ?: "",
                    city = bookedHotel.city,
                    subdivision_code = bookedHotel.subdivision,
                    hotel_name = bookedHotel.hotelName,
                    image_href = bookedHotel.hotelImage ?: bookedHotel.roomImage,
                    street_address = bookedHotel.addressOne,
                    postal_code = bookedHotel.postalCode,
                    country_code = bookedHotel.country,
                    phone = bookedHotel.phone,
                    amenity = amenity,
                    bed_group_description = bookedHotel.bedGroup,
                    checkin_time = bookedHotel.checkIn?.let { "${formatShortDate(it)}, ${formatTime(it)}" },
                    checkout_time = bookedHotel.checkOut?.let { "${formatShortDate(it)}, ${formatTime(it)}" },
                    trip_id = order.publicIdWithDashes(),
                    order_number = order.externalOrderId,
                    booked_on = formatSlashDate(order.orderedAt),
                    booker_name = "${order.purchaserFirstName} ${order.purchaserLastName}",
                    traveler_name = traveler.fullName,
                    policy_name = policy?.roleFormatted(),
                    quantity = ChronoUnit.DAYS.between(bookedHotel.checkIn, bookedHotel.checkOut).toString(),
                    hotel_information = strRegEx.replace(bookedHotel.checkinInstruction ?: "", " "),
                    confirmation_number = order.externalConfirmationId,
                    is_cancellation = isCancellation,
                    is_modification = isModification,
                    is_confirmation = isConfirmation,
                    cancellation = getCancellation(order),
                    charge = getCharge(order)
                )
            }
        }
    }

    fun getCancellation(order: OrderEntity): Cancellation {
        return Cancellation(
            cancelledOn = formatSlashDate(order.cancelledAt),
            cancelledBy = order.cancelledBy?.let { getCancelledByText(it) } ?: "",
            amountPenalty = amountTotalNet(order).amount.setScale(2).toString(),
            amountRefund = amountTotal(order, TransactionType.REFUND).amount.abs().setScale(2).toString()
        )
    }

    fun getCharge(order: OrderEntity): Charge {
        return Charge(
            amountBase = amountSubtotal(order, TransactionType.CHARGE).amount.setScale(2).toString(),
            amountTax = amountTax(order, TransactionType.CHARGE).amount.setScale(2).toString(),
            amountTotal = amountTotal(order, TransactionType.CHARGE).amount.setScale(2).toString(),
            paymentCard = when {
                null != order.personalCard -> {
                    "${order.personalCard!!.cardNetwork.toString().toLowerCase().capitalize()} ${order.personalCard!!.cardNumber}"
                }
                null != order.companyCard -> {
                    "${order.companyCard!!.cardNetwork.toString().toLowerCase().capitalize()} ${order.companyCard!!.cardNumber}"
                }
                else -> ""
            }
        )
    }

    private fun getCancelledByText(cancelledByType: CancelledByType): String {
        return when (cancelledByType) {
            CancelledByType.USER -> ""
            CancelledByType.FRAUD -> "by our inventory provider"
            CancelledByType.AGENT -> "through our support team"
            CancelledByType.SUPPLIER -> "by the hotel"
        }
    }

    private fun getAmenityIcons(amenities: String?): List<AmenityIcon>? {
        return amenities?.let {
            amenities.split(",").map { it.trim() }.map { propertyAmenity ->
                val formattedPropertyAmenity = if (propertyAmenity == HOTEL_FREE_SELF_PARKING) HOTEL_FREE_PARKING else propertyAmenity
                AmenityIcon(
                    cdn_url = orderHotelEmailParams.cdnBaseUrl,
                    icon = formattedPropertyAmenity.toLowerCase(),
                    type = formattedPropertyAmenity.split("_").joinToString(" ") { it.toLowerCase().capitalize() }
                )
            }
        }
    }

    private fun <T> formatTime(dateTime: T): String {
        return when (dateTime) {
            is ZonedDateTime -> dateTime.format(timeFormatter).replace("AM", "a").replace("PM", "p")
            is LocalDateTime -> dateTime.format(timeFormatter).replace("AM", "a").replace("PM", "p")
            else -> ""
        }
    }

    private fun <T> formatLongDate(dateTime: T): String {
        return when (dateTime) {
            is ZonedDateTime -> dateTime.format(longDateFormatter)
            is LocalDateTime -> dateTime.format(longDateFormatter)
            else -> ""
        }
    }

    private fun <T> formatShortDate(dateTime: T): String {
        return when (dateTime) {
            is ZonedDateTime -> dateTime.format(shortDateFormatter)
            is LocalDateTime -> dateTime.format(shortDateFormatter)
            else -> ""
        }
    }

    private fun <T> formatSlashDate(dateTime: T): String {
        return when (dateTime) {
            is ZonedDateTime -> dateTime.format(orderDateFormatter)
            is LocalDateTime -> dateTime.format(orderDateFormatter)
            else -> ""
        }
    }

    fun sendOrderModificationEmail(order: OrderEntity) {
        sendOrderEmail(order, isModification = true, isCancellation = false, isConfirmation = false)
    }

    fun sendOrderCancellationEmail(order: OrderEntity) {
        sendOrderEmail(order, isModification = false, isCancellation = true, isConfirmation = false)
    }

    fun sendOrderConfirmationEmail(order: OrderEntity) {
        sendOrderEmail(order, isModification = false, isCancellation = false, isConfirmation = true)
    }

    private fun sendOrderEmail(order: OrderEntity, isModification: Boolean, isCancellation: Boolean, isConfirmation: Boolean) {
        when {
            null != order.bookedFlight -> {
                // TODO: Flight template refactor
            }
            null != order.bookedHotel -> {
                getHotelOrderTemplateParams(
                    order,
                    isModification = isModification,
                    isCancellation = isCancellation,
                    isConfirmation = isConfirmation
                )?.let { hotelTemplateParams ->
                    listOfNotNull(order.bookedHotel?.traveler, order.purchaser.profile).distinctBy { it.id }.forEach {
                        sendEmailProducer.sendEmail(
                            SendEmailMessage(
                                orderHotelEmailParams.sender,
                                listOf(EmailAddress(it.user.profile.fullName, it.user.email)),
                                if (isConfirmation) orderHotelEmailParams.subjectConfirmation else orderHotelEmailParams.subjectModification,
                                orderHotelEmailParams.emailTemplate,
                                jacksonObjectMapper().writeValueAsString(hotelTemplateParams)
                            )
                        )
                    }
                }
            }
        }
    }

    fun refresh(order: OrderEntity) {
        orderRepository.refresh(order)
    }

    companion object {
        private val orderDateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")
        private val longDateFormatter = DateTimeFormatter.ofPattern("E, MMM d, yyyy")
        private val shortDateFormatter = DateTimeFormatter.ofPattern("E, MMM d")
        private val timeFormatter = DateTimeFormatter.ofPattern("h:mma")
        private const val HOTEL_FREE_SELF_PARKING = "FREE_SELF_PARKING"
        private const val HOTEL_FREE_PARKING = "FREE_PARKING"
        private const val CURRENCY_USD = "USD"
        private val strRegEx = """(<[^>]*>)""".toRegex()
    }
}
