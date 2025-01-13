package com.assolink.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.assolink.data.model.association.Association
import com.assolink.network.RetrofitClient
import com.assolink.network.dto.GlobalModelDto
import com.assolink.network.mapper.mapGlobalDataDtoToGlobalDataModel
import com.assolink.network.services.GlobalDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class GlobalDataRepository {
    private val globalDataService = RetrofitClient.instance.create(GlobalDataService::class.java)
    private val _associations = MutableLiveData<List<Association>>()
    val associations: LiveData<List<Association>> = _associations

    fun fetchAssociations() {
        globalDataService.getAllData().enqueue(object : Callback<GlobalModelDto> {
            override fun onResponse(call: Call<GlobalModelDto>, response: Response<GlobalModelDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { dto ->
                        val globalData = mapGlobalDataDtoToGlobalDataModel(dto)
                        _associations.value = globalData.associations
                        Log.d("Repository", "Associations récupérées : ${_associations.value?.size}")
                    }
                } else {
                    Log.e("Repository", "Erreur: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GlobalModelDto>, t: Throwable) {
                Log.e("Repository", "Erreur réseau", t)
            }
        })
    }
}