package com.tuturing.api.location.configuration

import com.tuturing.api.location.valueobject.MultiAirportCities
import com.tuturing.api.location.valueobject.MultiAirportCity
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.BufferedReader
import java.math.BigDecimal
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
class AirportConfiguration(
    @Value("classpath:json/airport/mac.json") private val macJsonResource: Resource
) {
    @Bean("multiAirportCities")
    fun multiAirportCities(): MultiAirportCities? {
        val bigDecimalAdapter = object {
            @FromJson
            fun fromJson(string: String) = BigDecimal(string)

            @ToJson
            fun toJson(value: BigDecimal) = value.toString()
        }

        val content = macJsonResource.inputStream.bufferedReader().use(BufferedReader::readText)
        val moshi = Moshi.Builder()
            .add(bigDecimalAdapter)
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(MultiAirportCities::class.java)
        val macs = adapter.fromJson(content)

        return macs
    }

    @Bean("macToAirportsMap")
    fun macToAirportsMap(): Map<String, MultiAirportCity> {
        val macs = multiAirportCities()

        val map = macs?.cities?.map { it.iataCode to it }?.toMap()
        return map ?: emptyMap()
    }

    @Bean("airportToMac")
    fun airportToMac(): Map<String, MultiAirportCity> {
        val macs = multiAirportCities()

        val map = macs?.cities?.map { city ->
            city.airports?.filter { airport ->
                airport.iataCode != null
            }?.map { airport ->
                Pair(airport.iataCode!!, city)
            } ?: listOf()
        }?.flatten()?.distinctBy { it.first }?.toMap()

        return map ?: emptyMap()
    }
}
