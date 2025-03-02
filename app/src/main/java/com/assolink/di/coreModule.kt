package com.assolink.di

import com.assolink.data.repositories.AssociationRepository
import com.assolink.data.repositories.UserRepository
import com.assolink.ui.viewmodels.MapViewModel
import com.assolink.ui.viewmodels.AssociationDetailsViewModel
import com.assolink.ui.fragments.MapFragment
import com.assolink.ui.fragments.AssociationDetailsFragment
import com.assolink.ui.viewmodels.AuthViewModel
import com.assolink.ui.viewmodels.ProfileViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val coreModule = module {
    // Fragments
    fragment { MapFragment() }
    fragment { params -> AssociationDetailsFragment.newInstance(params.get()) }

    // ViewModels
    viewModel { MapViewModel() }
    viewModel { AssociationDetailsViewModel() }
    viewModel { AuthViewModel(get()) }
    viewModel { ProfileViewModel(get()) }

    // Repositories
    single { AssociationRepository() }
    single { UserRepository(get(), get(), get ()) }
}