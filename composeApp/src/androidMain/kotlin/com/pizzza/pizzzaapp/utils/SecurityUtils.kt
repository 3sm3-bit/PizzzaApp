package com.pizzza.pizzzaapp.utils

import android.content.Context
import com.valu.uitaycompose.utils.extension.uiTayIsDeveloperMode
import com.valu.uitaycompose.utils.extension.uiTayIsDeviceRooted
import com.valu.uitaycompose.utils.extension.uiTayIsEmulator

object SecurityUtils {

    enum class SecurityWarningType {
        NONE, ROOT, DEVELOPER_MODE, EMULATOR
    }

    var securityWarning: SecurityWarningType = SecurityWarningType.NONE
        private set


    fun verifyIntegrity(context: Context, isDebug: Boolean) {
        val isRoot = if(isDebug)false else context.uiTayIsDeviceRooted()
        val isDeveloperModeDetected = if(isDebug)false else context.uiTayIsDeveloperMode()
        val isEmu = if(isDebug)false else uiTayIsEmulator()

        securityWarning = when {
            isRoot -> SecurityWarningType.ROOT
            isEmu -> SecurityWarningType.EMULATOR
            isDeveloperModeDetected -> SecurityWarningType.DEVELOPER_MODE
            else -> SecurityWarningType.NONE
        }

    }
}
