package com.jnetai.contractnda.data

import androidx.room.*
import com.jnetai.contractnda.model.Clause

@Dao
interface ClauseDao {
    @Query("SELECT * FROM clauses WHERE contractId = :contractId ORDER BY `order`")
    suspend fun getByContractId(contractId: Long): List<Clause>

    @Query("SELECT * FROM clauses WHERE id = :id")
    suspend fun getById(id: Long): Clause?

    @Insert
    suspend fun insert(clause: Clause): Long

    @Update
    suspend fun update(clause: Clause)

    @Delete
    suspend fun delete(clause: Clause)

    @Query("DELETE FROM clauses WHERE contractId = :contractId")
    suspend fun deleteByContractId(contractId: Long)

    @Query("SELECT AVG(riskScore) FROM clauses WHERE contractId = :contractId")
    suspend fun getAverageRiskScore(contractId: Long): Float?
}