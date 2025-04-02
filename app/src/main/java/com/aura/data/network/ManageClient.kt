package com.aura.data.network

import com.aura.data.response.AuraBankResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ManageClient {
    @GET("/data/2.5/forecast")
    suspend fun getWeatherByPosition(
        @Query(value = "lat") latitude: Double,
        @Query(value = "lon") longitude: Double,
        @Query(value = "appid") apiKey: String
    ): Response<AuraBankResponse>
}