package com.assolink

import android.app.Application
import com.assolink.di.authModule
import com.assolink.di.coreModule
import com.assolink.di.remoteModule
import com.assolink.di.databaseModule
import com.assolink.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AssoLinkApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@AssoLinkApplication)
            modules(
                listOf(
                    coreModule,
                    remoteModule,
                    databaseModule,
                    viewModelModule,
                    authModule
                )
            )
        }
    }
}