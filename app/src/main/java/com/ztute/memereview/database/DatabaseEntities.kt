package com.ztute.memereview.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ztute.memereview.domain.model.Meme

@Entity(tableName = "meme")
data class DatabaseMeme(
    @PrimaryKey
    val id: String,
    val boxCount: Int,
    val height: Int,
    val name: String,
    val url: String,
    val width: Int
)

fun List<DatabaseMeme>.asDomainModel(): List<Meme> {
    return map {
        Meme(
            id = it.id,
            boxCount = it.boxCount,
            height = it.height,
            name = it.name,
            url = it.url,
            width = it.width
        )
    }
}