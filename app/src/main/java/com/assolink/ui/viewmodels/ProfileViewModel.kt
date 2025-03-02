package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assolink.data.model.User
import com.assolink.data.remote.Result
import com.assolink.data.repositories.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> = _userProfile

    private val _updateStatus = MutableLiveData<UpdateProfileStatus>()
    val updateStatus: LiveData<UpdateProfileStatus> = _updateStatus

    private val _isDarkModeEnabled = MutableLiveData<Boolean>()
    val isDarkModeEnabled: LiveData<Boolean> = _isDarkModeEnabled

    init {
        loadUserProfile()
        loadThemePreference()
    }

    fun loadUserProfile() {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                when (val result = userRepository.getUserProfile(currentUser.uid)) {
                    is Result.Success -> {
                        _userProfile.value = result.value
                    }
                    is Result.Failure -> {
                        // Fallback à un profil basique si Firestore échoue
                        _userProfile.value = User(
                            id = currentUser.uid,
                            email = currentUser.email ?: "",
                            username = currentUser.displayName ?: "",
                            preferences = emptyList(),
                            favoriteAssociations = emptyList(),
                            registeredEvents = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun updateProfile(username: String, address: String) {
        val currentUser = userRepository.getCurrentUser() ?: return

        _updateStatus.value = UpdateProfileStatus.Loading
        viewModelScope.launch {
            val updates = mapOf(
                "username" to username,
                "address" to address
            )

            when (val result = userRepository.updateUserProfile(currentUser.uid, updates)) {
                is Result.Success -> {
                    // Mettre à jour le profil local
                    _userProfile.value = _userProfile.value?.copy(
                        username = username
                    )
                    _updateStatus.value = UpdateProfileStatus.Success
                }
                is Result.Failure -> {
                    _updateStatus.value = UpdateProfileStatus.Error(
                        result.exception.message ?: "Erreur lors de la mise à jour du profil"
                    )
                }
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkModeEnabled.value = enabled
        userRepository.saveDarkModePreference(enabled)
    }

    private fun loadThemePreference() {
        _isDarkModeEnabled.value = userRepository.getDarkModePreference()
    }

    fun signOut() {
        userRepository.signOut()
    }
}

sealed class UpdateProfileStatus {
    object Loading : UpdateProfileStatus()
    object Success : UpdateProfileStatus()
    data class Error(val message: String) : UpdateProfileStatus()
}