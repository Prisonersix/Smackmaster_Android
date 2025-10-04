package com.smackmaster.android.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smackmaster.android.BuildConfig
import com.smackmaster.android.model.Tone
import com.smackmaster.android.ui.components.GlassCard
import com.smackmaster.android.ui.components.NeonButton
import com.smackmaster.android.ui.components.NeonPill
import com.smackmaster.android.ui.theme.PremiumBlueOutline
import com.smackmaster.android.ui.theme.PremiumCrimsonRed
import com.smackmaster.android.ui.theme.PremiumDeepBlack
import com.smackmaster.android.ui.theme.PremiumNeonGreen
import com.smackmaster.android.ui.theme.PremiumNeonYellow
import com.smackmaster.android.ui.theme.PremiumSubtextGray
import com.smackmaster.android.ui.theme.PremiumTextWhite
import com.smackmaster.android.ui.theme.SmackMasterTheme
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SmackMasterApp(
    modifier: Modifier = Modifier,
    viewModel: SmackMasterViewModel = viewModel(),
    onClose: () -> Unit = {},
) {
    SmackMasterTheme {
        val clipboardManager = LocalClipboardManager.current
        val haptics = LocalHapticFeedback.current
        val toastMessage = viewModel.toastMessage

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(PremiumDeepBlack.copy(alpha = 0.92f), Color.Black),
                        center = Offset.Zero,
                        radius = 1800f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.isMinimized) {
                MinimizedDot(
                    modifier = Modifier.align(Alignment.BottomStart),
                    onRestore = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        viewModel.restore()
                    }
                )
            } else {
                SmackMasterSurface(
                    viewModel = viewModel,
                    onCopy = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        viewModel.copy { clipboardManager.setText(AnnotatedString(it)) }
                    },
                    onClear = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        viewModel.clear()
                    },
                    onMinimize = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        viewModel.minimize()
                    },
                    onClose = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onClose()
                    },
                    onToneClicked = { tone ->
                        viewModel.setTone(tone)
                        viewModel.requestRoast(
                            baseUrl = BuildConfig.API_BASE_URL,
                            endpoint = BuildConfig.ROAST_ENDPOINT
                        )
                    },
                    onMicClick = {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        viewModel.toggleMicGlow()
                    }
                )
            }

            AnimatedVisibility(
                visible = !toastMessage.isNullOrEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            ) {
                toastMessage?.let { message ->
                    ToastChip(message = message)
                }
            }
        }

        LaunchedEffect(toastMessage) {
            if (toastMessage != null) {
                viewModel.scheduleToastClear()
            }
        }
    }
}

@Composable
private fun SmackMasterSurface(
    viewModel: SmackMasterViewModel,
    onCopy: () -> Unit,
    onClear: () -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onToneClicked: (Tone) -> Unit,
    onMicClick: () -> Unit,
) {
    GlassCard(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 48.dp),
        alpha = 0.88f,
        blurRadius = 24.dp,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HeaderRow(
                onCopy = onCopy,
                onClear = onClear,
                onMinimize = onMinimize,
                onClose = onClose,
                copyEnabled = viewModel.roast.isNotBlank() && !viewModel.isProcessing,
                clearEnabled = !viewModel.isProcessing
            )

            CommentField(
                value = viewModel.comment,
                onValueChange = viewModel::updateComment,
                placeholder = "Paste something spicy to roast...",
                isLoading = viewModel.isProcessing
            )

            viewModel.warningMessage?.let { warning ->
                Text(
                    text = warning,
                    style = MaterialTheme.typography.bodySmall,
                    color = PremiumCrimsonRed,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            AnimatedVisibility(visible = viewModel.roast.isNotBlank()) {
                GlassCard(
                    modifier = Modifier,
                    alpha = 0.9f,
                    blurRadius = 12.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = viewModel.roast,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PremiumTextWhite,
                        modifier = Modifier.animateContentSize()
                    )
                }
            }

            ToneSelector(
                selectedTone = viewModel.selectedTone,
                onToneClicked = onToneClicked,
                isProcessing = viewModel.isProcessing
            )

            BottomRow(
                onMicClick = onMicClick,
                micGlowing = viewModel.isMicGlowing
            )
        }
    }
}

