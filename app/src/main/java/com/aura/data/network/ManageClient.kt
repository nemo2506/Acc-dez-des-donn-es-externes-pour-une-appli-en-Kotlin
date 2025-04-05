package com.aura.data.network

import com.aura.data.response.LoginBankResponse
import com.aura.domain.model.User
import okhttp3.Request
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ManageClient {

    @POST("/login")
    suspend fun fetchAccess(
        @Body request: User
    ): Response<LoginBankResponse>
}