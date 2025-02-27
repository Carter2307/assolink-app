package com.assolink.data.model.event

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: Long,
    val location: String,
    val associationId: String
)
