package com.assolink.di

import com.assolink.data.repositories.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { UserRepository(get()) }
}