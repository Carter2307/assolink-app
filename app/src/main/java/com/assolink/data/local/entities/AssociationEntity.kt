package com.assolink.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "associations")
data class AssociationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val address: String,
    val locationLat: Double?,
    val locationLng: Double?,
    val contactEmail: String,
    val contactPhone: String,
    val website: String?,
    val imageUrl: String?,
    val eventsJson: String,
    val membersJson: String,
    val createdAt: Long?,
    val updatedAt: Long?,
    val lastSyncTime: Long = System.currentTimeMillis()
)