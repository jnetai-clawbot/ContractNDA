package com.jnetai.contractnda.data

import androidx.room.*
import com.jnetai.contractnda.model.Contract

@Dao
interface ContractDao {
    @Query("SELECT * FROM contracts ORDER BY updatedAt DESC")
    suspend fun getAll(): List<Contract>

    @Query("SELECT * FROM contracts WHERE id = :id")
    suspend fun getById(id: Long): Contract?

    @Insert
    suspend fun insert(contract: Contract): Long

    @Update
    suspend fun update(contract: Contract)

    @Delete
    suspend fun delete(contract: Contract)

    @Query("DELETE FROM contracts WHERE id = :id")
    suspend fun deleteById(id: Long)
}