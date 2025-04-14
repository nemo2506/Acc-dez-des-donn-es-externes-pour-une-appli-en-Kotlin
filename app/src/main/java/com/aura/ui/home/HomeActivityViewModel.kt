package com.aura.ui.home

import androidx.lifecycle.ViewModel
import com.aura.data.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    val balance = dataRepository.currentBalance
}