package dev.wceng.filteryou.data.repository

import dev.wceng.filteryou.data.local.dao.LogDao
import dev.wceng.filteryou.data.local.dao.RuleDao
import dev.wceng.filteryou.data.model.FilterRule
import dev.wceng.filteryou.data.model.InterceptedLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterRepository @Inject constructor(
    private val logDao: LogDao,
    private val ruleDao: RuleDao
) {
    val allLogs: Flow<List<InterceptedLog>> = logDao.getAllLogs()
    val allRules: Flow<List<FilterRule>> = ruleDao.getAllRules()

    suspend fun insertLog(log: InterceptedLog) = logDao.insertLog(log)
    suspend fun deleteLog(log: InterceptedLog) = logDao.deleteLog(log)
    suspend fun deleteAllLogs() = logDao.deleteAllLogs()

    suspend fun insertRule(rule: FilterRule) = ruleDao.insertRule(rule)
    suspend fun updateRule(rule: FilterRule) = ruleDao.updateRule(rule)
    suspend fun deleteRule(rule: FilterRule) = ruleDao.deleteRule(rule)
    suspend fun getActiveRules() = ruleDao.getActiveRules()
}
