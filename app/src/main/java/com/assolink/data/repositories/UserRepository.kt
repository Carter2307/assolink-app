// app/src/main/java/com/assolink/data/repository/UserRepository.kt
package com.assolink.data.repositories

import com.assolink.data.local.daos.UserDao
import com.assolink.data.local.entities.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(email: String, password: String, firstName: String,
                             lastName: String, address: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = UserEntity(
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    address = address
                )
                userDao.insertAll(user)
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByEmail(email)
                if (user != null && user.password == password) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Invalid credentials"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}