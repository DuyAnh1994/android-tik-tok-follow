package com.solar.dev.tiktok.app.model.app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tikTokVideo_table", indices = [Index(value = ["key"], unique = true)])
data class RecentVideo(@PrimaryKey(autoGenerate = true)
                       val id: Int = 0,
                       @ColumnInfo(name = "name")
                       val name: String = "",
                       @ColumnInfo(name = "urlFull")
                       val urlFull: String = "",
                       @ColumnInfo(name = "urlShort")
                       val urlShort: String = "",
                       @ColumnInfo(name = "thumbnail")
                       val thumbnail: String = "",
                       @ColumnInfo(name = "key")
                       val key: String = ""

)