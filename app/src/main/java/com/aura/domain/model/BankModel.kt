package com.aura.domain.model

/**
 * Represents the result of a login attempt.
 *
 * @property granted Indicates whether access was successfully granted.
 */
data class LoginReportModel(val granted: Boolean)

/**
 * Represents the balance report for a user account.
 *
 * @property balance The available balance of the main account. Can be null if no main account is found.
 */
data class BalanceReportModel(val balance: Double?)

/**
 * Represents the result of a money transfer.
 *
 * @property done Indicates whether the transfer was completed successfully. Can be null if the status is unknown.
 */
data class TransferReportModel(val done: Boolean?)


/**
 * Represents user credentials required for authentication.
 *
 * @property id The unique identifier for the user.
 * @property password The user's password.
 */
data class User(val id: String, val password: String)

/**
 * Represents a user's bank account.
 *
 * @property id The unique identifier for the account.
 * @property main Indicates whether this is the user's main account.
 * @property balance The current balance of the account.
 */
data class Account(val id: String, val main: Boolean, val balance: Double)

/**
 * Represents the data required to perform a money transfer.
 *
 * @property sender The ID of the account sending the money.
 * @property recipient The ID of the account receiving the money.
 * @property amount The amount of money to be transferred.
 */
data class Transfer(val sender: String, val recipient: String, val amount: Double)
