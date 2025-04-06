package com.aura.domain.model

import android.os.IBinder.DeathRecipient

data class LoginReportModel(val granted: Boolean, val message: String)
data class AccountsReportModel(val balance: Double?, val message: String?)
data class TransferReportModel(val result: Boolean?, val message: String?)
data class User(val id: String, val password: String)
data class Transfer(val sender: String, val recipient: String, val amount: Double)