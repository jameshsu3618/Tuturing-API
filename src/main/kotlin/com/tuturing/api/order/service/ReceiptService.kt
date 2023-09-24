package com.tuturing.api.order.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.tuturing.api.location.domain.AirportService
import com.tuturing.api.loyalty.service.AirlineService
import com.tuturing.api.order.entity.OrderEntity
import com.tuturing.api.order.valueobject.*
import com.tuturing.api.policy.service.BasicPolicyService
import com.tuturing.api.sabre.valueobject.FlightNotificationType
import com.tuturing.api.shared.jms.EmailAddress
import com.tuturing.api.shared.jms.SendEmailMessage
import com.tuturing.api.shared.jms.SendEmailProducer
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.entity.UserProfileEntity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.parsers.DocumentBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.xhtmlrenderer.pdf.ITextRenderer

@Service
class ReceiptService(
    @Autowired private val handlebars: Handlebars,
    @Autowired private val documentBuilder: DocumentBuilder?,
    @Autowired private val basicPolicyService: BasicPolicyService,
    @Autowired private val airportService: AirportService,
    @Autowired private val airlineService: AirlineService,
    @Autowired private val orderFlightTemplateParams: OrderFlightTemplateParams,
    @Autowired private val orderHotelEmailParams: OrderHotelEmailParams,
    @Autowired private val receiptParams: ReceiptParams,
    @Autowired private val sendEmailProducer: SendEmailProducer,
    @Autowired private val orderService: OrderService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getReceipt(order: OrderEntity): ByteArrayInputStream? {
        return when {
            null != order.bookedFlight -> {
                val notificationType = when (order.status) {
                    OrderStatus.CANCELED -> FlightNotificationType.CANCELLATION
                    else -> FlightNotificationType.CONFIRMATION
                }
                val templateParams = orderService.getFlightOrderTemplateParams(order, null, notificationType)
                val template = handlebars.compile(orderFlightTemplateParams.orderFlightTemplatePdf).apply(templateParams) ?: ""
                return getPdfByteArrayStream(template)
            }
            null != order.bookedHotel -> {
                val isCancellation = order.status == OrderStatus.CANCELED
                val isConfirmation = order.status == OrderStatus.COMPLETE
                val templateParams = orderService.getHotelOrderTemplateParams(
                    order,
                    isCancellation,
                    isModification = false,
                    isConfirmation = isConfirmation
                )
                val template = handlebars.compile(orderHotelEmailParams.pdfTemplate).apply(templateParams) ?: ""
                return getPdfByteArrayStream(template)
            }
            else -> null
        }
    }

    fun getReceiptToken(orderId: UUID, user: UserEntity): String {
        val algorithm = Algorithm.RSA256(receiptParams.jwtPublicKey, receiptParams.jwtPrivateKey)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, receiptParams.receiptExpirationTime)

        return JWT.create()
            .withIssuer(receiptParams.jwtIssuer)
            .withExpiresAt(calendar.time)
            .withClaim("companyId", user.company.id.toString())
            .withClaim("userId", user.id.toString())
            .withClaim("orderId", orderId.toString())
            .sign(algorithm)
    }

    fun verifyReceiptToken(token: String, orderId: UUID): Boolean {
        val jwt = runCatching {
            val algorithm = Algorithm.RSA256(receiptParams.jwtPublicKey, receiptParams.jwtPrivateKey)
            val verifier = JWT.require(algorithm)
                .withIssuer(receiptParams.jwtIssuer)
                .build()
            verifier.verify(token)
        }

        jwt.fold({
//            val companyId = UUID.fromString(it.getClaim("companyId").asString())
//            val userId = UUID.fromString(it.getClaim("userId").asString())
            val claimOrderId = UUID.fromString(it.getClaim("orderId").asString())
            return claimOrderId == orderId
        }, {
            return false
        })
    }

    private fun getPdfByteArrayStream(template: String): ByteArrayInputStream? {
        return documentBuilder?.parse(ByteArrayInputStream(template.toByteArray(Charsets.UTF_8)))?.let {
            // Convert to PDF
            val renderer = ITextRenderer()
            val output = ByteArrayOutputStream()
            renderer.setDocument(it, null)
            renderer.layout()
            renderer.createPDF(output)
            renderer.finishPDF()
            ByteArrayInputStream(output.toByteArray())
        }
    }

    fun sendBookingCompleteEmail(order: OrderEntity) {
        logger.debug("Sending booking complete email, flight {} hotel {}", order.bookedFlight, order.bookedHotel)

        when {
            null != order.bookedFlight -> {
                logger.debug("Sending booking complete email for a flight")
                listOfNotNull(order.bookedFlight?.traveler, order.purchaser.profile).distinctBy { it.id }.forEach { userProfile ->
                    logger.debug("Sending booking complete email for a flight to a user profile {}", userProfile.id.toString())
                    getFlightReceiptParams(order, userProfile)?.let { flightReceiptParams ->
                        logger.debug("Sending booking complete email with params {}", flightReceiptParams)

                        sendEmailProducer.sendEmail(SendEmailMessage(
                                orderFlightTemplateParams.orderFlightSender,
                                listOf(EmailAddress(userProfile.fullName, userProfile.user.email)),
                                orderFlightTemplateParams.orderFlightSubjectComplete,
                                orderFlightTemplateParams.orderFlightTemplate,
                                jacksonObjectMapper().writeValueAsString(flightReceiptParams)
                        ))
                    }
                }
            }
            null != order.bookedHotel -> {
                orderService.sendOrderConfirmationEmail(order)
            }
        }
    }

    fun getFlightReceiptParams(order: OrderEntity, recipient: UserProfileEntity?): FlightReceiptParams? {
        return order.bookedFlight?.traveler?.let { traveler ->
            val policy = basicPolicyService.findByCompanyAndRoleForReceipt(traveler.company, traveler.user.role)
            val paymentCardName = order.companyCard?.cardNickname ?: order.personalCard?.cardNickname
            val paymentCardNetwork = order.companyCard?.cardNetwork ?: order.personalCard?.cardNetwork
            val bookedOnFormat = DateTimeFormatter.ofPattern(BOOKED_ON_FORMAT)

            order.bookedFlight?.itinerary?.flightSegments?.let { flightSegments ->
                val receiptSegments = flightSegments.mapIndexed { segmentIdx, flightSegment ->
                    val receiptLegs = flightSegment.flightLegs.mapIndexed { legIdx, flightLeg ->
                        val departureAirport = airportService.findByIataCode(flightLeg.departureAirport)
                        val arrivalAirport = airportService.findByIataCode(flightLeg.arrivalAirport)
                        val marketingAirline = flightLeg.marketingAirlineCode?.let { airlineService.findByIataCode(it) }
                        val operatingAirline = flightLeg.operatingAirlineCode?.let { airlineService.findByIataCode(it) }
                        FlightReceiptLeg(
                            departure_date = formatLongDate(flightLeg.departureAt),
                            departure_time = formatTime(flightLeg.departureAt),
                            arrival_date = formatLongDate(flightLeg.arrivalAt),
                            arrival_time = formatTime(flightLeg.arrivalAt),
                            is_next_day_arrival = flightLeg.isNextDayArrival(),
                            is_next_day_departure = flightLeg.isNextDayDeparture(),
                            departure_airport = FlightReceiptAirport(
                                code = departureAirport?.iataCode,
                                city = departureAirport?.city,
                                subdivision_code = departureAirport?.region,
                                country = departureAirport?.country
                            ),
                            arrival_airport = FlightReceiptAirport(
                                code = arrivalAirport?.iataCode,
                                city = arrivalAirport?.city,
                                subdivision_code = arrivalAirport?.region,
                                country = arrivalAirport?.country
                            ),
                            airline_marketing_name = marketingAirline?.shortName,
                            airline_marketing_iata = flightLeg.marketingAirlineCode,
                            airline_operating_name = operatingAirline?.shortName,
                            airline_operating_iata = flightLeg.operatingAirlineCode,
                            airline_operating_flight_number = flightLeg.operatingAirlineFlightNumber,
                            flight_duration = flightLeg.durationFormatted(),
                            is_layover = legIdx > 0,
                            layover_duration = flightLeg.layoverDurationFormatted(),
                            cabin_name = flightLeg.cabinName(),
                            booking_code = flightLeg.bookingCode
                        )
                    }
                    FlightReceiptSegment(
                        is_return = segmentIdx == 1,
                        destination = receiptLegs.last().arrival_airport,
                        total_duration = flightSegment.durationFormatted(),
                        flight_legs = receiptLegs
                    )
                }

                FlightReceiptParams(
                    first_name = recipient?.firstName ?: traveler.firstName,
                    trip_id = order.publicIdWithDashes(),
                    order_number = order.externalOrderId,
                    confirmation_number = order.externalConfirmationId,
                    booked_on = order.orderedAt?.format(bookedOnFormat),
                    booker_name = "${order.purchaserFirstName} ${order.purchaserLastName}",
                    traveler_name = "${traveler.firstName} ${traveler.lastName}",
                    policy_name = policy?.roleFormatted(),
                    amount_base = order.amountSubtotal.setScale(2)?.toString(),
                    amount_tax = order.amountTax.setScale(2)?.toString(),
                    amount_total = order.amountTotal.setScale(2)?.toString(),
                    payment_card = paymentCardName,
                    payment_card_network = paymentCardNetwork?.toString()?.capitalize(),
                    flight_segments = receiptSegments,
                    cdn_url = orderFlightTemplateParams.cdnBaseUrl,
                    flight_information = FLIGHT_INFORMATION_PLACEHOLDER
                )
            }
        }
    }

    private fun <T> formatTime(dateTime: T): String {
        val timeFormat = DateTimeFormatter.ofPattern(TIME_FORMAT)
        return when (dateTime) {
            is ZonedDateTime -> dateTime.format(timeFormat).replace("AM", "a").replace("PM", "p")
            is LocalDateTime -> dateTime.format(timeFormat).replace("AM", "a").replace("PM", "p")
            else -> ""
        }
    }

    private fun <T> formatLongDate(dateTime: T): String {
        val dateFormat = DateTimeFormatter.ofPattern(LONG_DATE_FORMAT)
        return when (dateTime) {
            is ZonedDateTime -> dateTime.format(dateFormat)
            is LocalDateTime -> dateTime.format(dateFormat)
            else -> ""
        }
    }

    companion object {
        private const val BOOKED_ON_FORMAT = "M/d/yyyy"
        private const val LONG_DATE_FORMAT = "E, MMM d, yyyy"
        private const val TIME_FORMAT = "h:mma"
        // Creating this as template var in the event we improve this to pass specific flight information into template
        private const val FLIGHT_INFORMATION_PLACEHOLDER = "Fees and restrictions may apply. Rules and restrictions are not imposed by tuturing."
    }
}
