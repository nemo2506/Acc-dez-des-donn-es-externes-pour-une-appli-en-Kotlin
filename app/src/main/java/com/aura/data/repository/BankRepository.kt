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
 * Repository responsible for handling banking-related operations such as login,
 * retrieving account balance, and performing money transfers. Communicates
 * with [ManageClient] to fetch data from the API and transforms the response
 * to domain models.
 *
 * @property dataService The network client used to perform API calls.
 */
class BankRepository @Inject constructor(
    private val dataService: ManageClient
) {

    /**
     * Attempts to log in a user with the given [id] and [password].
     *
     * @param id The user identifier.
     * @param password The user's password.
     * @return [Result] containing [LoginReportModel] on success or an error message on failure.
     */
    suspend fun getLogin(id: String, password: String): Result<LoginReportModel> {
        return try {
            val user = User(id, password)
            val result = dataService.fetchAccess(user)
            val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }

    /**
     * Retrieves the balance information for the user with the given [currentId].
     *
     * @param currentId The identifier of the current user.
     * @return [Result] containing [BalanceReportModel] with the main account's balance,
     * or an error message on failure.
     */
    suspend fun getBalance(currentId: String): Result<BalanceReportModel> {
        return try {
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
     * Performs a transfer from the current user to the [recipient] with the specified [amount].
     *
     * @param currentId The ID of the sender.
     * @param recipient The ID of the recipient.
     * @param amount The amount of money to transfer.
     * @return [Result] containing [TransferReportModel] on success or an error message on failure.
     */
    suspend fun getTransfer(
        currentId: String,
        recipient: String,
        amount: Double
    ): Result<TransferReportModel> {
        return try {
            val transfer = Transfer(currentId, recipient, amount)
            val result = dataService.fetchTransfer(transfer)
            val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
            Result.Success(model)
        } catch (error: Exception) {
            Result.Failure(error.message)
        }
    }
}
