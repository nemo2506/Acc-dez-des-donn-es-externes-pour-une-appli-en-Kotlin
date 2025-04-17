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

    /**
     * StateFlow to inform screen target activity
     */
    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    /**
     * State Update to inform user data verified
     */
    fun userDataControl(isRecipient: Boolean, isAmount: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isUserDataReady = isRecipient && isAmount
            )
        }
    }

    /**
     * State Update target to transfer verified
     */
    fun getAuraTransfer(currentId: String, recipient: String, amount: Double) {

        viewModelScope.launch {

            /**
             * Force to wait 1 sec to display loader
             */
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

            /**
             * Update stateflow in case failure, loading, success
             */
            when (val transferUpdate = dataRepository.getTransfer(currentId, recipient, amount)) {

                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isViewLoading = false,
                            errorMessage = transferUpdate.message,
                            transferred = false
                        )
                    }
                }

                Result.Loading -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isViewLoading = true,
                            transferred = null
                        )
                    }
                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isViewLoading = false,
                            transferred = transferUpdate.value.done
                        )
                    }
                }
            }
        }
    }

    /**
     * State Update to reset stateflow when failed
     */
    fun reset() {
        _uiState.update { currentState ->
            currentState.copy(
                isUserDataReady = null,
                transferred = null
            )
        }
    }
}

/**
 * Data to query UserData, Transferred, IsLoading and ErrorMessage
 */
data class TransferUiState(
    val isUserDataReady: Boolean? = null,
    val transferred: Boolean? = null,
    val isViewLoading: Boolean? = null,
    val errorMessage: String? = null
)