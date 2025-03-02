 package com.assolink.data.model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Association(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val address: String = "",
    val location: GeoPoint? = null,
    val contactEmail: String = "",
    val contactPhone: String = "",
    val website: String? = null,
    val imageUrl: String? = null,
    val events: List<String> = emptyList(),
    val members: List<String> = emptyList(),
    val createdAt: Long? = null,
    val updatedAt: Long? = null
) : Serializable