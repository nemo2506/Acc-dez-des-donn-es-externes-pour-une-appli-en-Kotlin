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

    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    fun getAuraBalance(currentId: String) {

        viewModelScope.launch {

            // FORCE 1 sec to TEST
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

    fun reInit() {
        _uiState.update { currentState ->
            currentState.copy(
                isBalanceReady = null,
                balance = null
            )
        }
    }
}

data class QueryUiState(
    val balance: Double? = null,
    val isBalanceReady: Boolean? = null,
    val isViewLoading: Boolean? = null,
    val errorMessage: String? = null
)