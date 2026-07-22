package dev.wceng.filteryou.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.wceng.filteryou.data.local.dao.LogDao
import dev.wceng.filteryou.data.local.dao.RuleDao
import dev.wceng.filteryou.data.model.FilterRule
import dev.wceng.filteryou.data.model.InterceptedLog

@Database(entities = [InterceptedLog::class, FilterRule::class], version = 3, exportSchema = false)
abstract class FilterYouDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun ruleDao(): RuleDao

    companion object {
        @Volatile
        private var INSTANCE: FilterYouDatabase? = null

        fun getDatabase(context: Context): FilterYouDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FilterYouDatabase::class.java,
                    "filteryou_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
