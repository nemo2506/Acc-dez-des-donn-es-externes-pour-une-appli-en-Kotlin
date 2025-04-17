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

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    /**
     * StateFlow to inform screen target activity
     */
    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    /**
     * State Update target to balance
     */
    fun getAuraBalance(currentId: String) {

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
            if (remainingDelay > 0)
                delay(remainingDelay)

            /**
             * Update stateflow in case failure, loading, success
             */
            when (val balanceUpdate = dataRepository.getBalance(currentId)) {

                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isBalanceReady = false,
                            isViewLoading = false,
                            errorMessage = balanceUpdate.message
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
                            isBalanceReady = balanceUpdate.value.balance != null,
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
     * State Update to reset stateflow when failed
     */
    fun reset() {
        _uiState.update { currentState ->
            currentState.copy(
                isBalanceReady = null,
                balance = null
            )
        }
    }
}

/**
 * Data to query balance, balance ready, IsLoading and ErrorMessage
 */
data class QueryUiState(
    val balance: Double? = null,
    val isBalanceReady: Boolean? = null,
    val isViewLoading: Boolean? = null,
    val errorMessage: String? = null
)