package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.assolink.data.model.Association
import com.assolink.data.model.Event
import com.assolink.data.repositories.AssociationRepository

class AssociationDetailsViewModel : ViewModel() {

    private val repository = AssociationRepository()

    private val _association = MutableLiveData<Association?>()
    val association: LiveData<Association?> = _association

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAssociationDetails(associationId: String) {
        _isLoading.value = true
        _error.value = null

        repository.getAssociationById(associationId) { result ->
            if (result != null) {
                _association.value = result
                // Pour l'instant, pas d'événements
                _events.value = emptyList()
            } else {
                _error.value = "Impossible de charger les détails de l'association"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}