package com.tayler.pizzzaapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings


fun Context.validNetWork() = isConnected() && !isAirplaneModeActive()

fun Context.isAirplaneModeActive(): Boolean {
    return Settings.Global.getInt(
        this.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
}


@SuppressLint("MissingPermission")
fun Context.isConnected(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    return false
}