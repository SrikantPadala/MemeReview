package com.ztute.memereview.repository

import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.domain.repository.MemeRepository
import com.ztute.memereview.network.Data
import com.ztute.memereview.network.MemeDto
import com.ztute.memereview.network.MemeNetworkResponse
import com.ztute.memereview.network.ResultWrapper
import okhttp3.Headers

class FakeMemeRepository : MemeRepository {
    val memeList = mutableListOf<DatabaseMeme>()
    val networkMemeList = mutableListOf<MemeDto>()

    override suspend fun refreshMemes(): ResultWrapper<MemeNetworkResponse> {
        return ResultWrapper.NetworkSuccess(
            MemeNetworkResponse(
                Data(networkMemeList),
                true
            ), Headers.headersOf(""), 200
        )
    }

    override suspend fun cacheData(memeList: List<DatabaseMeme>) {
        this.memeList.addAll(memeList)
    }

    override suspend fun getMemes(): List<DatabaseMeme> {
        return memeList
    }
}