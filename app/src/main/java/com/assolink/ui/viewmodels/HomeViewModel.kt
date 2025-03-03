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
import com.assolink.data.repositories.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val associationRepository: AssociationRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _favoriteAssociations = MutableLiveData<List<Association>>()
    val favoriteAssociations: LiveData<List<Association>> = _favoriteAssociations

    private val _upcomingEvents = MutableLiveData<List<Event>>()
    val upcomingEvents: LiveData<List<Event>> = _upcomingEvents

    private val _recommendedAssociations = MutableLiveData<List<Association>>()
    val recommendedAssociations: LiveData<List<Association>> = _recommendedAssociations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

    fun loadHomeData(forceRefresh: Boolean = false) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Charger les données en parallèle
                loadFavoriteAssociations(forceRefresh)
                loadUpcomingEvents(forceRefresh)
                loadRecommendedAssociations(forceRefresh)

                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors du chargement des données"
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadFavoriteAssociations(forceRefresh: Boolean) {
        val currentUser = userRepository.getCurrentUser()

        if (currentUser != null) {
            when (val userResult = userRepository.getUserProfile(currentUser.uid)) {
                is Result.Success -> {
                    val favoriteIds = userResult.value.favoriteAssociations

                    if (favoriteIds.isNotEmpty()) {
                        when (val result = associationRepository.getFavoriteAssociations(favoriteIds)) {
                            is Result.Success -> {
                                _favoriteAssociations.value = result.value
                            }
                            is Result.Failure -> {
                                _favoriteAssociations.value = emptyList()
                            }
                        }
                    } else {
                        _favoriteAssociations.value = emptyList()
                    }
                }
                is Result.Failure -> {
                    _favoriteAssociations.value = emptyList()
                }
            }
        } else {
            _favoriteAssociations.value = emptyList()
        }
    }

    private suspend fun loadUpcomingEvents(forceRefresh: Boolean) {
        when (val result = eventRepository.getUpcomingEvents()) {
            is Result.Success -> {
                _upcomingEvents.value = result.value.take(5) // Limiter à 5 événements pour la page d'accueil
            }
            is Result.Failure -> {
                _upcomingEvents.value = emptyList()
            }
        }
    }

    private suspend fun loadRecommendedAssociations(forceRefresh: Boolean) {
        // Logique simple pour les recommandations : prendre les 5 premières associations
        when (val result = associationRepository.getAllAssociations(forceRefresh)) {
            is Result.Success -> {
                _recommendedAssociations.value = result.value.take(5)
            }
            is Result.Failure -> {
                _recommendedAssociations.value = emptyList()
            }
        }
    }

    fun registerForEvent(eventId: String, associationId: String) {
        val currentUser = userRepository.getCurrentUser() ?: return

        _registrationStatus.value = RegistrationStatus.Loading

        viewModelScope.launch {
            // Inscrire l'utilisateur à l'événement
            when (val result = eventRepository.registerUserForEvent(eventId, currentUser.uid)) {
                is Result.Success -> {
                    _registrationStatus.value = RegistrationStatus.Success
                    loadUpcomingEvents(true) // Rafraîchir les événements
                }
                is Result.Failure -> {
                    _registrationStatus.value = RegistrationStatus.Error(
                        result.exception.message ?: "Erreur lors de l'inscription"
                    )
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}