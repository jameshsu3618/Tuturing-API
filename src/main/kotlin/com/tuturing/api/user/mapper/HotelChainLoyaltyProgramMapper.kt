package com.tuturing.api.user.mapper

import com.tuturing.api.loyalty.mapper.HotelChainMapper
import com.tuturing.api.loyalty.service.HotelChainService
import com.tuturing.api.user.dto.resource.HotelChainLoyaltyProgramDto
import com.tuturing.api.user.entity.HotelChainLoyaltyProgramEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring", uses = [HotelChainMapper::class])
abstract class HotelChainLoyaltyProgramMapper {
    @Autowired
    protected lateinit var hotelChainService: HotelChainService

    abstract fun convertToDto(entity: HotelChainLoyaltyProgramEntity): HotelChainLoyaltyProgramDto

    @Mappings(
        Mapping(target = "hotelChain", expression = "java(this.getHotelChainService().findById(dto.getHotelChain().getId()))"),
        Mapping(target = "userProfile", ignore = true),
        Mapping(target = "createdAt", ignore = true),
        Mapping(target = "updatedAt", ignore = true)
    )
    abstract fun convertToEntity(dto: HotelChainLoyaltyProgramDto, @MappingTarget entity: HotelChainLoyaltyProgramEntity): HotelChainLoyaltyProgramEntity
}
