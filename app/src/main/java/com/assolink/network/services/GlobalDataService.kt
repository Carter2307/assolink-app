package com.assolink.network.services

import com.assolink.data.model.association.Association
import com.assolink.network.dto.GlobalModelDto
import retrofit2.Call
import retrofit2.http.GET


interface GlobalDataService {
    @GET("Gilles-knd/JsonServerAssoLink/db")
    fun getAllData(): Call<GlobalModelDto>

    @GET("Gilles-knd/JsonServerAssoLink/associations")
    fun getAssociations(): Call<List<Association>>
}

