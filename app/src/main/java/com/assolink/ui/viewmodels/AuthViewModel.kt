package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assolink.data.model.User
import com.assolink.data.remote.Result
import com.assolink.data.repositories.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser != null) {
            // Convertir FirebaseUser en User
            val user = User(
                id = currentUser.uid,
                email = currentUser.email ?: "",
                username = currentUser.displayName ?: "",
                preferences = emptyList(),
                favoriteAssociations = emptyList(),
                registeredEvents = emptyList()
            )
            _authState.value = AuthState.Authenticated(user)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = userRepository.signIn(email, password)) {
                is Result.Success -> _authState.value = AuthState.Authenticated(result.value)
                is Result.Failure -> _authState.value = AuthState.Error(
                    result.exception.message ?: "Erreur d'authentification"
                )
            }
        }
    }

    fun register(email: String, password: String, username: String, address: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = userRepository.register(email, password, username, address)) {
                is Result.Success -> _authState.value = AuthState.Authenticated(result.value)
                is Result.Failure -> _authState.value = AuthState.Error(
                    result.exception.message ?: "Erreur lors de l'inscription"
                )
            }
        }
    }

    fun resetPassword(email: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = userRepository.resetPassword(email)) {
                is Result.Success -> _authState.value = AuthState.PasswordResetSent
                is Result.Failure -> _authState.value = AuthState.Error(
                    result.exception.message ?: "Erreur lors de la rÃ©initialisation du mot de passe"
                )
            }
        }
    }

    fun signOut() {
        userRepository.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}