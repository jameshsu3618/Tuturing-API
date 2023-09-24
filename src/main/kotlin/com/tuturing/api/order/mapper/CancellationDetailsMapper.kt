package com.tuturing.api.order.mapper

import com.tuturing.api.order.dto.CancellationDetailsDto
import com.tuturing.api.order.valueobject.CancellationDetails
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface CancellationDetailsMapper {
    @Mappings()
    fun convertToDto(entity: CancellationDetails): CancellationDetailsDto
}
