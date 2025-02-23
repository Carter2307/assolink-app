package com.assolink.db

import android.content.Context
import androidx.room.Room

object DBInstance {
    var db: AppDatabase? = null

    // accéder à la base avec contexte
    fun getDatabase(context: Context): AppDatabase {
        return db ?: init(context)
    }

    fun init(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "assolink"
            ).build()
        }
        return db as AppDatabase
    }
}
