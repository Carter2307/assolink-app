package com.assolink.data.local.daos

import androidx.room.*
import com.assolink.data.local.entities.AssociationEntity

@Dao
interface AssociationDao {
    @Query("SELECT * FROM associations")
    suspend fun getAllAssociations(): List<AssociationEntity>

    @Query("SELECT * FROM associations WHERE id = :id")
    suspend fun getAssociationById(id: String): AssociationEntity?

    @Query("SELECT * FROM associations WHERE category = :category")
    suspend fun getAssociationsByCategory(category: String): List<AssociationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociations(associations: List<AssociationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociation(association: AssociationEntity)

    @Query("DELETE FROM associations")
    suspend fun deleteAllAssociations()
}