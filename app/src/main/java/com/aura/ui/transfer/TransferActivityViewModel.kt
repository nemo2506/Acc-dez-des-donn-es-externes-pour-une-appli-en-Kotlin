package com.aura.ui.transfer

import androidx.lifecycle.ViewModel
import com.aura.data.repository.BankRepository
import com.aura.domain.model.TransferReportModel
import com.aura.data.repository.Result
import com.aura.domain.model.BalanceReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class TransferActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    val balance = dataRepository.currentBalance
    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    suspend fun getAuraTransfer(recipient: String, amount: Double) {

        when (val transferUpdate = dataRepository.getTransfer(recipient, amount)) {
            is Result.Failure -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        transferred = false,
                        isViewLoading = false,
                        errorMessage = transferUpdate.message
                    )
                }
            }

            Result.Loading -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        isViewLoading = true,
                        errorMessage = null
                    )
                }
            }

            is Result.Success -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        transferred = transferUpdate.value.done,
                        isViewLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }
}

data class TransferUiState(
    val transferred: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)