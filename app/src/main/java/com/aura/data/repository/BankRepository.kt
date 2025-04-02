package com.aura.data.repository

import com.aura.data.network.ManageClient
import com.aura.domain.model.BankModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class BankRepository(private val dataService: ManageClient) {
    private val API_KEY = "30e9d0b7be54cf1b79271c3813510c1c"


    fun fetchForecastData(lat: Double, lng: Double): Flow<Result<List<BankModel>>> =
        flow {
            emit(Result.Loading)
            val result = dataService.getWeatherByPosition(
                latitude = lat,
                longitude = lng,
                apiKey = API_KEY
            )
            val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
            emit(Result.Success(model))
        }.catch { error ->
            emit(Result.Failure(error.message))
        }
}
