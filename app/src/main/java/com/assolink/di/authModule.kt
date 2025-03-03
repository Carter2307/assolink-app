package com.assolink.di

import com.assolink.data.repositories.UserRepository
import com.assolink.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

val authModule = module {
    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Repositories
    single { UserRepository(get(), get()) }

    // ViewModels
    viewModel { AuthViewModel(get()) }
}