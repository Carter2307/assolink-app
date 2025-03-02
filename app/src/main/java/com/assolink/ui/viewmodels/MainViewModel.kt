package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.assolink.data.model.Association
import com.assolink.data.repositories.GlobalDataRepository

class MainViewModel : ViewModel() {
    private val repository = GlobalDataRepository()
    val associations: LiveData<List<Association>> = repository.associations

    fun loadAssociations() {
        repository.fetchAssociations()
    }
}