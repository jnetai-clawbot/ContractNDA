package com.jnetai.contractnda.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jnetai.contractnda.data.Converters
import java.time.LocalDate

@Entity(tableName = "contracts")
@TypeConverters(Converters::class)
data class Contract(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: ContractType,
    val parties: String,
    val date: LocalDate,
    val keyTerms: String,
    val riskLevel: RiskLevel,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ContractType(val label: String) {
    NDA("NDA"),
    EMPLOYMENT("Employment Contract"),
    SERVICE("Service Agreement"),
    LEASE("Lease Agreement"),
    PARTNERSHIP("Partnership Agreement"),
    LICENSING("Licensing Agreement"),
    PURCHASE("Purchase Agreement"),
    OTHER("Other")
}

enum class RiskLevel(val label: String, val color: String) {
    LOW("Low", "#4CAF50"),
    MEDIUM("Medium", "#FF9800"),
    HIGH("High", "#F44336")
}