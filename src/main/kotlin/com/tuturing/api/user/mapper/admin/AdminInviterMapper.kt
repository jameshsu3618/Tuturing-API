package com.tuturing.api.user.mapper.admin

import com.tuturing.api.user.dto.resource.admin.InviterDto
import com.tuturing.api.user.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [AdminUserProfileMapper::class])
interface AdminInviterMapper {
    @Mappings()
    fun convertToDto(entity: UserEntity): InviterDto
}
