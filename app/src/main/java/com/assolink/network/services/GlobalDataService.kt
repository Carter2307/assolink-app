package com.assolink.network.services

import com.assolink.data.model.association.Association
import com.assolink.data.model.event.Event
import com.assolink.network.dto.GlobalModelDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface GlobalDataService {
    @GET("Gilles-knd/JsonServerAssoLink/db")
    fun getAllData(): Call<GlobalModelDto>

    @GET("Gilles-knd/JsonServerAssoLink/associations")
    fun getAssociations(): Call<List<Association>>

    @GET("Gilles-knd/JsonServerAssoLink/events")
    fun getEvents(): Call<List<Event>>

    @GET("Gilles-knd/JsonServerAssoLink/events/association/{associationId}")
    fun getEventsByAssociationId(@Path("associationId") associationId: String): Call<List<Event>>
}

