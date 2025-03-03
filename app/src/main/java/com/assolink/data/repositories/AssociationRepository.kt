package com.assolink.data.repositories

import android.util.Log
import com.assolink.data.local.daos.AssociationDao
import com.assolink.data.local.mapper.EntityMappers
import com.assolink.data.model.Association
import com.assolink.data.remote.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AssociationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val associationDao: AssociationDao
) {
    private val associationsCollection = firestore.collection("associations")

    // Dans AssociationRepository
    suspend fun getAllAssociations(forceRefresh: Boolean = false): Result<List<Association>> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("Repository", "Début de getAllAssociations(forceRefresh=$forceRefresh)")

                // Essayer le cache local d'abord
                if (!forceRefresh) {
                    val localAssociations = associationDao.getAllAssociations()
                    Log.d("Repository", "Associations locales: ${localAssociations.size}")
                    if (localAssociations.isNotEmpty()) {
                        val mappedAssociations = localAssociations.map { EntityMappers.mapEntityToAssociation(it) }
                        Log.d("Repository", "Retourne ${mappedAssociations.size} associations depuis le cache")
                        return@withContext Result.success(mappedAssociations)
                    }
                }

                // Sinon, récupérer depuis Firestore
                Log.d("Repository", "Récupération depuis Firestore...")
                val snapshot = associationsCollection.get().await()
                Log.d("Repository", "Réponse Firestore reçue: ${snapshot.documents.size} documents")

                val associations = snapshot.documents.mapNotNull { document ->
                    try {
                        val association = document.toObject(Association::class.java)?.copy(id = document.id)
                        Log.d("Repository", "Association mappée: ${association?.name}")
                        association
                    } catch (e: Exception) {
                        Log.e("Repository", "Erreur de mapping pour ${document.id}", e)
                        null
                    }
                }

                // Mettre en cache
                val entities = associations.map { EntityMappers.mapAssociationToEntity(it) }
                Log.d("Repository", "Mise en cache de ${entities.size} associations")
                associationDao.insertAssociations(entities)

                Log.d("Repository", "Retourne ${associations.size} associations depuis Firestore")
                Result.success(associations)
            } catch (e: Exception) {
                Log.e("Repository", "Erreur dans getAllAssociations", e)

                // En cas d'erreur, essayer de récupérer du cache
                try {
                    val localAssociations = associationDao.getAllAssociations()
                    if (localAssociations.isNotEmpty()) {
                        val mappedAssociations = localAssociations.map { EntityMappers.mapEntityToAssociation(it) }
                        Log.d("Repository", "Fallback: retourne ${mappedAssociations.size} associations depuis le cache")
                        return@withContext Result.success(mappedAssociations)
                    }
                } catch (cacheError: Exception) {
                    Log.e("Repository", "Erreur de fallback vers le cache", cacheError)
                }

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
                Log.d("Repository", "Recherche d'associations: '$query'")

                if (query.isBlank()) {
                    return@withContext getAllAssociations()
                }

                // Firebase ne supporte pas les recherches textuelles complètes
                // On utilise une méthode alternative avec plusieurs champs
                val lowerQuery = query.lowercase()

                // Créer plusieurs requêtes pour différents champs
                val nameStartsWithQuery = associationsCollection
                    .orderBy("name")
                    .startAt(lowerQuery)
                    .endAt(lowerQuery + '\uf8ff')
                    .get()
                    .await()

                val categoryEqualsQuery = associationsCollection
                    .whereEqualTo("category", query)
                    .get()
                    .await()

                // Combiner les résultats
                val resultMap = mutableMapOf<String, Association>()

                nameStartsWithQuery.documents.forEach { doc ->
                    doc.toObject(Association::class.java)?.copy(id = doc.id)?.let {
                        resultMap[doc.id] = it
                    }
                }

                categoryEqualsQuery.documents.forEach { doc ->
                    doc.toObject(Association::class.java)?.copy(id = doc.id)?.let {
                        resultMap[doc.id] = it
                    }
                }

                Log.d("Repository", "Recherche terminée: ${resultMap.size} résultats")
                Result.success(resultMap.values.toList())
            } catch (e: Exception) {
                Log.e("Repository", "Erreur de recherche: ${e.message}", e)
                Result.failure(e)
            }
        }
}