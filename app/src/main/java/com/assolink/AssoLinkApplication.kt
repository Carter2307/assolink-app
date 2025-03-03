package com.assolink

import android.app.Application
import android.util.Log
import com.assolink.di.coreModule
import com.assolink.di.databaseModule
import com.assolink.di.remoteModule
import com.assolink.utils.ThemeManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AssoLinkApplication : Application() {

    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()

        // Initialiser Firebase
        FirebaseApp.initializeApp(this)

        val settings = FirebaseFirestoreSettings.Builder()
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        FirebaseFirestore.getInstance().collection("associations").get()
            .addOnSuccessListener {
                Log.d("FirestoreTest", "Connexion réussie! Nombre d'associations: ${it.size()}")
                for (doc in it.documents) {
                    Log.d("FirestoreTest", "Association: ${doc.id} - ${doc.getString("name")}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreTest", "Erreur de connexion à Firestore", e)
            }

        // Démarrer Koin (CRUCIAL)
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@AssoLinkApplication)
            modules(listOf(
                databaseModule,
                coreModule,
                remoteModule,
            ))
        }

        // Initialiser et appliquer le thème
        themeManager = ThemeManager(this)
        themeManager.applyTheme()
    }
}