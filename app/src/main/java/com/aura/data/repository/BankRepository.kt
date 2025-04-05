package com.aura.data.repository

import android.util.Log
import com.aura.data.network.ManageClient
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.User


class BankRepository(private val dataService: ManageClient) {

    suspend fun fetchLoginAccess(id: String, password: String): Result<LoginReportModel> {
        return try {
            Result.Loading
            val user = User(id, password)
            val result = dataService.fetchAccess(user)
            Log.d("MARC", "fetchLoginAccess: $result")
            // Convert the response to domain model using the toDomainModel() function
            val model = result.body()?.toDomainModel() ?: throw Exception("Problème serveur")
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }
}
