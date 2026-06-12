// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Approximates iOS's `.glassCard(tint:)` (a `.ultraThinMaterial` card with a
 * tinted diagonal gradient and matching gradient border). True backdrop blur
 * would need `RenderEffect` (API 31+); this uses a translucent gradient fill
 * instead so it still looks right on minSdk 24.
 */
fun Modifier.glassCard(tint: Color = BrandGreen, cornerRadius: Float = 24f): Modifier {
    val shape = RoundedCornerShape(cornerRadius.dp)
    val fillBrush = Brush.linearGradient(
        colors = listOf(
            BrandWhite.copy(alpha = 0.42f),
            tint.copy(alpha = 0.16f),
            BrandViolet.copy(alpha = 0.14f),
            BrandVioletDark.copy(alpha = 0.10f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            BrandWhite.copy(alpha = 0.78f),
            tint.copy(alpha = 0.42f),
            BrandViolet.copy(alpha = 0.34f),
            BrandVioletDark.copy(alpha = 0.24f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    return this
        .shadow(elevation = 10.dp, shape = shape, ambientColor = tint.copy(alpha = 0.10f), spotColor = tint.copy(alpha = 0.10f))
        .background(color = Color.White.copy(alpha = 0.55f), shape = shape)
        .background(brush = fillBrush, shape = shape)
        .border(width = 1.dp, brush = borderBrush, shape = shape)
}
