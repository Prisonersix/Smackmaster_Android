package com.smackmaster.android.ui.components

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smackmaster.android.ui.theme.glassSurfaceBrush

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    blurRadius: Dp = 24.dp,
    alpha: Float = 0.85f,
    glowColor: Color = MaterialTheme.neonGlow,
    glowAlpha: Float = 0.25f,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val animatedAlpha by animateFloatAsState(targetValue = alpha, label = "glass_alpha")

    val baseModifier = modifier
        .graphicsLayer { this.alpha = animatedAlpha }
        .shadow(elevation = 24.dp, shape = shape, clip = false, ambientColor = glowColor.copy(alpha = glowAlpha))
    val blurredModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        baseModifier.blur(radius = blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
    } else {
        baseModifier
    }

    Box(
        modifier = blurredModifier
            .background(brush = MaterialTheme.glassSurfaceBrush, shape = shape)
            .border(width = 1.2.dp, brush = Brush.linearGradient(listOf(glowColor.copy(alpha = 0.65f), glowColor.copy(alpha = 0.2f))), shape = shape)
            .padding(contentPadding)
            .fillMaxWidth()
    ) {
        content()
    }
}
