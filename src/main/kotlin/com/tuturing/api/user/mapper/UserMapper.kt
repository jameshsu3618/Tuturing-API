package com.tuturing.api.user.mapper

import com.tuturing.api.company.mapper.CompanyMapper
import com.tuturing.api.user.dto.resource.UserDto
import com.tuturing.api.user.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [
    UserProfileMapper::class,
    CompanyMapper::class
])
interface UserMapper {
    @Mappings()
    fun convertToDto(entity: UserEntity): UserDto
}
