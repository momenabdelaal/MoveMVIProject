package com.mazaady.data.util

import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (throwable: Throwable) {
        Timber.e(throwable)
        when (throwable) {
            is IOException -> NetworkResult.Error("Please check your internet connection")
            is HttpException -> {
                val code = throwable.code()
                val errorMessage = when (code) {
                    400 -> "Bad Request"
                    401 -> "Unauthorized"
                    403 -> "Forbidden"
                    404 -> "Not Found"
                    500 -> "Internal Server Error"
                    else -> "Something went wrong"
                }
                NetworkResult.Error(errorMessage, code = code)
            }
            else -> NetworkResult.Error(throwable.message ?: "Unknown error occurred")
        }
    }
}
