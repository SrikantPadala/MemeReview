package com.ztute.memereview.network

import okhttp3.Headers

sealed class ResultWrapper<out T> {
    data class NetworkSuccess<out T>(
        val value: T,
        val headers: Headers,
        val code: Int = 0
    ) : ResultWrapper<T>()

    data class GenericError(val code: Int? = null, val error: String? = null) :
        ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()

    data class DatabaseSuccess<out T>(val value: T) : ResultWrapper<T>()
    object DatabaseError : ResultWrapper<Nothing>()
}
