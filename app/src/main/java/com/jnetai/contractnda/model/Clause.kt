package com.jnetai.contractnda.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clauses",
    foreignKeys = [ForeignKey(
        entity = Contract::class,
        parentColumns = ["id"],
        childColumns = ["contractId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("contractId")]
)
data class Clause(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contractId: Long,
    val clauseType: ClauseType,
    val title: String,
    val content: String,
    val riskScore: Int = 0, // 0-100
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val isRedFlag: Boolean = false,
    val notes: String = "",
    val reviewed: Boolean = false,
    val order: Int = 0
)

enum class ClauseType(val label: String) {
    LIABILITY("Liability"),
    INDEMNIFICATION("Indemnification"),
    TERMINATION("Termination"),
    IP("Intellectual Property"),
    NON_COMPETE("Non-Compete"),
    CONFIDENTIALITY("Confidentiality"),
    WARRANTY("Warranty"),
    PAYMENT("Payment Terms"),
    DISPUTE("Dispute Resolution"),
    FORCE_MAJEURE("Force Majeure"),
    ASSIGNMENT("Assignment"),
    GOVERNING_LAW("Governing Law"),
    SEVERABILITY("Severability"),
    AMENDMENT("Amendment"),
    OTHER("Other")
}