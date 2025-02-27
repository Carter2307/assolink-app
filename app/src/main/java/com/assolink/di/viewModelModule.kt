package com.assolink.di

import com.assolink.ui.viewmodels.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
}