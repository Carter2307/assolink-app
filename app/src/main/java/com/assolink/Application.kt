package com.assolink

import android.app.Application
import com.assolink.di.coreModule
import com.assolink.di.remoteModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AssoLinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialisation de Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AssoLinkApplication)
            modules(appModule)
        }
    }
}

val appModule = mutableListOf(coreModule, remoteModule)