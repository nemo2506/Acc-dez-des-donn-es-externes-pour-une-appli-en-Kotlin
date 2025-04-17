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

/**
 * Interface that defines the API endpoints used for managing user actions such as
 * authentication, balance inquiry, and money transfer.
 */
interface ManageClient {

    /**
     * Sends a login request to the API using the provided [User] credentials.
     *
     * @param request The user's login data containing ID and password.
     * @return [Response] containing [LoginBankResponse] with login result.
     */
    @POST("/login")
    suspend fun fetchAccess(
        @Body request: User
    ): Response<LoginBankResponse>

    /**
     * Fetches the list of bank accounts for the user with the given [id].
     *
     * @param id The unique identifier of the user.
     * @return [Response] containing a list of [AccountBankResponse] representing the user's accounts.
     */
    @GET("/accounts/{id}")
    suspend fun fetchBalance(
        @Path("id") id: String
    ): Response<List<AccountBankResponse>>

    /**
     * Sends a transfer request to the API using the provided [Transfer] data.
     *
     * @param request The transfer details including sender, recipient, and amount.
     * @return [Response] containing [TransferBankResponse] with the result of the transfer.
     */
    @POST("/transfer")
    suspend fun fetchTransfer(
        @Body request: Transfer
    ): Response<TransferBankResponse>
}
