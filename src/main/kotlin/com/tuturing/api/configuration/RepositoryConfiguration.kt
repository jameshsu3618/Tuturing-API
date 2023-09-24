package com.tuturing.api.configuration

import com.tuturing.api.ApiApplication
import com.tuturing.api.shared.repository.CustomRepositoryImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(
    basePackageClasses = [ApiApplication::class],
    repositoryBaseClass = CustomRepositoryImpl::class
)
class RepositoryConfiguration
