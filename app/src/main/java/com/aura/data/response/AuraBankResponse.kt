package com.aura.data.response

import com.aura.domain.model.BankModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuraBankResponse(
    @Json(name = "list")
    val forecasts: List<ForecastResponse>,
) {

    @JsonClass(generateAdapter = true)
    data class ForecastResponse(
        @Json(name = "dt")
        val time: Int,
        @Json(name = "main")
        val temperature: TemperatureResponse,
        @Json(name = "weather")
        val weather: List<WeatherResponse>,
    ) {


        @JsonClass(generateAdapter = true)
        data class TemperatureResponse(
            @Json(name = "temp")
            val temp: Double,
        )


        @JsonClass(generateAdapter = true)
        data class WeatherResponse(
            @Json(name = "id")
            val id: Int,
            @Json(name = "main")
            val title: String,
            @Json(name = "description")
            val description: String
        )
    }

    fun toDomainModel(): List<BankModel> {
        return forecasts.map { forecast ->

            // Check if the sky is clear (IDs 800 to 802 indicate clear sky conditions)
            val isClearSky = forecast.weather.isNotEmpty() && forecast.weather[0].id in 800..802

            // Convert temperature to Celsius
            val temperatureCelsius = (forecast.temperature.temp - 273.15).toInt()


            BankModel(
                isGoodForStargazing = isClearSky,
                temperatureCelsius = temperatureCelsius,
                weatherTitle = forecast.weather[0].title,
                weatherDescription = forecast.weather[0].description
            )
        }
    }
}