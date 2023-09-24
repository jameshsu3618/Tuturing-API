package com.tuturing.api.configuration

import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfiguration(
    @NotEmpty @Value("\${spring.redis.host}") private val hostName: String,
    @NotEmpty @Value("\${spring.redis.port}") private val port: Int
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory? {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(hostName, port))
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Map<String, Any>>? {
        val template = RedisTemplate<String, Map<String, Any>>()
        template.setConnectionFactory(redisConnectionFactory()!!)
        template.setDefaultSerializer(GenericJackson2JsonRedisSerializer())
        return template
    }
//    @Bean
//    fun hotelCheckoutRedisTemplate(): RedisTemplate<String, Map<String, HotelCheckoutInfo>>? {
//        return redisTemplate() as RedisTemplate<String, Map<String, HotelCheckoutInfo>>
//    }
//
//    @Bean
//    fun hotelContentRedisTemplate(): RedisTemplate<String, Map<String, PropertyContent>>? {
//        return redisTemplate() as RedisTemplate<String, Map<String, PropertyContent>>
//    }
//
//    @Bean
//    fun hotelPricingRedisTemplate(): RedisTemplate<String, Map<String, PropertyAvailability>>? {
//        return redisTemplate() as RedisTemplate<String, Map<String, PropertyAvailability>>
//    }
}
