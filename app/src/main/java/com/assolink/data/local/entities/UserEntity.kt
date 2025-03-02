package com.assolink.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val username: String,
    val preferences: String,
    val favoriteAssociations: String = "",
    val registeredEvents: String = "",
    val createdAt: Long? = null,
    val lastLogin: Long? = null,
    val isDarkModeEnabled: Boolean? = null
)