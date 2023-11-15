package com.ajiananta.submisiintermediate.api.remote

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ajiananta.submisiintermediate.api.response.ListStoryItem

@Dao
interface StoriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(quote: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAllStories()
}