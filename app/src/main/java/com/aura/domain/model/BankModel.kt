package com.aura.domain.model

data class LoginReportModel(val granted: Boolean, val message: String)
data class AccountsReportModel(val balance: Double?, val message: String?)
data class TransferReportModel(val sender: String, val recipient: String, val amount: Double, val message: String)
data class User(val id: String, val password: String)