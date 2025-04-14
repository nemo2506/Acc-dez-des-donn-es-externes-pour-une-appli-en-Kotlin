package com.aura.ui.login

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
class LoginActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    suspend fun getAuraLogin(id: String, password: String) {

        when (val loginUpdate = dataRepository.getLogin(id, password)) {
            is Result.Failure -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        isViewLoading = false,
                        errorMessage = loginUpdate.message
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
                        logged = loginUpdate.value.granted,
                        isViewLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    suspend fun getAuraBalance() {

        when (val balanceUpdate = dataRepository.getBalance()) {
            is Result.Failure -> {
                _uiState.update { currentState ->
                    currentState.copy(
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
                        balanceReady = true,
                        isViewLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }
}

data class QueryUiState(
    val logged: Boolean? = null,
    val balanceReady: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)
