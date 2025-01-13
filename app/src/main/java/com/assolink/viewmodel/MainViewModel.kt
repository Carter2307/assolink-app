package com.assolink.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.assolink.data.model.association.Association
import com.assolink.repository.GlobalDataRepository

class MainViewModel : ViewModel() {
    private val repository = GlobalDataRepository()
    val associations: LiveData<List<Association>> = repository.associations

    fun loadAssociations() {
        repository.fetchAssociations()
    }
}