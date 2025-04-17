package com.aura.data.repository

/**
 * Represents the result of an operation that can either be a loading state,
 * a success with a value, or a failure with an optional error message.
 *
 * This sealed class helps manage different states of data-fetching operations,
 * making it easier to handle UI and business logic accordingly.
 *
 * @param T The type of data returned in the success state.
 */
sealed class Result<out T> {

    /**
     * Represents a loading state, typically used while waiting for a response.
     */
    object Loading : Result<Nothing>()

    /**
     * Represents a failure state with an optional error [message].
     *
     * @property message An optional description of the error.
     */
    data class Failure(
        val message: String? = null,
    ) : Result<Nothing>()

    /**
     * Represents a successful result containing a value of type [R].
     *
     * @param R The type of the returned data.
     * @property value The data associated with the success.
     */
    data class Success<out R>(val value: R) : Result<R>()
}
