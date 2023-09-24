package com.tuturing.api.shared.dto

import com.tuturing.api.shared.valueobject.CrudOperation
import com.tuturing.api.shared.valueobject.ProfileType
import java.util.*

data class ProfileOperationDto(
    var id: UUID,
    var type: ProfileType,
    var operation: CrudOperation
)
