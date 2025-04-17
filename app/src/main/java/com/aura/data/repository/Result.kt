package com.aura.data.repository

/**
 * Pattern to Return Result in different case: Loading, Failure, Success
 */
sealed class Result<out T> {

    object Loading : Result<Nothing>()

    data class Failure(
        val message: String? = null,
    ) : Result<Nothing>()


    data class Success<out R>(val value: R) : Result<R>()
}