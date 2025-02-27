
package com.assolink.data.repositories

import com.assolink.data.model.event.Event
import com.assolink.network.services.GlobalDataService


class EventRepository(private val apiService: GlobalDataService) {
    suspend fun getEventsByAssociationId(associationId: String): List<Event> {
        // Simuler des données pour le moment
        return emptyList()
    }
}