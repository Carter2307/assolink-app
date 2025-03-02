package com.assolink.data.remote

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()

    companion object {
        fun <T> success(value: T): Result<T> = Success(value)
        fun failure(exception: Exception): Result<Nothing> = Failure(exception)
    }

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = when(this) {
        is Success -> value
        is Failure -> null
    }
}