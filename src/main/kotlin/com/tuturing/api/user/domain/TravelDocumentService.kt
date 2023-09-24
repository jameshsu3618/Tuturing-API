package com.tuturing.api.user.domain

import com.tuturing.api.user.entity.TravelDocumentEntity
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.repository.TravelDocumentRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class TravelDocumentService(
    @Autowired private val travelDocumentRepository: TravelDocumentRepository
) {
    @PreAuthorize("#tuturing.isPersonal(#travelDocument)")
    fun save(travelDocument: TravelDocumentEntity) {
        travelDocumentRepository.save(travelDocument)
    }

    @PostAuthorize("#tuturing.isPersonal(returnObject)")
    fun findById(id: UUID): TravelDocumentEntity? {
        return travelDocumentRepository.findByIdOrNull(id)
    }

    @PreAuthorize("#tuturing.isPrincipal(#userProfile)")
    fun findByProfileId(userProfile: UserProfileEntity): MutableIterable<TravelDocumentEntity> {
        return travelDocumentRepository.findByUserProfileId(userProfile.id)
    }

    @PreAuthorize("#tuturing.isPersonal(#travelDocument)")
    fun delete(travelDocument: TravelDocumentEntity) {
        travelDocumentRepository.delete(travelDocument)
    }
}
