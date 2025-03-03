package com.assolink.data.repositories

import com.assolink.data.model.Registration
import com.assolink.data.remote.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegistrationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val registrationsCollection = firestore.collection("registrations")

    suspend fun registerUserToEvent(userId: String, eventId: String, associationId: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val registration = hashMapOf(
                    "userId" to userId,
                    "eventId" to eventId,
                    "associationId" to associationId,
                    "registrationDate" to System.currentTimeMillis(),
                    "status" to "confirmed"
                )

                val documentRef = registrationsCollection.add(registration).await()
                Result.success(documentRef.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getUserRegistrations(userId: String): Result<List<Registration>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = registrationsCollection
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val registrations = snapshot.documents.mapNotNull { document ->
                    document.toObject(Registration::class.java)?.copy(id = document.id)
                }
                Result.success(registrations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun cancelRegistration(registrationId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // Option 1: Mettre à jour le statut
                registrationsCollection.document(registrationId)
                    .update("status", "cancelled")
                    .await()

                // Option 2: Supprimer l'enregistrement
                // registrationsCollection.document(registrationId).delete().await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getRegistrationsByEvent(eventId: String): Result<List<Registration>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = registrationsCollection
                    .whereEqualTo("eventId", eventId)
                    .whereEqualTo("status", "confirmed") // Seulement les inscriptions confirmées
                    .get()
                    .await()

                val registrations = snapshot.documents.mapNotNull { document ->
                    document.toObject(Registration::class.java)?.copy(id = document.id)
                }
                Result.success(registrations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}