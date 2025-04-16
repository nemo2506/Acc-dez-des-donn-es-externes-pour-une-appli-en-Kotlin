package com.aura.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject


@HiltViewModel
class TransferActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    fun transferManage(isRecipient: Boolean, isAmout: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isTransferReady = isRecipient && isAmout
            )
        }
    }

    fun getAuraTransfer(currentId: String, recipient: String, amount: Double) {

        viewModelScope.launch {

            _uiState.update { currentState ->
                currentState.copy(
                    isViewLoading = true,
                    errorMessage = null
                )
            }
            val startTime = System.currentTimeMillis()
            val elapsed = System.currentTimeMillis() - startTime
            val remainingDelay = 1000 - elapsed
            if (remainingDelay > 0) delay(remainingDelay)

            when (val transferUpdate = dataRepository.getTransfer(currentId, recipient, amount)) {

                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            errorMessage = transferUpdate.message
                        )
                    }
                }

                Result.Loading -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isViewLoading = true
                        )
                    }
                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            transferred = transferUpdate.value.done
                        )
                    }
                }
            }
        }
    }
}

data class TransferUiState(
    val isTransferReady: Boolean = false,
    val transferred: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)