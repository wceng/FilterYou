package dev.wceng.filteryou.service

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import dev.wceng.filteryou.data.local.FilterYouDatabase
import dev.wceng.filteryou.data.local.PreferencesDataStore
import dev.wceng.filteryou.data.model.InterceptedLog
import dev.wceng.filteryou.data.repository.FilterRepository
import dev.wceng.filteryou.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FilterCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var filterRepository: FilterRepository
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        val database = FilterYouDatabase.getDatabase(this)
        val preferencesDataStore = PreferencesDataStore(this)
        filterRepository = FilterRepository(database.logDao(), database.ruleDao())
        settingsRepository = SettingsRepository(preferencesDataStore)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val handle = callDetails.handle
        val phoneNumber = handle?.schemeSpecificPart ?: ""

        serviceScope.launch {
            val isProtectionEnabled = settingsRepository.callProtectionEnabled.first()
            if (!isProtectionEnabled) {
                respondToCall(callDetails, CallResponse.Builder().build())
                return@launch
            }

            val activeRules = filterRepository.getActiveRules()
            val matchingRule = activeRules.find { rule ->
                (rule.type == "CALL" || rule.type == "BOTH") &&
                        phoneNumber.contains(rule.pattern, ignoreCase = true)
            }

            if (matchingRule != null && matchingRule.ruleType == "BLOCK") {
                val response = CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(true)
                    .build()
                
                filterRepository.insertLog(
                    InterceptedLog(
                        type = "CALL",
                        sender = phoneNumber,
                        body = null,
                        reason = "Matched rule: ${matchingRule.pattern}"
                    )
                )
                respondToCall(callDetails, response)
            } else {
                respondToCall(callDetails, CallResponse.Builder().build())
            }
        }
    }
}
