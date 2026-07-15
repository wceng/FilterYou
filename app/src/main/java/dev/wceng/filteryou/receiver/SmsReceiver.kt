package dev.wceng.filteryou.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
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

class SmsReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_DELIVER_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val database = FilterYouDatabase.getDatabase(context)
        val preferencesDataStore = PreferencesDataStore(context)
        val filterRepository = FilterRepository(database.logDao(), database.ruleDao())
        val settingsRepository = SettingsRepository(preferencesDataStore)

        val firstMessage = messages[0]
        val sender = firstMessage.displayOriginatingAddress ?: ""
        val fullBody = messages.joinToString("") { it.displayMessageBody ?: "" }

        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                val isProtectionEnabled = settingsRepository.smsProtectionEnabled.first()
                if (!isProtectionEnabled) {
                    return@launch
                }

                val activeRules = filterRepository.getActiveRules()
                val matchingRule = activeRules.find { rule ->
                    (rule.type == "SMS" || rule.type == "BOTH") &&
                            (sender.contains(rule.pattern, ignoreCase = true) || 
                             fullBody.contains(rule.pattern, ignoreCase = true))
                }

                if (matchingRule != null && matchingRule.ruleType == "BLOCK") {
                    filterRepository.insertLog(
                        InterceptedLog(
                            type = "SMS",
                            sender = sender,
                            body = fullBody,
                            reason = "Matched rule: ${matchingRule.pattern}"
                        )
                    )
                    // Aborting broadcast to prevent it from reaching other apps if we are default
                    // Note: abortBroadcast() only works for ordered broadcasts. 
                    // SMS_DELIVER_ACTION is delivered specifically to the default SMS app.
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
