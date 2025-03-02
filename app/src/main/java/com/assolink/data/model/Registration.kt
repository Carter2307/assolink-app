package com.assolink.data.model

data class Registration(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val associationId: String = "",
    val registrationDate: Long = System.currentTimeMillis(),
    val status: String = "confirmed" // confirmed, cancelled
)
