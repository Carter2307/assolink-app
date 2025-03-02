package com.assolink.di

import com.assolink.data.repositories.AssociationRepository
import com.assolink.ui.viewmodels.MapViewModel
import com.assolink.ui.viewmodels.AssociationDetailsViewModel
import com.assolink.ui.fragments.MapFragment
import com.assolink.ui.fragments.AssociationDetailsFragment
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