package dev.wceng.filteryou.data.repository

import dev.wceng.filteryou.data.local.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val preferencesDataStore: PreferencesDataStore) {
    val callProtectionEnabled: Flow<Boolean> = preferencesDataStore.callProtectionEnabled

    suspend fun setCallProtectionEnabled(enabled: Boolean) {
        preferencesDataStore.updateCallProtection(enabled)
    }
}
