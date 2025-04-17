package com.aura.data.repository

import com.aura.data.network.ManageClient
import com.aura.domain.model.Account
import com.aura.domain.model.BalanceReportModel
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.Transfer
import com.aura.domain.model.TransferReportModel
import com.aura.domain.model.User
import javax.inject.Inject


/**
 * Return Result by state from api response
 */
class BankRepository @Inject constructor(
    private val dataService: ManageClient
) {
    /**
     * Result to Login
     */
    suspend fun getLogin(id: String, password: String): Result<LoginReportModel> {
        return try {
            Result.Loading
            val user = User(id, password)
            val result = dataService.fetchAccess(user)
            val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }

    /**
     * Result to Balance
     */
    suspend fun getBalance(currentId: String): Result<BalanceReportModel> {
        return try {
            Result.Loading
            val result = dataService.fetchBalance(currentId)
            val list = result.body() ?: throw Exception("Invalid data")
            val accounts: List<Account> = list.map { it.toDomainModel() }
            val mainAccount = accounts.firstOrNull { it.main }
            val model = BalanceReportModel(mainAccount?.balance)
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }

    /**
     * Result to Transfer
     */
    suspend fun getTransfer(
        currentId: String,
        recipient: String,
        amount: Double
    ): Result<TransferReportModel> {
        return try {
            Result.Loading
            val transfer = Transfer(currentId, recipient, amount)
            val result = dataService.fetchTransfer(transfer)
            val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }
}
