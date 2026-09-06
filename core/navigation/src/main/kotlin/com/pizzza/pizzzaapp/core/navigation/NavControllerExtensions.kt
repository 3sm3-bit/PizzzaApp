package com.pizzza.pizzzaapp.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Gatekeeper para evitar múltiples navegaciones rápidas (double tap)
 */
object NavGatekeeper {
    private var lastNavTime = 0L
    private const val NAV_DELAY = 500L

    fun canNavigate(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastNavTime >= NAV_DELAY) {
            lastNavTime = now
            return true
        }
        return false
    }
}

/**
 * Extensión para navegar de forma segura evitando duplicar pantallas
 */
fun <T : Any> NavController.navigateSafe(
    route: T,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    if (NavGatekeeper.canNavigate()) {
        navigate(route) {
            launchSingleTop = true
            builder()
        }
    }
}
