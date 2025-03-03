package com.assolink.data.local.daos

import androidx.room.*
import com.assolink.data.local.entities.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<EventEntity>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE associationId = :associationId")
    suspend fun getEventsByAssociation(associationId: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE startDate > :currentTime ORDER BY startDate ASC")
    suspend fun getUpcomingEvents(currentTime: Long): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()
}