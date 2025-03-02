package com.assolink.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assolink.data.model.Association
import com.assolink.data.remote.Result
import com.assolink.data.repositories.AssociationRepository
import kotlinx.coroutines.launch

class MapViewModel(
    private val associationRepository: AssociationRepository
) : ViewModel() {

    private val _associations = MutableLiveData<List<Association>>()
    val associations: LiveData<List<Association>> = _associations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> = _selectedCategory

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    init {
        loadAssociations()
    }

    fun loadAssociations(forceRefresh: Boolean = false) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val category = _selectedCategory.value
            val query = _searchQuery.value

            val result = when {
                // Si une catégorie est sélectionnée
                !category.isNullOrEmpty() ->
                    associationRepository.getAssociationsByCategory(category)

                // Si une recherche est en cours
                !query.isNullOrEmpty() ->
                    associationRepository.searchAssociations(query)

                // Sinon, récupérer toutes les associations
                else ->
                    associationRepository.getAllAssociations(forceRefresh)
            }

            when (result) {
                is Result.Success -> {
                    _associations.value = result.value
                }
                is Result.Failure -> {
                    _error.value = result.exception.message ?: "Erreur lors du chargement des associations"
                }
            }
            _isLoading.value = false
        }
    }

    fun setCategory(category: String?) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadAssociations(true)
        }
    }

    fun setSearchQuery(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
            loadAssociations(true)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadAssociations(true)
    }

    fun clearError() {
        _error.value = null
    }
}