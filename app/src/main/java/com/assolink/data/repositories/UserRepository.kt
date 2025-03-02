package com.assolink.data.repositories

import com.assolink.data.model.User
import com.assolink.data.remote.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // Convertir en votre modèle User
                    val user = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        username = firebaseUser.displayName ?: "",
                        preferences = emptyList(),
                        favoriteAssociations = emptyList(),
                        registeredEvents = emptyList()
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("Authentification échouée"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun register(email: String, password: String, username: String, address: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // Sauvegarder les infos additionnelles dans Firestore
                    val userDoc = mapOf(
                        "uid" to firebaseUser.uid,
                        "email" to email,
                        "username" to username,
                        "address" to address,
                        "preferences" to emptyList<String>()
                    )

                    usersCollection.document(firebaseUser.uid).set(userDoc).await()

                    // Créer et retourner le modèle User
                    val user = User(
                        id = firebaseUser.uid,
                        email = email,
                        username = username,
                        preferences = emptyList(),
                        favoriteAssociations = emptyList(),
                        registeredEvents = emptyList()
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("Inscription échouée"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun resetPassword(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                auth.sendPasswordResetEmail(email).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun signOut() = auth.signOut()
}