package com.aura.data.network

import com.aura.data.response.AccountBankResponse
import com.aura.data.response.LoginBankResponse
import com.aura.data.response.TransferBankResponse
import com.aura.domain.model.Transfer
import com.aura.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ManageClient {

    @POST("/login")
    suspend fun fetchAccess(
        @Body request: User
    ): Response<LoginBankResponse>

    @GET("/accounts/{id}")
    suspend fun fetchBalance(
        @Path("id") id: String
    ): Response<List<AccountBankResponse>>


    @POST("/transfer")
    suspend fun fetchTransfer(
        @Body request: Transfer
    ): Response<TransferBankResponse>
}