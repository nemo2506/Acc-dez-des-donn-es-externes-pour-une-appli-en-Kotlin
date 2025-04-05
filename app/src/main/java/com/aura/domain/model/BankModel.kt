package com.aura.domain.model

data class LoginReportModel(val granted: Boolean, val message: String)
data class AccountsReportModel(val id: String, val main: Boolean, val amount: Double)
data class TransferReportModel(val sender: String, val recipient: String, val amount: Double)
data class User(val id: String, val password: String)