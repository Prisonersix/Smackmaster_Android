package com.smackmaster.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Interaction
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NeonButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    outlineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = outlineColor,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    pressedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    glowColor: Color = outlineColor,
    shapeRadius: Dp = 18.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    isOutlineOnly: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressedState = remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction: Interaction ->
            pressedState.value = interaction is Interaction.Pressed
        }
    }

    val pressed by pressedState
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, label = "neon_button_scale")
    val animatedColor by animateColorAsState(
        targetValue = if (!isOutlineOnly || pressed) fillColor else Color.Transparent,
        label = "neon_button_color"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (!isOutlineOnly || pressed) pressedTextColor else textColor,
        label = "neon_button_text_color"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(shapeRadius))
            .border(
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(outlineColor, glowColor.copy(alpha = 0.6f)))),
                shape = RoundedCornerShape(shapeRadius)
            )
            .background(Color.Transparent)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(animatedColor.copy(alpha = if (pressed) 0.85f else 0.65f))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = textStyle,
                color = animatedTextColor
            )
        }
    }
}

@Composable
fun NeonPill(
    label: String,
    modifier: Modifier = Modifier,
    outlineColor: Color,
    fillColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    glowColor: Color = outlineColor,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    pressedTextColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    NeonButton(
        text = label,
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        outlineColor = outlineColor,
        fillColor = fillColor,
        glowColor = glowColor,
        textColor = textColor,
        pressedTextColor = pressedTextColor,
        shapeRadius = 50.dp,
        textStyle = MaterialTheme.typography.labelLarge,
        isOutlineOnly = true
    )
}
