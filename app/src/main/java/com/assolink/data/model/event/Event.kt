package com.assolink.data.model.event

data class Event(
    val id: Long,
    val associationId: Long,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val location: String
)
