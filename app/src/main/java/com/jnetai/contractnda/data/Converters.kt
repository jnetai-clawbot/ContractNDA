package com.jnetai.contractnda.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromContractType(type: com.jnetai.contractnda.model.ContractType): String = type.name

    @TypeConverter
    fun toContractType(value: String): com.jnetai.contractnda.model.ContractType =
        com.jnetai.contractnda.model.ContractType.valueOf(value)

    @TypeConverter
    fun fromRiskLevel(level: com.jnetai.contractnda.model.RiskLevel): String = level.name

    @TypeConverter
    fun toRiskLevel(value: String): com.jnetai.contractnda.model.RiskLevel =
        com.jnetai.contractnda.model.RiskLevel.valueOf(value)

    @TypeConverter
    fun fromClauseType(type: com.jnetai.contractnda.model.ClauseType): String = type.name

    @TypeConverter
    fun toClauseType(value: String): com.jnetai.contractnda.model.ClauseType =
        com.jnetai.contractnda.model.ClauseType.valueOf(value)
}