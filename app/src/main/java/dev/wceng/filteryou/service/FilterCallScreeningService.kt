package dev.wceng.filteryou.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.AndroidEntryPoint
import dev.wceng.filteryou.data.model.InterceptedLog
import dev.wceng.filteryou.data.model.RuleStrategy
import dev.wceng.filteryou.data.repository.FilterRepository
import dev.wceng.filteryou.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FilterCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var filterRepository: FilterRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val phoneNumberUtil: PhoneNumberUtil by lazy { PhoneNumberUtil.getInstance() }

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
            
            // 1. First check Whitelist (ALLOW)
            val whitelistRule = activeRules.filter { it.ruleType == "ALLOW" }.find { rule ->
                checkMatch(phoneNumber, rule)
            }
            
            if (whitelistRule != null) {
                respondToCall(callDetails, CallResponse.Builder().build())
                return@launch
            }

            // 2. Then check Blacklist (BLOCK)
            val blacklistRule = activeRules.filter { it.ruleType == "BLOCK" }.find { rule ->
                checkMatch(phoneNumber, rule)
            }

            if (blacklistRule != null) {
                val response = CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(true)
                    .build()
                
                filterRepository.insertLog(
                    InterceptedLog(
                        sender = phoneNumber,
                        reason = "Matched rule: ${blacklistRule.name} (${blacklistRule.pattern})"
                    )
                )
                respondToCall(callDetails, response)
            } else {
                respondToCall(callDetails, CallResponse.Builder().build())
            }
        }
    }

    private fun checkMatch(phoneNumber: String, rule: dev.wceng.filteryou.data.model.FilterRule): Boolean {
        return when (rule.strategy) {
            RuleStrategy.CONTAINS -> phoneNumber.contains(rule.pattern, ignoreCase = true)
            RuleStrategy.STARTS_WITH -> phoneNumber.startsWith(rule.pattern)
            RuleStrategy.ENDS_WITH -> phoneNumber.endsWith(rule.pattern)
            RuleStrategy.REGEX -> try {
                Regex(rule.pattern).containsMatchIn(phoneNumber)
            } catch (e: Exception) {
                false
            }
            RuleStrategy.LOCATION -> try {
                val numberProto = phoneNumberUtil.parse(phoneNumber, "CN")
                val regionCode = phoneNumberUtil.getRegionCodeForNumber(numberProto)
                regionCode?.equals(rule.pattern, ignoreCase = true) ?: false
            } catch (e: Exception) {
                false
            }
        }
    }
}
