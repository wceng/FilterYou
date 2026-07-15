package dev.wceng.filteryou.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wceng.filteryou.data.repository.FilterRepository
import dev.wceng.filteryou.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalBlocked: Int = 0,
    val callsBlocked: Int = 0,
    val smsBlocked: Int = 0,
    val isSmsProtectionEnabled: Boolean = true,
    val isCallProtectionEnabled: Boolean = true,
    val recentLogs: List<dev.wceng.filteryou.data.model.InterceptedLog> = emptyList()
)

class DashboardViewModel(
    private val filterRepository: FilterRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        filterRepository.allLogs,
        settingsRepository.smsProtectionEnabled,
        settingsRepository.callProtectionEnabled
    ) { logs, smsEnabled, callEnabled ->
        DashboardUiState(
            totalBlocked = logs.size,
            callsBlocked = logs.count { it.type == "CALL" },
            smsBlocked = logs.count { it.type == "SMS" },
            isSmsProtectionEnabled = smsEnabled,
            isCallProtectionEnabled = callEnabled,
            recentLogs = logs.take(5)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun toggleSmsProtection(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSmsProtectionEnabled(enabled)
        }
    }

    fun toggleCallProtection(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setCallProtectionEnabled(enabled)
        }
    }
}
