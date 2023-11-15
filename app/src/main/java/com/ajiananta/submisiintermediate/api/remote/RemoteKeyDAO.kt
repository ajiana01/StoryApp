package com.ajiananta.submisiintermediate.api.remote

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<RemoteKey>)
    @Query("SELECT * FROM remote_key WHERE id = :id")
    fun getRemoteKeyId(id: String): RemoteKey?
    @Query("DELETE FROM remote_key")
    fun deleteRemoteKey()
}