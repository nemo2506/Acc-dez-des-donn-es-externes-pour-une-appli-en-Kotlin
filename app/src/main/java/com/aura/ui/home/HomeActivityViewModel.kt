package com.aura.ui.home

import androidx.lifecycle.ViewModel
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    val balance = dataRepository.currentBalance
    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    suspend fun getAuraBalance() {

        when (val balanceUpdate = dataRepository.getBalance()) {
            is Result.Failure -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        balanceReady = false,
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
                        balanceReady = balanceUpdate.value.balance != null,
                        balance = balanceUpdate.value.balance,
                        isViewLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }
}

data class QueryUiState(
    val balance: Double? = null,
    val balanceReady: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)