// ui/viewmodels/EventsViewModel.kt
package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assolink.data.model.event.Event
import com.assolink.data.remote.Result
import com.assolink.data.repositories.EventRepository
import com.assolink.data.repositories.RegistrationRepository
import com.assolink.data.repositories.UserRepository
import kotlinx.coroutines.launch

class EventsViewModel(
    private val eventRepository: EventRepository,
    private val registrationRepository: RegistrationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

    fun loadUpcomingEvents(forceRefresh: Boolean = false) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            when (val result = eventRepository.getUpcomingEvents()) {
                is Result.Success -> {
                    _events.value = result.value
                    _isLoading.value = false
                }
                is Result.Failure -> {
                    _error.value = result.exception.message ?: "Erreur lors du chargement des événements"
                    _isLoading.value = false
                }
            }
        }
    }

    fun registerForEvent(eventId: String, associationId: String) {
        val currentUser = userRepository.getCurrentUser() ?: return

        _registrationStatus.value = RegistrationStatus.Loading

        viewModelScope.launch {
            // D'abord enregistrer dans la collection "events"
            val registerResult = eventRepository.registerUserForEvent(eventId, currentUser.uid)

            when (registerResult) {
                is Result.Success -> {
                    // Puis créer un enregistrement dans la collection "registrations"
                    when (val registrationResult = registrationRepository.registerUserToEvent(
                        currentUser.uid, eventId, associationId
                    )) {
                        is Result.Success -> {
                            // Mettre à jour la liste des événements pour refléter l'inscription
                            loadUpcomingEvents()
                            _registrationStatus.value = RegistrationStatus.Success
                        }
                        is Result.Failure -> {
                            _registrationStatus.value = RegistrationStatus.Error(
                                registrationResult.exception.message ?: "Erreur lors de l'inscription"
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    _registrationStatus.value = RegistrationStatus.Error(
                        registerResult.exception.message ?: "Erreur lors de l'inscription"
                    )
                }
            }
        }
    }

    fun unregisterFromEvent(eventId: String) {
        val currentUser = userRepository.getCurrentUser() ?: return

        _registrationStatus.value = RegistrationStatus.Loading

        viewModelScope.launch {
            when (val result = eventRepository.unregisterUserFromEvent(eventId, currentUser.uid)) {
                is Result.Success -> {
                    // Mettre à jour la liste des événements
                    loadUpcomingEvents()
                    _registrationStatus.value = RegistrationStatus.Success
                }
                is Result.Failure -> {
                    _registrationStatus.value = RegistrationStatus.Error(
                        result.exception.message ?: "Erreur lors de la désinscription"
                    )
                }
            }
        }
    }

    fun isUserRegisteredForEvent(event: Event): Boolean {
        val currentUser = userRepository.getCurrentUser() ?: return false
        return event.participants.contains(currentUser.uid)
    }

    fun clearError() {
        _error.value = null
    }
}

sealed class RegistrationStatus {
    object Loading : RegistrationStatus()
    object Success : RegistrationStatus()
    data class Error(val message: String) : RegistrationStatus()
}