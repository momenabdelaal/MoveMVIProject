package com.mazaady.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber

abstract class NetworkBoundResource<ResultType, RequestType> {

    fun asFlow() = flow {
        try {
            emit(NetworkResult.Loading)

            val dbValue = loadFromDb().first()
            if (shouldFetch(dbValue)) {
                emit(NetworkResult.Success(dbValue))

                when (val apiResponse = fetchFromNetwork()) {
                    is NetworkResult.Success -> {
                        saveNetworkResult(apiResponse.data)
                        emit(NetworkResult.Success(loadFromDb().first()))
                    }
                    is NetworkResult.Error -> {
                        emit(NetworkResult.Error(apiResponse.message, apiResponse.code))
                    }
                    NetworkResult.Loading -> {
                        // This should never happen as fetchFromNetwork() should never return Loading
                        emit(NetworkResult.Error("Invalid state: Loading during fetch"))
                    }
                }
            } else {
                emit(NetworkResult.Success(dbValue))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in NetworkBoundResource")
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    protected abstract suspend fun loadFromDb(): Flow<ResultType>

    protected abstract fun shouldFetch(data: ResultType): Boolean

    protected abstract suspend fun fetchFromNetwork(): NetworkResult<RequestType>

    protected abstract suspend fun saveNetworkResult(response: RequestType)
}
