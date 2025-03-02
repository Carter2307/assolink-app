 package com.assolink.data.model

data class Association(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val logoUrl: String,
    val email: String,
    val phone: String,
    val category: String? = null
)