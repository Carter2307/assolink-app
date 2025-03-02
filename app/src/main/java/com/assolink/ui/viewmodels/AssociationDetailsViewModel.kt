package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assolink.data.model.Association
import com.assolink.data.model.Event
import com.assolink.data.remote.Result
import com.assolink.data.repositories.AssociationRepository
import com.assolink.data.repositories.EventRepository
import kotlinx.coroutines.launch

class AssociationDetailsViewModel(
    private val associationRepository: AssociationRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _association = MutableLiveData<Association?>()
    val association: LiveData<Association?> = _association

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAssociationDetails(associationId: String, forceRefresh: Boolean = false) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            when (val result = associationRepository.getAssociationById(associationId, forceRefresh)) {
                is Result.Success -> {
                    _association.value = result.value
                    loadEventsForAssociation(associationId)
                }
                is Result.Failure -> {
                    _error.value = result.exception.message ?: "Impossible de charger les détails de l'association"
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadEventsForAssociation(associationId: String) {
        viewModelScope.launch {
            when (val result = eventRepository.getEventsByAssociation(associationId)) {
                is Result.Success -> {
                    _events.value = result.value
                }
                is Result.Failure -> {
                    // Juste un log, pas d'erreur critique
                    _events.value = emptyList()
                }
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}