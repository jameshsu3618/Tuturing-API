package com.tuturing.api.user.mapper

import com.tuturing.api.user.dto.resource.UserProfileDto
import com.tuturing.api.user.entity.UserProfileEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [PhoneNumberMapper::class])
interface UserProfileMapper {
    @Mappings()
    fun convertToDto(entity: UserProfileEntity): UserProfileDto

    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "user", ignore = true),
        Mapping(target = "company", ignore = true),
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
