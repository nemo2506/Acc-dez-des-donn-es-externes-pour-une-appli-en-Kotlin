package com.aura.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.aura.R
import com.aura.data.network.ManageClient
import com.aura.domain.model.Account
import com.aura.domain.model.BalanceReportModel
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.Transfer
import com.aura.domain.model.TransferReportModel
import com.aura.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class BankRepository @Inject constructor(
    private val dataService: ManageClient,
    @ApplicationContext private val context: Context
) {
    private lateinit var currentId: String
    private var _currentBalance = MutableLiveData<Double?>()
    val currentBalance: Double? get() = _currentBalance.value?.toDouble()

    suspend fun getLogin(id: String, password: String): Result<LoginReportModel> {
        return try {
            Result.Loading
            currentId = id
            val user = User(id, password)
            val result = dataService.fetchAccess(user)
            val model = result.body()?.toDomainModel(context)
                ?: throw Exception(context.getString(R.string.login_failed))
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }

    suspend fun getBalance(): Result<BalanceReportModel> {
        return try {
            val result = dataService.fetchBalance(currentId)
            val list = result.body() ?: throw Exception(context.getString(R.string.server_error))
            val accounts: List<Account> = list.map { it.toDomainModel() }
            val mainAccount = accounts.firstOrNull { it.main }
            val model = BalanceReportModel(mainAccount?.balance, null)
            if (mainAccount != null) {
                _currentBalance.value = mainAccount.balance
            }
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }

    suspend fun getTransfer(recipient: String, amount: Double): Result<TransferReportModel> {
        return try {
            Result.Loading
            val transfer = Transfer(currentId, recipient, amount)
            val result = dataService.fetchTransfer(transfer)
            val model = result.body()?.toDomainModel(context)
                ?: throw Exception(context.getString(R.string.transfer_error))
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }
}
