package dev.wceng.filteryou.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intercepted_logs")
data class InterceptedLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "SMS" or "CALL"
    val sender: String,
    val body: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String
)
