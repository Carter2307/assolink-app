package com.assolink.network.dto.association

data class AssociationDto(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val logoUrl: String,
    val email: String,
    val phone: String,
)
