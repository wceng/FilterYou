package dev.wceng.filteryou.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.wceng.filteryou.data.model.FilterRule
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {
    @Query("SELECT * FROM filter_rules")
    fun getAllRules(): Flow<List<FilterRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: FilterRule)

    @Update
    suspend fun updateRule(rule: FilterRule)

    @Delete
    suspend fun deleteRule(rule: FilterRule)

    @Query("SELECT * FROM filter_rules WHERE isActive = 1")
    suspend fun getActiveRules(): List<FilterRule>
}
