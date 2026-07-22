package dev.wceng.filteryou.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.wceng.filteryou.data.local.FilterYouDatabase
import dev.wceng.filteryou.data.local.dao.LogDao
import dev.wceng.filteryou.data.local.dao.RuleDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FilterYouDatabase {
        return FilterYouDatabase.getDatabase(context)
    }

    @Provides
    fun provideRuleDao(database: FilterYouDatabase): RuleDao {
        return database.ruleDao()
    }

    @Provides
    fun provideLogDao(database: FilterYouDatabase): LogDao {
        return database.logDao()
    }
}
