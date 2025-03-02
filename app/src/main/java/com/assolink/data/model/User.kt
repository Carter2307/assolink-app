package com.assolink.data.model

import java.util.Date

data class User(
    val id: String,
    val email: String,
    val username: String,
    val address: String? = null,
    val preferences: List<String> = emptyList(),
    val favoriteAssociations: List<String> = emptyList(),
    val registeredEvents: List<String> = emptyList(),
    val createdAt: Date? = null,
    val lastLogin: Date? = null,
    val isDarkModeEnabled: Boolean? = null
)