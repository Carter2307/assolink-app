package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.assolink.data.model.association.Association
import com.assolink.data.repositories.AssociationRepository

class MapViewModel : ViewModel() {

    private val repository = AssociationRepository()

    private val _associations = MutableLiveData<List<Association>?>()
    val associations: LiveData<List<Association>?> = _associations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadAssociations()
    }

    fun loadAssociations() {
        _isLoading.value = true
        _error.value = null

        repository.getAllAssociations { result ->
            if (result != null) {
                _associations.value = result
            } else {
                _error.value = "Impossible de charger les associations"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}