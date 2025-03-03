package com.assolink.data.model.event

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Event(
    val id: String = "",
    val associationId: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Long = 0,
    val endDate: Long? = null,
    val location: GeoPoint? = null,
    val address: String = "",
    val imageUrl: String? = null,
    val maxParticipants: Int? = null,
    val participants: List<String> = emptyList(),
    val createdAt: Long? = null,
    val updatedAt: Long? = null
) : Serializable