package com.pizzza.pizzzaapp.utils

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationUtils {

    fun calculateDistanceInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val radLat1 = lat1 * PI / 180.0
        val radLat2 = lat2 * PI / 180.0

        val a = sin(dLat / 2.0) * sin(dLat / 2.0) +
                cos(radLat1) * cos(radLat2) * sin(dLon / 2.0) * sin(dLon / 2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return earthRadiusKm * c
    }

    fun calculateDeliveryPrice(
        distanceKm: Double,
        deliveryPricePerKm: Double,
        minimumDeliveryPrice: Double
    ): Double {
        val rawPrice = if (distanceKm <= 0.5) {
            minimumDeliveryPrice
        } else {
            distanceKm * deliveryPricePerKm
        }
        return kotlin.math.round(rawPrice)
    }
}
