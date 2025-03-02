package com.assolink.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assolink.data.local.daos.UserDao
import com.assolink.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}