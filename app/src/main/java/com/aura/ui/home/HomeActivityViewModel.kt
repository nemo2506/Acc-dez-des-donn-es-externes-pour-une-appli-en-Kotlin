package com.aura.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [HomeActivity], handles logic related to fetching and managing balance data.
 *
 * Uses [BankRepository] to make API calls and exposes state through [uiState] to be observed by the UI.
 */
@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    /**
     * Mutable backing state for UI.
     */
    private val _uiState = MutableStateFlow(QueryUiState())

    /**
     * Public immutable state observed by the UI.
     */
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    /**
     * Fetches the user's balance from the repository.
     *
     * Updates UI state accordingly (loading, success, or failure).
     * Includes a forced delay of 1 second to simulate or ensure UI feedback.
     *
     * @param currentId The ID of the currently logged-in user.
     */
    fun getAuraBalance(currentId: String) {
        viewModelScope.launch {

            // Simulate a delay before showing the loader
            _uiState.update { currentState ->
                currentState.copy(
                    isViewLoading = true,
                    errorMessage = null
                )
            }

            // Force to wait 1 second to display the loader
            val startTime = System.currentTimeMillis()
            val elapsed = System.currentTimeMillis() - startTime
            val remainingDelay = 1000 - elapsed
            if (remainingDelay > 0) delay(remainingDelay)

            // Attempt to log in and update UI state based on the result
            when (val balanceUpdate = dataRepository.getBalance(currentId)) {

                // If balance fails, update state with failure message
                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isBalanceReady= false,
                            isViewLoading = false,
                            errorMessage = balanceUpdate.message
                        )
                    }
                }

                // If balance is in progress, keep the loader visible
                Result.Loading -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isViewLoading = true,
                            errorMessage = null
                        )
                    }
                }

                // If balance is successful, update state with login success
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isBalanceReady= true,
                            balance = balanceUpdate.value.balance,
                            isViewLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    /**
     * Resets the balance and status in the UI state.
     *
     * Called typically after a failed attempt to avoid stale or inconsistent UI state.
     */
    fun reset() {
        _uiState.update { currentState ->
            currentState.copy(
                balance = null,
                isBalanceReady = null,
                errorMessage = null
            )
        }
    }
}

/**
 * UI state class that holds balance, loading status, readiness state, and error messages.
 *
 * Used with [StateFlow] to provide observable state updates to the view.
 *
 * @property balance The user's current balance, if available.
 * @property isViewLoading Whether a loading indicator should be shown.
 * @property errorMessage Optional error message in case of a failure.
 */
data class QueryUiState(
    val balance: Double? = null,
    val isBalanceReady: Boolean? = null,
    val isViewLoading: Boolean? = null,
    val errorMessage: String? = null
)
