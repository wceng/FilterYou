package dev.wceng.filteryou.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.wceng.filteryou.ui.screen.dashboard.DashboardScreen
import dev.wceng.filteryou.ui.screen.dashboard.DashboardViewModel
import dev.wceng.filteryou.ui.screen.logs.LogsScreen
import dev.wceng.filteryou.ui.screen.logs.LogsViewModel
import dev.wceng.filteryou.ui.screen.rules.RulesScreen
import dev.wceng.filteryou.ui.screen.rules.RulesViewModel

@Composable
fun FilterYouNavigation() {
    val backStack = rememberNavBackStack(NavRoute.Dashboard as NavKey)

    val myEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider<NavKey> {
        addEntryProvider(NavRoute.Dashboard) {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = viewModel,
                onViewAllLogs = { backStack.add(NavRoute.Logs) },
                onManageRules = { backStack.add(NavRoute.Rules) }
            )
        }
        addEntryProvider(NavRoute.Logs) {
            val viewModel: LogsViewModel = hiltViewModel()
            LogsScreen(
                viewModel = viewModel,
                onNavigateBack = { backStack.removeAt(backStack.size - 1) }
            )
        }
        addEntryProvider(NavRoute.Rules) {
            val viewModel: RulesViewModel = hiltViewModel()
            RulesScreen(
                viewModel = viewModel,
                onNavigateBack = { backStack.removeAt(backStack.size - 1) }
            )
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeAt(backStack.size - 1) },
        entryProvider = myEntryProvider
    )
}
