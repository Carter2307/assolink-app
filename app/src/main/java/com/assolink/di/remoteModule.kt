package com.assolink.di

import com.assolink.network.RetrofitClient
import com.assolink.network.services.GlobalDataService
import org.koin.dsl.module
import retrofit2.create

val remoteModule = module {
    // API Services
    single { RetrofitClient.instance.create<GlobalDataService>() }
}