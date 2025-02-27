package com.assolink.di

import androidx.room.Room
import com.assolink.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "assolink"
        ).build()
    }

    single { get<AppDatabase>().userDao() }
}