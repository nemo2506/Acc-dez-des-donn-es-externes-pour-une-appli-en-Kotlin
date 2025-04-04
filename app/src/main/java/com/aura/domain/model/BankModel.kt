package com.aura.domain.model

data class LoginModel(val id: String,val password: String)
data class AccountsModel(val id: String, val main: Boolean, val amount: Double)
data class TransferModel(val sender: String, val recipient: String, val amount: Double)