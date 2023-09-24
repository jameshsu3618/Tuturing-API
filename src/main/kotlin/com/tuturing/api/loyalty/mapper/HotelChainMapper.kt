package com.tuturing.api.loyalty.mapper

import com.tuturing.api.loyalty.dto.HotelChainDto
import com.tuturing.api.loyalty.entity.HotelChainEntity
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface HotelChainMapper {
    @Mappings()
    fun convertToDto(entity: HotelChainEntity): HotelChainDto
}
