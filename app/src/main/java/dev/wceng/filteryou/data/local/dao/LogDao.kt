package dev.wceng.filteryou.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.wceng.filteryou.data.model.InterceptedLog
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM intercepted_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<InterceptedLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: InterceptedLog)

    @Delete
    suspend fun deleteLog(log: InterceptedLog)

    @Query("DELETE FROM intercepted_logs")
    suspend fun deleteAllLogs()
}
