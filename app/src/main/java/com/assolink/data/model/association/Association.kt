package com.assolink.data.model.association

data class Association(
    val id: Int,
    val name: String,
    val description: String,
    val logo: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
