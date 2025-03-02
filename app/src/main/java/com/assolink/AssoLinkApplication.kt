package com.assolink

import android.app.Application
import com.assolink.di.authModule
import com.assolink.di.coreModule
import com.assolink.di.databaseModule
import com.assolink.di.remoteModule
import com.assolink.di.viewModelModule
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AssoLinkApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialiser Firebase
        FirebaseApp.initializeApp(this)

        // Configuration pour le mode hors ligne Firestore
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Démarrer Koin (CRUCIAL)
        startKoin {
            // Niveau de log pour débug
            androidLogger(Level.DEBUG)
            // Contexte Android pour Koin
            androidContext(this@AssoLinkApplication)
            // Modules à charger
            modules(listOf(
                authModule,
                databaseModule,
                coreModule,
                remoteModule,
                viewModelModule
            ))
        }
    }
}