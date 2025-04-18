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

/**
 * ViewModel for the TransferActivity that handles the transfer logic.
 * It is responsible for handling the transfer process and updating the UI state accordingly.
 */
@HiltViewModel
class TransferActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    /**
     * StateFlow to inform the screen of the current transfer activity state.
     */
    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    /**
     * Updates the UI state to indicate whether the user has filled in the recipient and amount fields.
     *
     * @param isRecipient Boolean indicating whether the recipient field is filled.
     * @param isAmount Boolean indicating whether the amount field is filled.
     */
    fun userDataControl(isRecipient: Boolean, isAmount: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isUserDataReady = isRecipient && isAmount
            )
        }
    }

    /**
     * Initiates the transfer process by calling the repository to perform the transfer.
     * Updates the UI state based on the result (Loading, Success, Failure).
     *
     * @param currentId The current user ID.
     * @param recipient The recipient's information.
     * @param amount The transfer amount.
     */
    fun getAuraTransfer(currentId: String, recipient: String, amount: Double) {

        viewModelScope.launch {

            // Simulate a delay before showing the loader
            _uiState.update { currentState ->
                currentState.copy(
                    isViewLoading = true,
                    errorMessage = null
                )
            }

            // Force to wait 1 second to display the loader
            delay(1000)

            // Attempt to log in and update UI state based on the result
            when (val transferUpdate = dataRepository.getTransfer(currentId, recipient, amount)) {

                // If transfer fails, update state with failure message
                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isViewLoading = false,
                            errorMessage = transferUpdate.message,
                            transferred = false
                        )
                    }
                }

                // If transfer is successful, update state with login success
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isUserDataReady = false,
                            transferred = transferUpdate.value.done,
                            isViewLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    /**
     * Resets the UI state, particularly after a failed transfer.
     */
    fun reset() {
        _uiState.update { currentState ->
            currentState.copy(
                transferred = null,
                errorMessage = null
            )
        }
    }
}

/**
 * Data class representing the UI state of the transfer screen.
 * Contains the following properties:
 * - isUserDataReady: Boolean indicating whether the user data is valid (recipient and amount filled).
 * - transferred: Boolean indicating whether the transfer was successful.
 * - isViewLoading: Boolean indicating whether the transfer is in progress (loading state).
 * - errorMessage: A string to hold any error message if an error occurs.
 */
data class TransferUiState(
    val isUserDataReady: Boolean? = null,
    val transferred: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)
