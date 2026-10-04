package com.pizzza.pizzzaapp.core.ui.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.pizzza.pizzzaapp.core.ui.R
import com.valu.uitaycompose.utils.COUNTRY_CODE_PE
import com.valu.uitaycompose.utils.extension.uiTayExistApplicationInDevice
import com.valu.uitaycompose.utils.extension.uiTayIsWhatsAppInstalled
import com.valu.uitaycompose.utils.extension.uiTayShowToast

fun Context.openWhatsApp(phone: String, text: String,code : String = COUNTRY_CODE_PE) {
    if (existWhatsAppInDevice(this)) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                "$URL_WHATS_APP_CUSTOM$code$phone&text=$text".toUri()
            )
        )
    } else {
        this.uiTayShowToast(R.string.error_not_install)
    }
}

fun existWhatsAppInDevice(context: Context): Boolean {
    return uiTayIsWhatsAppInstalled(context, PACKAGE_APP_WHATS_APP)
            || uiTayIsWhatsAppInstalled(context, PACKAGE_APP_WHATS_APP_BUSINESS)|| uiTayExistApplicationInDevice(context, PACKAGE_APP_WHATS_APP)
            || uiTayExistApplicationInDevice(context, PACKAGE_APP_WHATS_APP_BUSINESS)
}

fun Context.openEmail(email: String = "lapizzzeriaoutlook.com") {
    try {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri())
        startActivity(intent)
    } catch (_: Exception) {
        this.uiTayShowToast(R.string.error_not_install)
    }
}