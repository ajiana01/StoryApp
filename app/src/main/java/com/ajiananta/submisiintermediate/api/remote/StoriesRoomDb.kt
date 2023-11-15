package com.ajiananta.submisiintermediate.api.remote

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ajiananta.submisiintermediate.api.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKey::class],
    version = 3,
    exportSchema = false
)
abstract class StoriesRoomDb: RoomDatabase(){
    abstract fun storiesDao(): StoriesDAO
    abstract fun remoteKeyDao(): RemoteKeyDAO

    companion object{
        @Volatile
        private var INSTANCE: StoriesRoomDb? = null

        @JvmStatic
        fun getDb (context: Context): StoriesRoomDb{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesRoomDb::class.java,
                    "stories_db"
                ).build()
            }
        }
    }
}