package com.tuturing.api.user.controller

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.TravelDocumentService
import com.tuturing.api.user.dto.resource.TravelDocumentDto
import com.tuturing.api.user.entity.TravelDocumentEntity
import com.tuturing.api.user.mapper.TravelDocumentMapper
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/travel-documents")
@Validated
class TravelDocumentController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val travelDocumentService: TravelDocumentService,
    @Autowired private val travelDocumentMapper: TravelDocumentMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getTravelDocuments(): ResponseEntity<List<TravelDocumentDto>> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val userProfile = principal.user.profile

        val travelDocuments = travelDocumentService.findByProfileId(userProfile)

        val dto = travelDocuments.map { travelDocument -> travelDocumentMapper.convertToDto(travelDocument) }

        return ResponseEntity.ok(dto)
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("")
    fun addTravelDocument(@RequestBody travelDocumentDto: TravelDocumentDto): ResponseEntity<TravelDocumentDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val userProfile = principal.user.profile

        var travelDocumentEntity = TravelDocumentEntity()

        try {
            travelDocumentEntity = travelDocumentMapper.convertToEntity(travelDocumentDto, travelDocumentEntity)
        } catch (iae: IllegalArgumentException) {
            // could not map, most likely invalid request, e.g. non-existent country code
            return ResponseEntity.badRequest().build()
        }

        travelDocumentEntity.userProfile = userProfile
        travelDocumentService.save(travelDocumentEntity)

        return ResponseEntity.ok(travelDocumentMapper.convertToDto(travelDocumentEntity))
    }

    @PreAuthorize("#oauth2.isUser()")
    @PutMapping("/{id}")
    fun updateTravelDocument(@PathVariable id: UUID, @RequestBody travelDocumentDto: TravelDocumentDto): ResponseEntity<TravelDocumentDto> {
        val travelDocumentEntity = travelDocumentService.findById(id)

        return travelDocumentEntity?.let {
            val entity = travelDocumentMapper.convertToEntity(travelDocumentDto, it)
            travelDocumentService.save(entity)
            ResponseEntity.ok(travelDocumentMapper.convertToDto(it))
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @DeleteMapping("/{id}")
    fun deleteTravelDocument(@PathVariable id: UUID): ResponseEntity<Any> {
        val travelDocumentEntity = travelDocumentService.findById(id)

        return travelDocumentEntity?.let {
            travelDocumentService.delete(it)
            ResponseEntity.ok().build<Any>()
        } ?: return ResponseEntity.notFound().build()
    }
}
