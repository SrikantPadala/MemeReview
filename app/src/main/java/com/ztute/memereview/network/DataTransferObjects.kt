package com.ztute.memereview.network

import com.google.gson.annotations.SerializedName
import com.ztute.memereview.database.DatabaseMeme

data class Data(
    val memes: List<MemeDto>
)

data class MemeNetworkResponse(
    @SerializedName("data")
    val `data`: Data,
    val success: Boolean
)

data class MemeDto(
    @SerializedName("box_count")
    val boxCount: Int,
    val height: Int,
    val id: String,
    val name: String,
    val url: String,
    val width: Int
)

fun MemeDto.asDabataseModel() =
    DatabaseMeme(id, boxCount, height, name, url, width)