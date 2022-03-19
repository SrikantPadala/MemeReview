package com.ztute.memereview.domain.usecase

import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.network.ResultWrapper
import com.ztute.memereview.repository.MemeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.sql.SQLException
import javax.inject.Inject

class GetMemesFromCacheUseCase @Inject constructor(val repository: MemeRepository) {
    operator fun invoke(): Flow<ResultWrapper<List<DatabaseMeme>>> = flow {
        try {
            emit(ResultWrapper.Loading)
            val memes = repository.getMemes()
            emit(ResultWrapper.DatabaseSuccess(memes))
        } catch (e: SQLException) {
            emit(ResultWrapper.DatabaseError)
        }
    }.flowOn(Dispatchers.IO)
}