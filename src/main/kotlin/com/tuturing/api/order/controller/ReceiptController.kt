package com.tuturing.api.order.controller

import com.tuturing.api.order.dto.ReceiptMetadataDto
import com.tuturing.api.order.service.OrderService
import com.tuturing.api.order.service.ReceiptService
import com.tuturing.api.order.valueobject.HotelOrderTemplateParams
import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import java.util.*
import javax.validation.constraints.NotEmpty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class ReceiptController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val orderService: OrderService,
    @Autowired private val receiptService: ReceiptService,
    @NotEmpty @Value("\${tuturing.api-base-url}") private val apiBaseUrl: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{id}/receipt")
    @ResponseBody
    fun getReceipt(@PathVariable id: UUID, @RequestParam token: String): ResponseEntity<ByteArray> {
        if (!receiptService.verifyReceiptToken(token, id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return try {
            orderService.findById(id)?.let { order ->
                receiptService.getReceipt(order)?.let {
                    val headers = HttpHeaders()
                    headers.contentType = MediaType.APPLICATION_PDF
                    val filename = "${order.publicId}_receipt.pdf"
                    headers.contentDisposition = ContentDisposition.builder("inline").filename(filename).build()
                    headers.cacheControl = "must-revalidate, post-check=0, pre-check=0"
                    ResponseEntity(it.readBytes(), headers, HttpStatus.OK)
                } ?: ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
            } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build() // return when order not found
        } catch (e: Exception) {
            logger.debug(e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() // return when order not found
        }
    }

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("/{orderId}/receipt/metadata")
    fun getReceiptMetadata(@PathVariable orderId: UUID): ResponseEntity<ReceiptMetadataDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val token = receiptService.getReceiptToken(orderId, principal.user)
        val url = "$apiBaseUrl/orders/$orderId/receipt?token=$token"
        return ResponseEntity.ok(ReceiptMetadataDto(receiptUrl = url))
    }

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("/{id}/receiptparams")
    // TODO: For debugging, will be removed
    fun getReceiptParams(@PathVariable id: UUID): ResponseEntity<HotelOrderTemplateParams> {
        return orderService.findById(id)?.let { order ->
            orderService.getHotelOrderTemplateParams(order)?.let {
                ResponseEntity.ok(it)
            }
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("/{id}/sendreceipt")
    // TODO: For debugging, will be removed
    fun sendReceipt(@PathVariable id: UUID): ResponseEntity<String> {
        return orderService.findById(id)?.let { order ->
            receiptService.sendBookingCompleteEmail(order)
            ResponseEntity.ok("Sent Receipt")
        } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
}
