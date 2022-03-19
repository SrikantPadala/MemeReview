package com.ztute.memereview.domain.model

import androidx.annotation.Keep

@Keep
data class Meme(
    val boxCount: Int,
    val height: Int,
    val id: String,
    val name: String,
    val url: String,
    val width: Int
)