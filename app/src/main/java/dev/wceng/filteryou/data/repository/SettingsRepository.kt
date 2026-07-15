package dev.wceng.filteryou.data.repository

import dev.wceng.filteryou.data.local.PreferencesDataStore
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val preferencesDataStore: PreferencesDataStore) {
    val smsProtectionEnabled: Flow<Boolean> = preferencesDataStore.smsProtectionEnabled
    val callProtectionEnabled: Flow<Boolean> = preferencesDataStore.callProtectionEnabled

    suspend fun setSmsProtectionEnabled(enabled: Boolean) {
        preferencesDataStore.updateSmsProtection(enabled)
    }

    suspend fun setCallProtectionEnabled(enabled: Boolean) {
        preferencesDataStore.updateCallProtection(enabled)
    }
}
