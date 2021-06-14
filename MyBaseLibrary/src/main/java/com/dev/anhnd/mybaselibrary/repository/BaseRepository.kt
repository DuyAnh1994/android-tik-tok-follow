package com.dev.anhnd.mybaselibrary.repository

open class BaseRepository<T>(
    private val clz: Class<T>
) {

    companion object  {
//        fun <T> getInstance(t : T) : T {
//
//        }
    }

    @Volatile
    private var instance: T? = null

    @Suppress("UNCHECKED_CAST")
    fun <T> getInstance(): T? {
        if (instance == null) {
            instance = createInstance(clz)
        }
        return instance as T
    }

    fun clear() {
        instance = null
    }

    private fun <T> createInstance(clazz: Class<T>): T {
        return clazz.newInstance()
    }
}

fun <T : BaseRepository<T>> createRepository(clz: Class<T>) = clz.newInstance()