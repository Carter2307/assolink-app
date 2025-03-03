package com.assolink.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.assolink.data.model.User
import com.assolink.data.remote.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val context: Context
) {
    private val usersCollection = firestore.collection("users")
    private val prefs: SharedPreferences = context.getSharedPreferences("assolink_prefs", Context.MODE_PRIVATE)

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun getUserProfile(uid: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val document = usersCollection.document(uid).get().await()

                if (document.exists()) {
                    val data = document.data ?: emptyMap()

                    fun Map<String, Any?>.getStringListSafely(key: String): List<String> {
                        val value = this[key]
                        return when {
                            value == null -> emptyList()
                            value is List<*> -> value.filterIsInstance<String>()
                            else -> emptyList()
                        }
                    }

                    val user = User(
                        id = uid,
                        email = data["email"] as? String ?: "",
                        username = data["username"] as? String ?: "",
                        address = data["address"] as? String,
                        preferences = data.getStringListSafely("preferences"),
                        favoriteAssociations = data.getStringListSafely("favoriteAssociations"),
                        registeredEvents = data.getStringListSafely("registeredEvents")
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("Profil utilisateur non trouvé"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun signIn(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    getUserProfile(firebaseUser.uid)
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
                val firebaseUser = authResult.user!!

                val userProfile = mapOf(
                    "uid" to firebaseUser.uid,
                    "email" to email,
                    "username" to username,
                    "address" to address,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "preferences" to emptyList<String>(),
                    "favoriteAssociations" to emptyList<String>(),
                    "registeredEvents" to emptyList<String>()
                )

                usersCollection.document(firebaseUser.uid).set(userProfile).await()

                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    username = username,
                    address = address,
                    preferences = emptyList(),
                    favoriteAssociations = emptyList(),
                    registeredEvents = emptyList()
                )
                Result.success(user)

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

    // Préférences de thème
    fun saveDarkModePreference(isDarkMode: Boolean) {
        prefs.edit().putBoolean("dark_mode_enabled", isDarkMode).apply()
    }

    fun getDarkModePreference(): Boolean {
        return prefs.getBoolean("dark_mode_enabled", false)
    }
}