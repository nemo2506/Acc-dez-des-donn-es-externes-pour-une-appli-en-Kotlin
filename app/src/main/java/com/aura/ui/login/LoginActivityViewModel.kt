package com.aura.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aura.R
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import com.aura.domain.model.BalanceReportModel
import com.aura.domain.model.LoginReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository,
    @ApplicationContext private val context: Context
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

        when (val loginUpdate = dataRepository.getBalance()) {
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
                        balance = loginUpdate.value.balance,
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
    val balance: Double? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)