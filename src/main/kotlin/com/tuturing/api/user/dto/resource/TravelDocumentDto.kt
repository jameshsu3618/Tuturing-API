package com.tuturing.api.user.dto.resource

import java.time.LocalDate
import java.util.UUID

data class TravelDocumentDto(
    var id: UUID?,
    var number: String,
    var nationalityCountryCode: String,
    var issuingCountryCode: String,
    var issueDate: LocalDate,
    var expirationDate: LocalDate
) {
    constructor() : this(null, "", "", "", LocalDate.now(), LocalDate.now())
}
