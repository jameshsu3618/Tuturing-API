package com.tuturing.api.configuration

import com.amazonaws.util.EC2MetadataUtils
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class MetricsConfiguration(
    @Value("\${spring.profiles.active}") val activeProfiles: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun timedAspect(registry: MeterRegistry): TimedAspect {
        return TimedAspect(registry)
    }

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry: MeterRegistry ->
            // other interesting tags
            // Tag.of("ami_id",            EC2MetadataUtils.getAmiId() ?: UNDEFINED_TAG_VALUE),
            // Tag.of("local_hostname",    EC2MetadataUtils.getLocalHostName() ?: UNDEFINED_TAG_VALUE),
            // Tag.of("java_vm_version",   System.getProperty("java.vm.version")),
            // Tag.of("java_vm_name",      System.getProperty("java.vm.name"))

            val tags = listOf<Tag>(
                    Tag.of("region", EC2MetadataUtils.getInstanceInfo()?.region ?: UNDEFINED_TAG_VALUE),
                    Tag.of("instance_id", EC2MetadataUtils.getInstanceId() ?: UNDEFINED_TAG_VALUE),
                    Tag.of("availability_zone", EC2MetadataUtils.getAvailabilityZone() ?: UNDEFINED_TAG_VALUE),
                    Tag.of("instance_region", EC2MetadataUtils.getEC2InstanceRegion() ?: UNDEFINED_TAG_VALUE),
                    Tag.of("instance_type", EC2MetadataUtils.getInstanceInfo()?.instanceType ?: UNDEFINED_TAG_VALUE),
                    Tag.of("spring_profiles", activeProfiles)
            )

            registry.config().commonTags(tags)
        }
    }

    companion object {
        private val UNDEFINED_TAG_VALUE: String = "undefined"
    }
}
