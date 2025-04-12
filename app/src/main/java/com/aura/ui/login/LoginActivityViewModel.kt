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

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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

    suspend fun getAuraBalance(): BalanceReportModel {
        return when (val result = dataRepository.getBalance()) {
            is Result.Failure -> BalanceReportModel(
                null,
                context.getString(R.string.balance_error)
            )

            Result.Loading -> BalanceReportModel(null, context.getString(R.string.loading))
            is Result.Success -> result.value
        }
    }
}

//data class LoginUiState(
//    val forecast: List<LoginReportModel> = emptyList(),
//    val isViewLoading: Boolean = false,
//    val errorMessage: String? = null
//)
data class LoginUiState(
    val logged: Boolean = false,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)

data class BalanceUiState(
    val login: List<BalanceReportModel> = emptyList(),
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)