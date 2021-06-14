package com.solar.dev.tiktok.app.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solar.dev.tiktok.app.model.app.RecentUser

@Dao
interface TikTokUserDao {

    @Query("SELECT * from tikTokAccount_table")
    fun getAllUser(): LiveData<List<RecentUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentUser: RecentUser)
}