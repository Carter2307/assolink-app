package com.assolink.network.dto.association

data class AssociationDto(
    val id: Int,
    val name: String,
    val description: String,
    val logo: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
