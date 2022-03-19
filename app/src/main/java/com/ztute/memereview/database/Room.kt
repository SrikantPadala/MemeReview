package com.ztute.memereview.database

import androidx.room.*

@Dao
interface MemeDao {
    @Query("select * from meme")
    fun getMemes(): List<DatabaseMeme>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg videos: DatabaseMeme)
}

@Database(entities = [DatabaseMeme::class], version = 1)
abstract class MemeDatabase : RoomDatabase() {
    abstract val memeDao: MemeDao
}