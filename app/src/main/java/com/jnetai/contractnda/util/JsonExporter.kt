package com.jnetai.contractnda.util

import com.google.gson.GsonBuilder
import com.jnetai.contractnda.model.Clause
import com.jnetai.contractnda.model.Contract
import java.time.LocalDate

data class ContractReviewExport(
    val contract: ContractInfo,
    val clauses: List<ClauseInfo>,
    val overallRiskScore: Float,
    val exportedAt: String
)

data class ContractInfo(
    val id: Long,
    val title: String,
    val type: String,
    val parties: String,
    val date: String,
    val keyTerms: String,
    val riskLevel: String
)

data class ClauseInfo(
    val id: Long,
    val clauseType: String,
    val title: String,
    val content: String,
    val riskScore: Int,
    val riskLevel: String,
    val isRedFlag: Boolean,
    val notes: String,
    val reviewed: Boolean
)

object JsonExporter {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun export(contract: Contract, clauses: List<Clause>, avgRisk: Float?): String {
        val contractInfo = ContractInfo(
            id = contract.id,
            title = contract.title,
            type = contract.type.label,
            parties = contract.parties,
            date = contract.date.toString(),
            keyTerms = contract.keyTerms,
            riskLevel = contract.riskLevel.label
        )

        val clauseInfos = clauses.map { c ->
            ClauseInfo(
                id = c.id,
                clauseType = c.clauseType.label,
                title = c.title,
                content = c.content,
                riskScore = c.riskScore,
                riskLevel = c.riskLevel.label,
                isRedFlag = c.isRedFlag,
                notes = c.notes,
                reviewed = c.reviewed
            )
        }

        val export = ContractReviewExport(
            contract = contractInfo,
            clauses = clauseInfos,
            overallRiskScore = avgRisk ?: 0f,
            exportedAt = java.time.LocalDateTime.now().toString()
        )

        return gson.toJson(export)
    }
}