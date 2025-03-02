package com.assolink.di

import com.assolink.network.RetrofitClient
import com.assolink.network.services.GlobalDataService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module
import retrofit2.create

val remoteModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // API Services
    single { RetrofitClient.instance.create<GlobalDataService>() }
}