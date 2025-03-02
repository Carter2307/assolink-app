package com.assolink.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.assolink.data.model.association.Association
import com.assolink.network.RetrofitClient
import com.assolink.network.services.GlobalDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class AssociationRepository {
    private val apiService = RetrofitClient.instance.create(GlobalDataService::class.java)
    private val _associations = MutableLiveData<List<Association>?>()
    val associations: LiveData<List<Association>?> = _associations

    fun getAllAssociations(callback: (List<Association>?) -> Unit) {
        apiService.getAssociations().enqueue(object : Callback<List<Association>> {
            override fun onResponse(call: Call<List<Association>>, response: Response<List<Association>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    _associations.value = data
                    callback(data)
                    Log.d("AssociationRepository", "Associations récupérées : ${data?.size}")
                } else {
                    callback(null)
                    Log.e("AssociationRepository", "Erreur: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Association>>, t: Throwable) {
                callback(null)
                Log.e("AssociationRepository", "Erreur réseau", t)
            }
        })
    }

    fun getAssociationById(id: String, callback: (Association?) -> Unit) {
        // Pour simplifier, on filtre depuis la liste déjà chargée
        val currentList = _associations.value
        if (currentList != null) {
            val association = currentList.find { it.id == id }
            callback(association)
        } else {
            // Si la liste n'est pas encore chargée, on la charge d'abord
            getAllAssociations { associations ->
                val association = associations?.find { it.id == id }
                callback(association)
            }
        }
    }
}