package com.utkarsh.beatzmusicplayer.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 4.dp,
    thumbRadius: Dp = 8.dp,
    activeTrackColor: Color = MaterialTheme.colorScheme.primary,
    inactiveTrackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    horizontalPadding: Dp = 12.dp,
    onValueChangeFinished: (() -> Unit)? = null,
    activeTrackBrush: Brush? = null // optional gradient brush
) {
    var sliderWidth by remember { mutableStateOf(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedRadius by animateDpAsState(
        targetValue = if (isPressed) thumbRadius * 1.4f else thumbRadius
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbRadius * 2)
            .padding(vertical = 6.dp, horizontal = horizontalPadding)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    sliderWidth.takeIf { it > 0f }?.let { width ->
                        val fraction = (offset.x / width).coerceIn(0f, 1f)
                        val newValue = valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                        onValueChangeFinished?.invoke()
                    }
                }
            }
    ) {
        sliderWidth = size.width
        val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        val trackY = size.height / 2

        // Inactive track
        drawLine(
            color = inactiveTrackColor,
            start = Offset(0f, trackY),
            end = Offset(size.width, trackY),
            strokeWidth = trackHeight.toPx(),
            cap = StrokeCap.Round
        )

        // Active track (use gradient if provided, else single color)
        val brushToUse = activeTrackBrush ?: androidx.compose.ui.graphics.SolidColor(activeTrackColor)
        drawLine(
            brush = brushToUse,
            start = Offset(0f, trackY),
            end = Offset(size.width * fraction, trackY),
            strokeWidth = trackHeight.toPx(),
            cap = StrokeCap.Round
        )

        // Thumb
        drawCircle(
            color = thumbColor,
            radius = animatedRadius.toPx(),
            center = Offset(size.width * fraction, trackY)
        )
    }
}