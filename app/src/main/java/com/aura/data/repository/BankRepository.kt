package com.aura.data.repository

import android.content.Context
import com.aura.R
import com.aura.data.network.ManageClient
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class BankRepository @Inject constructor(
    private val dataService: ManageClient,
    @ApplicationContext private val context: Context
) {

    suspend fun fetchLoginAccess(id: String, password: String): Result<LoginReportModel> {
        return try {
            Result.Loading
            val user = User(id, password)
            val result = dataService.fetchAccess(user)
            // Convert the response to domain model using the toDomainModel() function
            val model = result.body()?.toDomainModel() ?: throw Exception(context.getString(R.string.loading_failed))
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }
}
