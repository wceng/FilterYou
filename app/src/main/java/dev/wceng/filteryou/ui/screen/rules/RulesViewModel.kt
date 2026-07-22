package dev.wceng.filteryou.ui.screen.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.wceng.filteryou.data.model.FilterRule
import dev.wceng.filteryou.data.model.RuleStrategy
import dev.wceng.filteryou.data.repository.FilterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RulesUiState(
    val rules: List<FilterRule> = emptyList()
)

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val filterRepository: FilterRepository
) : ViewModel() {

    val uiState: StateFlow<RulesUiState> = filterRepository.allRules
        .map { RulesUiState(rules = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RulesUiState()
        )

    fun getNextDefaultName(): String {
        return "规则 ${uiState.value.rules.size + 1}"
    }

    fun getRuleDescription(rule: FilterRule): String {
        val prefix = if (rule.ruleType == "ALLOW") "放行" else "拦截"
        return when (rule.strategy) {
            RuleStrategy.CONTAINS -> "${prefix}包含 \"${rule.pattern}\" 的电话"
            RuleStrategy.STARTS_WITH -> "${prefix}开头为 \"${rule.pattern}\" 的电话"
            RuleStrategy.ENDS_WITH -> "${prefix}结尾为 \"${rule.pattern}\" 的电话"
            RuleStrategy.REGEX -> "${prefix}正则匹配 \"${rule.pattern}\" 的电话"
            RuleStrategy.LOCATION -> "${prefix}地区为 \"${rule.pattern}\" 的电话"
        }
    }

    fun addRule(name: String, pattern: String, strategy: RuleStrategy, ruleType: String) {
        viewModelScope.launch {
            filterRepository.insertRule(
                FilterRule(
                    name = if (name.isBlank()) getNextDefaultName() else name,
                    pattern = pattern,
                    strategy = strategy,
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

    fun restoreRule(rule: FilterRule) {
        viewModelScope.launch {
            filterRepository.insertRule(rule)
        }
    }
}
