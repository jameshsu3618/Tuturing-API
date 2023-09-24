package com.tuturing.api.user.mapper.admin

import com.tuturing.api.user.dto.resource.admin.UserProfileDto
import com.tuturing.api.user.entity.UserProfileEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface AdminUserProfileMapper {
    @Mappings()
    fun convertToDto(entity: UserProfileEntity): UserProfileDto

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "user", ignore = true),
        Mapping(target = "company", ignore = true),
        Mapping(target = "middleName", ignore = true),
        Mapping(target = "prefix", ignore = true),
        Mapping(target = "suffix", ignore = true),
        Mapping(target = "birthDate", ignore = true),
        Mapping(target = "gender", ignore = true),
        Mapping(target = "knownTravelerNumber", ignore = true),
        Mapping(target = "redressNumber", ignore = true),
        Mapping(target = "travelDocuments", ignore = true),
        Mapping(target = "phoneNumbers", ignore = true),
        Mapping(target = "personalCards", ignore = true),
        Mapping(target = "airlineLoyaltyPrograms", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true),
        Mapping(target = "department", ignore = true)
    )
    fun convertToEntity(dto: UserProfileDto, @MappingTarget profile: UserProfileEntity): UserProfileEntity
}
