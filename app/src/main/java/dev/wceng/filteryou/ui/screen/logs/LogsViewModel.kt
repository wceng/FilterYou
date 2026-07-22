package dev.wceng.filteryou.ui.screen.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.wceng.filteryou.data.model.InterceptedLog
import dev.wceng.filteryou.data.repository.FilterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LogsUiState(
    val logs: List<InterceptedLog> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val filterRepository: FilterRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    val uiState: StateFlow<LogsUiState> = combine(
        filterRepository.allLogs,
        _searchQuery
    ) { logs, query ->
        val filteredLogs = if (query.isBlank()) {
            logs
        } else {
            logs.filter {
                it.sender.contains(query, ignoreCase = true) ||
                it.reason.contains(query, ignoreCase = true)
            }
        }
        LogsUiState(logs = filteredLogs, searchQuery = query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogsUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteLog(log: InterceptedLog) {
        viewModelScope.launch {
            filterRepository.deleteLog(log)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            filterRepository.deleteAllLogs()
        }
    }
}
