package com.aura.ui.transfer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aura.R
import com.aura.data.repository.BankRepository
import com.aura.domain.model.TransferReportModel
import com.aura.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class TransferActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    suspend fun getAuraTransfer(recipient: String, amount: Double): TransferReportModel {
        return when (val result = dataRepository.getTransfer(recipient, amount)) {
            is Result.Failure -> TransferReportModel(null,context.getString(R.string.transfer_error))
            Result.Loading -> TransferReportModel(null, context.getString(R.string.loading))
            is Result.Success -> result.value
        }
    }
}