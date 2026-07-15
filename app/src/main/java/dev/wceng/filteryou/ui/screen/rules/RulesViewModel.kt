package dev.wceng.filteryou.ui.screen.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wceng.filteryou.data.model.FilterRule
import dev.wceng.filteryou.data.repository.FilterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RulesUiState(
    val rules: List<FilterRule> = emptyList()
)

class RulesViewModel(
    private val filterRepository: FilterRepository
) : ViewModel() {

    val uiState: StateFlow<RulesUiState> = filterRepository.allRules
        .map { RulesUiState(rules = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RulesUiState()
        )

    fun addRule(type: String, pattern: String, ruleType: String) {
        viewModelScope.launch {
            filterRepository.insertRule(
                FilterRule(
                    type = type,
                    pattern = pattern,
                    ruleType = ruleType
                )
            )
        }
    }

    fun updateRule(rule: FilterRule) {
        viewModelScope.launch {
            filterRepository.updateRule(rule)
        }
    }

    fun deleteRule(rule: FilterRule) {
        viewModelScope.launch {
            filterRepository.deleteRule(rule)
        }
    }
}
