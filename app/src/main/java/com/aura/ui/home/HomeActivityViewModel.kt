package com.aura.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aura.data.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val balance = dataRepository.currentBalance
}