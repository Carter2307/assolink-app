package com.assolink.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.assolink.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<UserEntity>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<UserEntity>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): UserEntity

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert
    suspend fun insertAll(vararg users: UserEntity)

    @Delete
    fun delete(user: UserEntity)
}