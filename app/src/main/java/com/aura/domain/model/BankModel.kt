package com.aura.domain.model

data class LoginReportModel(val granted: Boolean, val message: String)
data class BalanceReportModel(val balance: Double?, val message: String?)
data class TransferReportModel(val done: Boolean?, val message: String?)
data class User(val id: String, val password: String)
data class Account(val id: String, val main: Boolean, val balance: Double)
data class Transfer(val sender: String, val recipient: String, val amount: Double)
