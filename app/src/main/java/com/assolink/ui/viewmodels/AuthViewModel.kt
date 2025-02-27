package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assolink.data.local.entities.UserEntity
import com.assolink.data.repositories.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<UserEntity>>()
    val registerResult: LiveData<Result<UserEntity>> = _registerResult

    private val _loginResult = MutableLiveData<Result<UserEntity>>()
    val loginResult: LiveData<Result<UserEntity>> = _loginResult

    fun register(email: String, password: String, firstName: String,
                 lastName: String, address: String) {
        viewModelScope.launch {
            _registerResult.value = userRepository.registerUser(
                email, password, firstName, lastName, address
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = userRepository.loginUser(email, password)
        }
    }
}