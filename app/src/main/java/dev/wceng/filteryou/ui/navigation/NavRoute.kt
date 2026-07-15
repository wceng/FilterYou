package dev.wceng.filteryou.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavRoute : NavKey {
    @Serializable
    data object Dashboard : NavRoute

    @Serializable
    data object Logs : NavRoute

    @Serializable
    data object Rules : NavRoute
}
