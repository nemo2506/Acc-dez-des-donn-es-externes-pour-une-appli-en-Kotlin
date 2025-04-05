package com.aura.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.BankRepository
import com.aura.domain.model.AccountsReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
//    fun fetchAuraAccounts(id: String, password: String): Flow<Result<List<AccountsReportModel>>> {
//        dataRepository.fetchAuraAccounts(id).onEach { accountUpdate ->
//            when (accountUpdate) {
//                is Result.Failure -> _uiState.update { account ->
//                    account.copy(
//                        isViewLoading = false,
//                        errorMessage = accountUpdate.message
//                    )
//                }
//
//                Result.Loading -> _uiState.update { account ->
//                    account.copy(
//                        isViewLoading = true,
//                        errorMessage = null,
//                    )
//                }
//
//                is Result.Success -> _uiState.update { account ->
//                    account.copy(
//                        forecast = accountUpdate.value,
//                        isViewLoading = false,
//                        errorMessage = null,
//                    )
//                }
//            }
//        }.launchIn(viewModelScope)
//    }
}

data class HomeUiState(
    val forecast: List<AccountsReportModel> = emptyList(),
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)