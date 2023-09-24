package com.tuturing.api.paymentmethod.repository

import com.tuturing.api.paymentmethod.entity.SecuredCardEntity
import java.util.*
import org.springframework.data.repository.CrudRepository

interface SecuredCardRepository : CrudRepository<SecuredCardEntity, UUID> {
    fun save(securedCompanyCard: SecuredCardEntity): SecuredCardEntity
}
