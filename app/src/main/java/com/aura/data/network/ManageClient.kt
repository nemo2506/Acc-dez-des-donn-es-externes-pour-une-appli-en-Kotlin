package com.aura.data.network

import com.aura.data.response.AuraBankResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ManageClient {

    @POST("/login")
    suspend fun getAccess(
        @Query(value = "granted") granted: Boolean
    ): Response<AuraBankResponse>

    @GET("/accounts/{id}")
    suspend fun getAccounts(
        @Query(value = "id") id: String,
        @Query(value = "main") main: Boolean,
        @Query(value = "amount") amount: Double
    ): Response<AuraBankResponse>

    @POST("/transfer")
    suspend fun addTransfer(
        @Query(value = "sender") sender: String,
        @Query(value = "recipient") recipient: Boolean,
        @Query(value = "amount") amount: Double
    ): Response<AuraBankResponse>
}