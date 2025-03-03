package com.assolink.di

import android.app.Application
import androidx.room.Room
import com.assolink.data.local.daos.AssociationDao
import com.assolink.data.local.daos.EventDao
import com.assolink.data.local.database.AppDatabase
import com.assolink.data.local.daos.UserDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single { provideDatabase(androidApplication()) }
    single { provideUserDao(get()) }
    single { provideAssociationDao(get()) }
    single { provideEventDao(get()) }
}

private fun provideDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "assolink_database"
    ).build()
}

private fun provideUserDao(database: AppDatabase): UserDao {
    return database.userDao()
}

private fun provideAssociationDao(database: AppDatabase): AssociationDao {
    return database.associationDao()
}

private fun provideEventDao(database: AppDatabase): EventDao {
    return database.eventDao()
}