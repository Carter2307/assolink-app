package com.assolink.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String,
    val associationId: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long?,
    val locationLat: Double?,
    val locationLng: Double?,
    val address: String,
    val imageUrl: String?,
    val maxParticipants: Int?,
    val participantsJson: String,
    val createdAt: Long?,
    val updatedAt: Long?,
    val lastSyncTime: Long = System.currentTimeMillis()
)