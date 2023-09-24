package com.tuturing.api.security.repository

import com.tuturing.api.security.entity.OAuthClient
import java.util.UUID
import org.springframework.data.repository.CrudRepository

interface OAuthClientRepository : CrudRepository<OAuthClient, UUID>
