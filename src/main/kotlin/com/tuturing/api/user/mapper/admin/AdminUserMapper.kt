package com.tuturing.api.user.mapper.admin

import com.tuturing.api.policy.entity.PolicyEntity
import com.tuturing.api.policy.mapper.PolicyMapper
import com.tuturing.api.policy.service.PolicyService
import com.tuturing.api.user.dto.resource.admin.UserDto
import com.tuturing.api.user.dto.resource.admin.UserPolicyDto
import com.tuturing.api.user.dto.resource.admin.UserRegistrationDto
import com.tuturing.api.user.dto.resource.admin.UserUpdateDto
import com.tuturing.api.user.dto.resource.admin.UserWithInviterDto
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.valueobject.UserUpdateParams
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring", uses = [
    AdminUserProfileMapper::class,
    AdminInviterMapper::class,
    PolicyMapper::class
])
abstract class AdminUserMapper {
    @Autowired protected lateinit var policyService: PolicyService

    @Mappings()
    abstract fun convertToDto(entity: UserEntity): UserDto

    @Mappings()
    abstract fun convertToDtoWithInviter(entity: UserEntity): UserWithInviterDto

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "password", ignore = true),
        Mapping(target = "company", ignore = true),
        Mapping(target = "inviter", ignore = true),
        Mapping(target = "invitedAt", ignore = true),
        Mapping(target = "status", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true),
        Mapping(target = "department", ignore = true),
        Mapping(target = "policy", ignore = true)
    )
    abstract fun convertToEntity(dto: UserDto, @MappingTarget entity: UserEntity): UserEntity

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "profile", ignore = true), // when registering, there is no profile yet
        Mapping(target = "password", ignore = true),
        Mapping(target = "company", ignore = true),
        Mapping(target = "inviter", ignore = true),
        Mapping(target = "invitedAt", ignore = true),
        Mapping(target = "status", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true),
        Mapping(target = "department", ignore = true),
        Mapping(target = "policy", ignore = true)
    )
    abstract fun convertToEntity(dto: UserRegistrationDto, @MappingTarget entity: UserEntity): UserEntity

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "email", ignore = true),
        Mapping(target = "password", ignore = true),
        Mapping(target = "company", ignore = true),
        Mapping(target = "inviter", ignore = true),
        Mapping(target = "invitedAt", ignore = true),
        Mapping(target = "status", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true),
        Mapping(target = "department", ignore = true),
        Mapping(target = "policy", ignore = true)
    )
    abstract fun convertToEntity(dto: UserUpdateDto, @MappingTarget entity: UserEntity): UserEntity

    @Mappings(
        Mapping(target = "policy", expression = "java(this.findPolicy(dto.getPolicy()))"),
        Mapping(target = "profile.firstName", source = "dto.profile.firstName"),
        Mapping(target = "profile.lastName", source = "dto.profile.lastName")
    )
    abstract fun convertToUserUpdateParams(dto: UserUpdateDto): UserUpdateParams

    protected fun findPolicy(dto: UserPolicyDto?): PolicyEntity? {
        return dto?.let { departmentPolicyDto ->
            departmentPolicyDto.id?.let {
                policyService.findByIdAuthorized(it)
            }
        }
    }
}
