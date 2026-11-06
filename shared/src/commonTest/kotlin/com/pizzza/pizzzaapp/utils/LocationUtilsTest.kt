package com.pizzza.pizzzaapp.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class LocationUtilsTest {

    @Test
    fun testCalculateDeliveryPriceFullKilometersRoundingUp() {
        val pricePerKm = 40.0

        // 0.5 km -> rounds up to 1 km -> 1 * 40 = 40.0
        assertEquals(40.0, LocationUtils.calculateDeliveryPrice(0.5, pricePerKm))

        // 1.2 km -> rounds up to 2 km -> 2 * 40 = 80.0
        assertEquals(80.0, LocationUtils.calculateDeliveryPrice(1.2, pricePerKm))

        // 4.1 km -> rounds up to 5 km -> 5 * 40 = 200.0
        assertEquals(200.0, LocationUtils.calculateDeliveryPrice(4.1, pricePerKm))

        // Exactly 3.0 km -> 3 * 40 = 120.0
        assertEquals(120.0, LocationUtils.calculateDeliveryPrice(3.0, pricePerKm))
    }
}
