package com.ztute.memereview.network

import retrofit2.Response
import retrofit2.http.GET

interface MemeReviewApiService {
    @GET("get_memes")
    suspend fun getMemes(): Response<MemeNetworkResponse>
}