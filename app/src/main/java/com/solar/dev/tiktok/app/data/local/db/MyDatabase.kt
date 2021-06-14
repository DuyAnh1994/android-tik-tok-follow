package com.solar.dev.tiktok.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solar.dev.tiktok.app.model.app.RecentUser
import com.solar.dev.tiktok.app.model.app.RecentVideo
import com.solar.dev.tiktok.app.utils.Constant

@Database(entities = [RecentVideo::class, RecentUser::class], version = 1)
abstract class MyDatabase : RoomDatabase() {

    abstract fun tikTokVideo(): TikTokVideoDao
    abstract fun tikTokUser(): TikTokUserDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(context.applicationContext, MyDatabase::class.java, Constant.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}