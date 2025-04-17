package com.aura.domain.model

/**
 * Data to stock report infos
 */
data class LoginReportModel(val granted: Boolean)
data class BalanceReportModel(val balance: Double?)
data class TransferReportModel(val done: Boolean?)

/**
 * Data to api required infos
 */
data class User(val id: String, val password: String)
data class Account(val id: String, val main: Boolean, val balance: Double)
data class Transfer(val sender: String, val recipient: String, val amount: Double)
