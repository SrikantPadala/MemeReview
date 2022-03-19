package com.ztute.memereview.network

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

//https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> Response<T>
): ResultWrapper<T> {
    return withContext(dispatcher) {
        val response: Response<T>
        try {
            response = apiCall.invoke()
            if (response.isSuccessful) {
                if (response.body() == null)
                    ResultWrapper.NetworkSuccess(
                        Any() as T,
                        response.headers(),
                        response.code()
                    )
                else
                    ResultWrapper.NetworkSuccess(
                        response.body()!!,
                        response.headers(),
                        response.code()
                    )
            } else {
                ResultWrapper.GenericError(response.code(), response.message())
            }
        } catch (throwable: Throwable) {
            Timber.d((throwable is IOException).toString())
            when (throwable) {
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResultWrapper.GenericError(code, errorResponse)
                }
                is IOException -> {
                    Timber.d("IOException")
                    ResultWrapper.NetworkError
                }
                else -> {
                    ResultWrapper.GenericError(null, throwable.message)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()?.let {
            Gson().fromJson(it, String::class.java)
        }
    } catch (exception: Exception) {
        null
    }
}
