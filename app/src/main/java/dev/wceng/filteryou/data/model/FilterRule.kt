package dev.wceng.filteryou.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_rules")
data class FilterRule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "SMS", "CALL", or "BOTH"
    val pattern: String,
    val ruleType: String, // "BLOCK" or "ALLOW"
    val isActive: Boolean = true
)
