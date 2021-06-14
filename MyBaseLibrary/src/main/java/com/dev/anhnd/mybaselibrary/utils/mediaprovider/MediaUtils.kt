package com.dev.anhnd.mybaselibrary.utils.mediaprovider

import android.content.Context
import android.database.Cursor
import java.lang.reflect.Field

fun <T : BaseMediaModel> Context.getMedia(
    clz: Class<T>,
    onCheckIfAddItem: (item: T) -> Boolean = { true },
    projection: Array<String>? = null,
    selection: String? = null,
    selectArgs: Array<String>? = null,
    sortOrder: String? = null
): MutableList<T> {
    val media = clz.newInstance()
    val uri = media.getUri()
    val cursor = contentResolver.query(
        uri,
        projection,
        selection,
        selectArgs,
        sortOrder
    )
    val data = mutableListOf<T>()
    cursor?.let {
        it.moveToFirst()
        while (!it.isAfterLast) {
            val item = getRow(cursor, clz)
            if (onCheckIfAddItem(item)) {
                data.add(item)
            }
            it.moveToNext()
        }
    }
    return data
}


private fun <T : BaseMediaModel> getRow(cursor: Cursor?, clz: Class<T>): T {
    val t = clz.newInstance()
    val fields = t.javaClass.declaredFields
    fields.forEach {
        it.isAccessible = true
        val annotation = it.getAnnotation(MediaInfo::class.java)
        if (annotation != null) {
            val index = cursor?.getColumnIndex(annotation.getFieldName)
            mappingField(cursor!!, index!!, it, t)
        }
    }
    return t
}

private fun <T : BaseMediaModel> mappingField(
    cursor: Cursor,
    index: Int, f: Field, t: T
) {
    when (f.type) {
        Int::class.java -> f.setInt(t, cursor.getInt(index))
        String::class.java -> f.set(t, cursor.getString(index))
        Float::class.java -> f.setFloat(t, cursor.getFloat(index))
        Long::class.java -> f.setLong(t, cursor.getLong(index))
    }
}