@Composable
private fun HeaderRow(
    onCopy: () -> Unit,
    onClear: () -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    copyEnabled: Boolean,
    clearEnabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy((-4).dp)) {
            Text(
                text = "Smack",
                color = PremiumTextWhite,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp)
            )
            Text(
                text = "Master",
                color = PremiumTextWhite,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NeonButton(
                text = "Copy",
                onClick = onCopy,
                enabled = copyEnabled,
                outlineColor = PremiumNeonGreen,
                fillColor = PremiumNeonGreen.copy(alpha = 0.85f),
                pressedTextColor = PremiumDeepBlack,
                textColor = PremiumNeonGreen
            )
            NeonButton(
                text = "Clear",
                onClick = onClear,
                enabled = clearEnabled,
                outlineColor = PremiumCrimsonRed,
                fillColor = PremiumCrimsonRed.copy(alpha = 0.85f),
                pressedTextColor = PremiumTextWhite,
                textColor = PremiumCrimsonRed
            )
            NeonPill(
                label = "M",
                onClick = onMinimize,
                outlineColor = PremiumBlueOutline,
                fillColor = PremiumBlueOutline.copy(alpha = 0.7f),
                textColor = PremiumBlueOutline,
                pressedTextColor = PremiumTextWhite
            )
            NeonPill(
                label = "C",
                onClick = onClose,
                outlineColor = PremiumBlueOutline,
                fillColor = PremiumBlueOutline.copy(alpha = 0.7f),
                textColor = PremiumBlueOutline,
                pressedTextColor = PremiumTextWhite
            )
        }
    }
}

@Composable
private fun CommentField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isLoading: Boolean
) {
    val placeholderAlpha by animateFloatAsState(targetValue = if (value.isEmpty()) 0.65f else 0f, label = "placeholder_alpha")
    val focusColor = remember { PremiumNeonYellow.copy(alpha = 0.9f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(focusColor, focusColor.copy(alpha = 0.4f))),
                shape = MaterialTheme.shapes.large
            )
            .background(Color.Transparent)
            .padding(12.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = PremiumTextWhite),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 4.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { /* handled by tone buttons */ }),
            cursorBrush = Brush.radialGradient(listOf(PremiumNeonYellow, PremiumNeonYellow.copy(alpha = 0.4f)))
        )
        if (placeholderAlpha > 0f) {
            Text(
                text = placeholder,
                color = PremiumSubtextGray.copy(alpha = placeholderAlpha),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (isLoading) {
            PulsingOverlay()
        }
    }
}

@Composable
private fun PulsingOverlay() {
    val pulse = rememberInfiniteTransition(label = "pulsing_overlay")
    val alpha by pulse.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 520, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsing_alpha"
    )
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(PremiumNeonYellow.copy(alpha = alpha))
    )
}

@Composable
private fun ToneSelector(
    selectedTone: Tone,
    onToneClicked: (Tone) -> Unit,
    isProcessing: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Choose the vibe",
            style = MaterialTheme.typography.bodyMedium,
            color = PremiumSubtextGray
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Tone.entries.forEach { tone ->
                val isSelected = tone == selectedTone
                NeonButton(
                    text = tone.label,
                    onClick = { onToneClicked(tone) },
                    enabled = !isProcessing,
                    outlineColor = PremiumNeonYellow,
                    fillColor = PremiumNeonYellow.copy(alpha = if (isSelected) 0.9f else 0.6f),
                    textColor = if (isSelected) PremiumDeepBlack else PremiumNeonYellow,
                    pressedTextColor = PremiumDeepBlack,
                    isOutlineOnly = !isSelected
                )
            }
        }
    }
}

@Composable
private fun BottomRow(
    onMicClick: () -> Unit,
    micGlowing: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonButton(
            text = "Mic",
            onClick = onMicClick,
            outlineColor = PremiumBlueOutline,
            fillColor = if (micGlowing) PremiumBlueOutline.copy(alpha = 0.7f) else Color.Transparent,
            textColor = if (micGlowing) PremiumTextWhite else PremiumSubtextGray,
            pressedTextColor = PremiumTextWhite,
            isOutlineOnly = !micGlowing
        )
    }
}

@Composable
private fun ToastChip(message: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(PremiumDeepBlack.copy(alpha = 0.85f))
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(listOf(PremiumNeonGreen, PremiumNeonGreen.copy(alpha = 0.4f))),
                shape = CircleShape
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text = message, color = PremiumNeonGreen, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MinimizedDot(
    modifier: Modifier = Modifier,
    onRestore: () -> Unit
) {
    val position = remember { mutableStateOf(Offset(40f, -220f)) }
    val glowAlpha by animateFloatAsState(targetValue = 0.8f, label = "minimized_alpha")

    Box(
        modifier = modifier
            .offset { IntOffset(position.value.x.roundToInt(), position.value.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    position.value = position.value + dragAmount
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onRestore() })
            }
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                brush = Brush.radialGradient(listOf(PremiumNeonYellow.copy(alpha = glowAlpha), Color.Transparent)),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = PremiumDeepBlack.copy(alpha = 0.6f),
                radius = size.minDimension / 2
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(PremiumNeonYellow, Color.Transparent)),
                radius = size.minDimension / 2.4f,
                style = androidx.compose.ui.graphics.drawscope.Fill
            )
        }
    }
}
