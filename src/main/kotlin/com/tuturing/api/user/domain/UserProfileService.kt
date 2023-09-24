package com.tuturing.api.user.domain

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.repository.UserProfileRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val userProfileRepository: UserProfileRepository,
    @Autowired private val userService: UserService
) {
    fun refresh(profile: UserProfileEntity) {
        userProfileRepository.refresh(profile)
    }

    fun findById(id: UUID): UserProfileEntity? {
        return userProfileRepository.findByIdOrNull(id)
    }

    // don't annotate with authorization, too generic use case
    fun findAllByIds(ids: Iterable<UUID>): List<UserProfileEntity> {
        return userProfileRepository.findAllById(ids)
    }

    // don't annotate with authorization, too generic use case
    fun findAllByUserIds(ids: Iterable<UUID>): List<UserProfileEntity> {
        return userService.findAllByIds(ids).map { it.profile }
    }

    // don't annotate with authorization, too generic use case
    fun save(profile: UserProfileEntity) {
        userProfileRepository.save(profile)
    }

    @PreAuthorize("#tuturing.isPrincipal(#profile) or (#tuturing.isAnAdmin() and #tuturing.isCorporate(#profile))")
    fun update(profile: UserProfileEntity) {
        userProfileRepository.save(profile)
    }

    @PreAuthorize("#oauth2.isUser()")
    fun maskedKnownTravelerNumber(profile: UserProfileEntity): String? {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails
        val user = principal.user

        return if (0 == profile.id?.compareTo(user.profile.id)) {
            profile.knownTravelerNumber
        } else {
            profile.maskedKnownTravelerNumber()
        }
    }

    @PreAuthorize("#oauth2.isUser()")
    fun maskedRedressNumber(profile: UserProfileEntity): String? {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails
        val user = principal.user

        return if (0 == profile.id?.compareTo(user.profile.id)) {
            profile.redressNumber
        } else {
            profile.maskedRedressNumber()
        }
    }
}
