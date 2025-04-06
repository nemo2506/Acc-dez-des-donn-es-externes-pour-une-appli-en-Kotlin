package com.aura.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.aura.R
import com.aura.data.network.ManageClient
import com.aura.data.response.AccountBankResponse
import com.aura.domain.model.AccountsReportModel
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class BankRepository @Inject constructor(
    private val dataService: ManageClient,
    @ApplicationContext private val context: Context
) {
    private var _currentId = MutableLiveData<String>()
    private val currentId: String get() = _currentId.value.toString()
    private var _currentBalance = MutableLiveData<Double?>()
    val currentBalance: Double? get() = _currentBalance.value?.toDouble()

    suspend fun fetchLoginAccess(id: String, password: String): Result<LoginReportModel> {
        return try {
            Result.Loading
            _currentId.value = id
            val user = User(id, password)
            val result = dataService.fetchAccess(user)
            val model = result.body()?.toDomainModel(context)
                ?: throw Exception(context.getString(R.string.login_failed))
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }

    private fun List<AccountBankResponse>.toDomainModel(context: Context): AccountsReportModel {
        val mainAccount = this.firstOrNull { it.main }
        return if (mainAccount != null) {
            AccountsReportModel(mainAccount.balance, "Solde")
        } else {
            AccountsReportModel(null, context.getString(R.string.server_error))
        }
    }

    suspend fun fetchAccounts(): Result<AccountsReportModel> {
        return try {
            Result.Loading
            val result = dataService.fetchApiAccounts(currentId)
            val list = result.body() ?: throw Exception(context.getString(R.string.server_error))
            val model = list.toDomainModel(context)
            _currentBalance.value = model.balance
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(context.getString(R.string.server_error))
        }
    }
}
