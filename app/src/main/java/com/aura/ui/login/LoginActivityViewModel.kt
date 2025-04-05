package com.aura.ui.login

import androidx.lifecycle.ViewModel
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import com.aura.data.response.LoginBankResponse
import com.aura.domain.model.LoginReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val dataRepository: BankRepository) : ViewModel() {

    suspend fun getAuraLogin(id:String, password:String): LoginReportModel {
        return when(val result = dataRepository.fetchLoginAccess(id,password)){
            is Result.Success -> result.value
            is Result.Failure -> LoginReportModel(false, "Erreur du serveur")
            is Result.Loading -> LoginReportModel(false, "Erreur de chargement")
        }
    }
}