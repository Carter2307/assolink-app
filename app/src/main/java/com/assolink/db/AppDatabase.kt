package com.assolink.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assolink.db.daos.UserDao
import com.assolink.db.entities.UserEntity

/*
Room (component 1/3) : Database class
    -> Holds the database and serves as the main access point for the underlying connection to your app's persisted data.
*/

@Database(
    entities = [UserEntity::class], // All entities associated with the DB
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}