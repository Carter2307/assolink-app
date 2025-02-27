package com.assolink.di

import com.assolink.repository.AssociationRepository
import com.assolink.viewmodel.MapViewModel
import com.assolink.viewmodel.AssociationDetailsViewModel
import com.assolink.views.fragments.MapFragment
import com.assolink.views.fragments.AssociationDetailsFragment
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

    // Repositories
    single { AssociationRepository() }
}