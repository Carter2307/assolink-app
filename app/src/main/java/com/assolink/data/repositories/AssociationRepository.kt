package com.assolink.data.repositories

import com.assolink.data.model.association.Association
import com.assolink.data.remote.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AssociationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val associationsCollection = firestore.collection("associations")

    suspend fun getAllAssociations(forceRefresh: Boolean): Result<List<Association>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = associationsCollection.get().await()
                val associations = snapshot.documents.mapNotNull { document ->
                    document.toObject(Association::class.java)?.copy(id = document.id)
                }
                Result.success(associations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getAssociationById(id: String, forceRefresh: Boolean): Result<Association> =
        withContext(Dispatchers.IO) {
            try {
                val document = associationsCollection.document(id).get().await()
                if (document.exists()) {
                    val association = document.toObject(Association::class.java)?.copy(id = document.id)
                    if (association != null) {
                        Result.success(association)
                    } else {
                        Result.failure(Exception("Échec de conversion du document"))
                    }
                } else {
                    Result.failure(Exception("Association non trouvée"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getAssociationsByCategory(category: String): Result<List<Association>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = associationsCollection
                    .whereEqualTo("category", category)
                    .get()
                    .await()

                val associations = snapshot.documents.mapNotNull { document ->
                    document.toObject(Association::class.java)?.copy(id = document.id)
                }
                Result.success(associations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getFavoriteAssociations(favoriteIds: List<String>): Result<List<Association>> =
        withContext(Dispatchers.IO) {
            try {
                if (favoriteIds.isEmpty()) {
                    return@withContext Result.success(emptyList())
                }

                // Firebase permet un maximum de 10 valeurs dans une requête "in"
                val chunkedIds = favoriteIds.chunked(10)
                val allAssociations = mutableListOf<Association>()

                for (chunk in chunkedIds) {
                    val snapshot = associationsCollection
                        .whereIn("id", chunk)
                        .get()
                        .await()

                    val associations = snapshot.documents.mapNotNull { document ->
                        document.toObject(Association::class.java)?.copy(id = document.id)
                    }
                    allAssociations.addAll(associations)
                }

                Result.success(allAssociations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun searchAssociations(query: String): Result<List<Association>> =
        withContext(Dispatchers.IO) {
            try {
                // Firebase ne supporte pas les recherches textuelles natives
                val startAt = query.lowercase()
                val endAt = startAt + '\uf8ff' // caractère Unicode élevé

                val snapshot = associationsCollection
                    .orderBy("name", Query.Direction.ASCENDING)
                    .startAt(startAt)
                    .endAt(endAt)
                    .get()
                    .await()

                val associations = snapshot.documents.mapNotNull { document ->
                    document.toObject(Association::class.java)?.copy(id = document.id)
                }
                Result.success(associations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}