package dev.wceng.filteryou.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_rules")
data class FilterRule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val pattern: String,
    val strategy: RuleStrategy,
    val ruleType: String = "BLOCK", // "BLOCK" or "ALLOW"
    val isActive: Boolean = true
)
