package com.aura.ui.transfer

import androidx.lifecycle.ViewModel
import com.aura.data.repository.BankRepository
import com.aura.domain.model.TransferReportModel
import com.aura.data.repository.Result
import com.aura.domain.model.BalanceReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TransferActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    val balance = dataRepository.currentBalance

    suspend fun getAuraTransfer(recipient: String, amount: Double): TransferReportModel {
        return when (val result = dataRepository.getTransfer(recipient, amount)) {
            is Result.Failure -> TransferReportModel(
                null,
                result.message
            )

            Result.Loading -> TransferReportModel(null, null)
            is Result.Success -> result.value
        }
    }

    suspend fun getAuraBalance(): BalanceReportModel {
        return when (val result = dataRepository.getBalance()) {
            is Result.Failure -> BalanceReportModel(null, result.message)
            Result.Loading -> BalanceReportModel(null, null)
            is Result.Success -> result.value
        }
    }
}