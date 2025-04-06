package com.aura.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aura.R
import com.aura.data.network.ManageClient
import com.aura.domain.model.AccountsReportModel
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class BankRepository @Inject constructor(
    private val dataService: ManageClient,
    @ApplicationContext private val context: Context
) {
    private var _currentId = MutableLiveData<String>()
    val currentId: LiveData<String> get() = _currentId
    private var _currentBalance = MutableLiveData<Double?>()
    val currentBalance: MutableLiveData<Double?> get() = _currentBalance

    suspend fun fetchLoginAccess(id: String, password: String): Result<LoginReportModel> {
        return try {
            Result.Loading
            _currentId.value = id
            val user = User(id, password)
            val result = dataService.fetchAccess(user)
            // Convert the response to domain model using the toDomainModel() function
            val model = result.body()?.toDomainModel(context)
                ?: throw Exception(context.getString(R.string.login_failed))
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }

    suspend fun fetchAccounts(): Result<AccountsReportModel> {
        return try {
            Result.Loading
            val result = dataService.fetchAccounts(currentId.toString())
            // Convert the response to domain model using the toDomainModel() function
            val model = result.body()?.toDomainModel(context)
                ?: throw Exception(context.getString(R.string.login_failed))
            _currentBalance.value = model.balance
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }
}
