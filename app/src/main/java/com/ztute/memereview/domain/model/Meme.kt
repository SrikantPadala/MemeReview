package com.ztute.memereview.domain.model

import androidx.annotation.Keep
import com.ztute.memereview.database.DatabaseMeme

@Keep
data class Meme(
    val boxCount: Int,
    val height: Int,
    val id: String,
    val name: String,
    val url: String,
    val width: Int
)

fun List<Meme>.asDatabaseModel(): List<DatabaseMeme> {
    return map {
        DatabaseMeme(
            id = it.id,
            boxCount = it.boxCount,
            height = it.height,
            name = it.name,
            url = it.url,
            width = it.width
        )
    }
}