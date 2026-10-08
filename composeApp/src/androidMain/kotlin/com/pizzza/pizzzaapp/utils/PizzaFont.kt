package com.pizzza.pizzzaapp.utils

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzaapp.R

val allerFont = FontFamily(Font(R.font.aller_bold))
val mouseFont = FontFamily(Font(R.font.mouse_deco))
val skiaFont = FontFamily(Font(R.font.skia_regular))

val textAller40: TextStyle
    get() = pizzaAller.headlineLarge
val textAller35: TextStyle
    get() = pizzaAller.headlineMedium
val textAller30: TextStyle
    get() = pizzaAller.headlineSmall
val textAller25: TextStyle
    get() = pizzaAller.titleLarge
val textAller22: TextStyle
    get() = pizzaAller.titleMedium
val textAller20: TextStyle
    get() = pizzaAller.titleSmall
val textAller18: TextStyle
    get() = pizzaAller.bodyLarge
val textAller16: TextStyle
    get() = pizzaAller.bodyMedium
val textAller14: TextStyle
    get() = pizzaAller.bodySmall
val textAller12: TextStyle
    get() = pizzaAller.labelLarge
val textAller10: TextStyle
    get() = pizzaAller.labelMedium
val textAller8: TextStyle
    get() = pizzaAller.labelSmall


val textMouse40: TextStyle
    get() = pizzaMouse.headlineLarge
val textMouse35: TextStyle
    get() = pizzaMouse.headlineMedium
val textMouse30: TextStyle
    get() = pizzaMouse.headlineSmall
val textMouse25: TextStyle
    get() = pizzaMouse.titleLarge
val textMouse22: TextStyle
    get() = pizzaMouse.titleMedium
val textMouse20: TextStyle
    get() = pizzaMouse.titleSmall
val textMouse18: TextStyle
    get() = pizzaMouse.bodyLarge
val textMouse16: TextStyle
    get() = pizzaMouse.bodyMedium
val textMouse14: TextStyle
    get() = pizzaMouse.bodySmall
val textMouse12: TextStyle
    get() = pizzaMouse.labelLarge
val textMouse10: TextStyle
    get() = pizzaMouse.labelMedium
val textMouse8: TextStyle
    get() = pizzaMouse.labelSmall

val textSkia40: TextStyle
    get() = pizzaSkia.headlineLarge
val textSkia35: TextStyle
    get() = pizzaSkia.headlineMedium
val textSkia30: TextStyle
    get() = pizzaSkia.headlineSmall
val textSkia25: TextStyle
    get() = pizzaSkia.titleLarge
val textSkia22: TextStyle
    get() = pizzaSkia.titleMedium
val textSkia20: TextStyle
    get() = pizzaSkia.titleSmall
val textSkia18: TextStyle
    get() = pizzaSkia.bodyLarge
val textSkia16: TextStyle
    get() = pizzaSkia.bodyMedium
val textSkia14: TextStyle
    get() = pizzaSkia.bodySmall
val textSkia12: TextStyle
    get() = pizzaSkia.labelLarge
val textSkia10: TextStyle
    get() = pizzaSkia.labelMedium
val textSkia8: TextStyle
    get() = pizzaSkia.labelSmall


private val pizzaAller = Typography(
    headlineLarge = TextStyle(
        fontFamily = allerFont,
        fontSize = 40.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = allerFont,
        fontSize = 35.sp,
        color = Color.White
    ),
    headlineSmall = TextStyle(
        fontFamily = allerFont,
        fontSize = 30.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = allerFont,
        fontSize = 25.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = allerFont,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleSmall = TextStyle(
        fontFamily = allerFont,
        fontSize = 20.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = allerFont,
        fontSize = 18.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = allerFont,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodySmall = TextStyle(
        fontFamily = allerFont,
        fontSize = 14.sp,
        color = Color.White
    ),
    labelLarge = TextStyle(
        fontFamily = allerFont,
        fontSize = 12.sp,
        color = Color.White
    ),
    labelMedium = TextStyle(
        fontFamily = allerFont,
        fontSize = 10.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = allerFont,
        fontSize = 8.sp,
        color = Color.White
    )
)

private val pizzaMouse = Typography(
    headlineLarge = TextStyle(
        fontFamily = mouseFont,
        fontSize = 40.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = mouseFont,
        fontSize = 35.sp,
        color = Color.White
    ),
    headlineSmall = TextStyle(
        fontFamily = mouseFont,
        fontSize = 30.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = mouseFont,
        fontSize = 25.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = mouseFont,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleSmall = TextStyle(
        fontFamily = mouseFont,
        fontSize = 20.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = mouseFont,
        fontSize = 18.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = mouseFont,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodySmall = TextStyle(
        fontFamily = mouseFont,
        fontSize = 14.sp,
        color = Color.White
    ),
    labelLarge = TextStyle(
        fontFamily = mouseFont,
        fontSize = 12.sp,
        color = Color.White
    ),
    labelMedium = TextStyle(
        fontFamily = mouseFont,
        fontSize = 10.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = mouseFont,
        fontSize = 8.sp,
        color = Color.White
    )
)

private val pizzaSkia = Typography(
    headlineLarge = TextStyle(
        fontFamily = skiaFont,
        fontSize = 40.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = skiaFont,
        fontSize = 35.sp,
        color = Color.White
    ),
    headlineSmall = TextStyle(
        fontFamily = skiaFont,
        fontSize = 30.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = skiaFont,
        fontSize = 25.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = skiaFont,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleSmall = TextStyle(
        fontFamily = skiaFont,
        fontSize = 20.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = skiaFont,
        fontSize = 18.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = skiaFont,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodySmall = TextStyle(
        fontFamily = skiaFont,
        fontSize = 14.sp,
        color = Color.White
    ),
    labelLarge = TextStyle(
        fontFamily = skiaFont,
        fontSize = 12.sp,
        color = Color.White
    ),
    labelMedium = TextStyle(
        fontFamily = skiaFont,
        fontSize = 10.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = skiaFont,
        fontSize = 8.sp,
        color = Color.White
    )
)