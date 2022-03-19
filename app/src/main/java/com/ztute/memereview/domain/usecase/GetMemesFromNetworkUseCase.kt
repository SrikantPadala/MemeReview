package com.ztute.memereview.domain.usecase

import com.ztute.memereview.common.SERVER_TIMEOUT
import com.ztute.memereview.database.asDomainModel
import com.ztute.memereview.domain.model.Meme
import com.ztute.memereview.network.ResultWrapper
import com.ztute.memereview.network.asDabataseModel
import com.ztute.memereview.repository.MemeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class GetMemesFromNetworkUseCase @Inject constructor(val repository: MemeRepository) {
    operator fun invoke(): Flow<ResultWrapper<List<Meme>>> = flow {
        emit(ResultWrapper.Loading)
        val job = withTimeoutOrNull(SERVER_TIMEOUT) {
            val result = repository.refreshMemes()
            when (result) {
                is ResultWrapper.NetworkError -> emit(ResultWrapper.NetworkError)
                is ResultWrapper.NetworkSuccess -> {
                    val databaseMemes = result.value.data.memes.map {
                        it.asDabataseModel()
                    }
                    emit(
                        ResultWrapper.NetworkSuccess(
                            databaseMemes.asDomainModel(),
                            result.headers,
                            result.code
                        )
                    )
                }
                is ResultWrapper.GenericError -> emit(
                    ResultWrapper.GenericError(
                        result.code, result.error
                    )
                )
            }
        }
        if (job == null) {
            emit(
                ResultWrapper.GenericError(
                    504,
                    "Server took too long to respond"
                )
            )
        }
    }
        .flowOn(Dispatchers.IO)
}