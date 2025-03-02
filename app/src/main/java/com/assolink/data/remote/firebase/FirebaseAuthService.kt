package com.assolink.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                Result.success(authResult.user!!)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun register(email: String, password: String, username: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user!!

                // Créer le profil utilisateur dans Firestore
                val userProfile = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "username" to username,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "preferences" to emptyList<String>()
                )

                usersCollection.document(user.uid).set(userProfile).await()
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun getUserProfile(uid: String): Result<Map<String, Any>> =
        withContext(Dispatchers.IO) {
            try {
                val document = usersCollection.document(uid).get().await()
                if (document.exists()) {
                    Result.success(document.data ?: emptyMap())
                } else {
                    Result.failure(Exception("User profile not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                usersCollection.document(uid).update(updates).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}