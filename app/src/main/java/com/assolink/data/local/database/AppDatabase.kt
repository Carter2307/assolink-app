
package com.assolink.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assolink.data.local.daos.AssociationDao
import com.assolink.data.local.daos.EventDao
import com.assolink.data.local.daos.UserDao
import com.assolink.data.local.entities.AssociationEntity
import com.assolink.data.local.entities.EventEntity
import com.assolink.data.local.entities.UserEntity


@Database(
    entities = [UserEntity::class, AssociationEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun associationDao(): AssociationDao
    abstract fun eventDao(): EventDao
}