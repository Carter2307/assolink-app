package com.assolink.data.repositories

import com.assolink.data.local.daos.EventDao
import com.assolink.data.model.Event
import com.assolink.data.remote.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val eventDao: EventDao
) {
    private val eventsCollection = firestore.collection("events")

    suspend fun getEventsByAssociation(associationId: String): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = eventsCollection
                    .whereEqualTo("associationId", associationId)
                    .orderBy("startDate", Query.Direction.ASCENDING)
                    .get()
                    .await()

                val events = snapshot.documents.mapNotNull { document ->
                    document.toObject(Event::class.java)?.copy(id = document.id)
                }
                Result.success(events)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getUpcomingEvents(): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val snapshot = eventsCollection
                    .whereGreaterThan("startDate", currentTime)
                    .orderBy("startDate", Query.Direction.ASCENDING)
                    .limit(20) // Limiter pour des raisons de performance
                    .get()
                    .await()

                val events = snapshot.documents.mapNotNull { document ->
                    document.toObject(Event::class.java)?.copy(id = document.id)
                }
                Result.success(events)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getEventById(id: String): Result<Event> =
        withContext(Dispatchers.IO) {
            try {
                val document = eventsCollection.document(id).get().await()
                if (document.exists()) {
                    val event = document.toObject(Event::class.java)?.copy(id = document.id)
                    if (event != null) {
                        Result.success(event)
                    } else {
                        Result.failure(Exception("Échec de conversion du document"))
                    }
                } else {
                    Result.failure(Exception("Événement non trouvé"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun registerUserForEvent(eventId: String, userId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val eventRef = eventsCollection.document(eventId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(eventRef)
                    val event = snapshot.toObject(Event::class.java)

                    if (event == null) {
                        throw Exception("Événement non trouvé")
                    }

                    if (event.participants.contains(userId)) {
                        throw Exception("Utilisateur déjà inscrit")
                    }

                    if (event.maxParticipants != null && event.participants.size >= event.maxParticipants) {
                        throw Exception("L'événement est complet")
                    }

                    val updatedParticipants = event.participants.toMutableList()
                    updatedParticipants.add(userId)

                    transaction.update(eventRef, "participants", updatedParticipants)
                }.await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun unregisterUserFromEvent(eventId: String, userId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val eventRef = eventsCollection.document(eventId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(eventRef)
                    val event = snapshot.toObject(Event::class.java)

                    if (event == null) {
                        throw Exception("Événement non trouvé")
                    }

                    if (!event.participants.contains(userId)) {
                        throw Exception("Utilisateur non inscrit")
                    }

                    val updatedParticipants = event.participants.toMutableList()
                    updatedParticipants.remove(userId)

                    transaction.update(eventRef, "participants", updatedParticipants)
                }.await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}