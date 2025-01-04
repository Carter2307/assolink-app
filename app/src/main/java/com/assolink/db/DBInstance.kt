package com.assolink.db

import android.content.Context
import androidx.room.Room

object DBInstance {
    var db: AppDatabase? = null

    fun init(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "assolink"
            ).build()
        }
        return db as AppDatabase
    }

    fun getDatabase(): AppDatabase {
        return db ?: throw IllegalStateException("DBInstance is not initialized.")
    }
}
