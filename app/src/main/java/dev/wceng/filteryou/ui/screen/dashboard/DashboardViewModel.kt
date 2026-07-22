package dev.wceng.filteryou.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.wceng.filteryou.data.repository.FilterRepository
import dev.wceng.filteryou.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalBlocked: Int = 0,
    val isCallProtectionEnabled: Boolean = true,
    val recentLogs: List<dev.wceng.filteryou.data.model.InterceptedLog> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val filterRepository: FilterRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        filterRepository.allLogs,
        settingsRepository.callProtectionEnabled
    ) { logs, callEnabled ->
        DashboardUiState(
            totalBlocked = logs.size,
            isCallProtectionEnabled = callEnabled,
            recentLogs = logs.take(5)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun toggleCallProtection(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setCallProtectionEnabled(enabled)
        }
    }
}
