package com.solar.dev.tiktok.app.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solar.dev.tiktok.app.model.app.RecentVideo

@Dao
interface TikTokVideoDao {

    @Query("SELECT * from tikTokVideo_table")
    fun getAllVideo(): LiveData<List<RecentVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentVideo: RecentVideo)
}