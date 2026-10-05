package com.pizzza.pizzzaapp.core.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
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

fun Context.openEmail(
    email: String = "lapizzzeria@outlook.com",
    subject: String = "Hola me gustaria consultar:",
    body: String = "Hola, me gustaría realizar la siguiente consulta:"
) {
    try {
        val uri = "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}".toUri()
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(intent)
    } catch (_: Exception) {
        this.uiTayShowToast(R.string.error_not_install)
    }
}

fun Context.uiTayUrlInstagram(username: String) {
    if (username.isNotEmpty()) {
        val cleanUsername = username.removePrefix("@")
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "http://instagram.com/_u/$cleanUsername".toUri()
            ).apply {
                setPackage("com.instagram.android")
            }
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://instagram.com/$cleanUsername".toUri()
                )
            )
        }
    } else {
        uiTayShowToast("Aun no esta configurado")
    }
}

fun Context.uiTayUrlTikTok(username: String) {
    if (username.isNotEmpty()) {
        val cleanUsername = if (username.startsWith("@")) username else "@$username"
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "snssdk1128://user/profile/$cleanUsername".toUri()
            ).apply {
                setPackage("com.zhiliaoapp.musically")
            }
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://www.tiktok.com/$cleanUsername".toUri()
                )
            )
        }
    } else {
        uiTayShowToast("Aun no esta configurado")
    }
}