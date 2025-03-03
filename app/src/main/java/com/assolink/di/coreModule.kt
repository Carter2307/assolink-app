package com.assolink.di

import com.assolink.data.repositories.AssociationRepository
import com.assolink.data.repositories.EventRepository
import com.assolink.data.repositories.RegistrationRepository
import com.assolink.data.repositories.UserRepository
import com.assolink.ui.viewmodels.MapViewModel
import com.assolink.ui.viewmodels.AssociationDetailsViewModel
import com.assolink.ui.fragments.MapFragment
import com.assolink.ui.fragments.AssociationDetailsFragment
import com.assolink.ui.viewmodels.AuthViewModel
import com.assolink.ui.viewmodels.EventsViewModel
import com.assolink.ui.viewmodels.HomeViewModel
import com.assolink.ui.viewmodels.ProfileViewModel
import com.assolink.utils.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val coreModule = module {
    // Fragments
    fragment { MapFragment() }
    fragment { params -> AssociationDetailsFragment.newInstance(params.get()) }

    // ViewModels
    viewModel { AuthViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { EventsViewModel(eventRepository = get(), registrationRepository = get(), userRepository = get()) }
    viewModel { MapViewModel(associationRepository = get()) }
    viewModel { AssociationDetailsViewModel(associationRepository = get(), eventRepository = get()) }
    viewModel { HomeViewModel(associationRepository = get(), eventRepository = get(), userRepository = get()) }

    // Repositories
    single { AssociationRepository(get()) }
    single { UserRepository(get(), get(), get ()) }
    single { EventRepository(get()) }
    single { RegistrationRepository(firestore = get()) }

    // Utils
    single { ThemeManager(androidContext()) }

}