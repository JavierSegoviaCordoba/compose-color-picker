package com.godaddy.android.colorpicker.disc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.HsvColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun HueRing(modifier: Modifier = Modifier,
                     currentColor: HsvColor,
                     onHueChanged: (Float) -> Unit
) {
    val rainbowBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFFFF0000),
                Color(0xFFFF8000),
                Color(0xFFFFFF00),
                Color(0xFF80FF00),
                Color(0xFF00FF00),
                Color(0xFF00FF80),
                Color(0xFF00FFFF),
                Color(0xFF0080FF),
                Color(0xFF0000FF),
                Color(0xFF8000FF),
                Color(0xFFFF00FF),
                Color(0xFFFF0040),
            ).reversed()
        )
    }

    Canvas(modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .pointerInput(Unit) {
            val size = this.size
            detectTapGestures(
                onTap = { offset ->
                    if (!offset.isValid()) return@detectTapGestures
                    val hue = hueFromPoint(offset, size)
                    onHueChanged(hue)
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures { pointerInput, _ ->
                if (!pointerInput.position.isValid()) return@detectDragGestures
                pointerInput.consumeAllChanges()
                val hue = hueFromPoint(pointerInput.position, size)
                onHueChanged(hue)
            }
        }
    ) {

        drawArc(brush = rainbowBrush, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(24.dp.toPx()))

        val circleRadius = size.width / 2f
        val centerPoint = pointFromHue(currentColor.hue, circleRadius) + Offset(size.width / 2f, size.height / 2f)

        drawCircle(color = Color.White, radius = 10.dp.toPx(), style = Stroke(2.dp.toPx()), center = centerPoint)
    }
}

/**
 * Returns a value between 0 - 360, representing the hue, based on the offset and size passed in.
 *
 */
private fun hueFromPoint(offset: Offset, size: IntSize): Float {
    val radius = size.width / 2f
    val hueRadians = atan2((offset.x - radius), (offset.y - radius) )
    var hueDegrees = hueRadians * (180f / Math.PI.toFloat())
    if (hueDegrees < 0){
        hueDegrees += 360f
    }
    return hueDegrees
}

private fun pointFromHue(hue: Float, radius: Float): Offset {
    val hueRadians = Math.toRadians(hue.toDouble()).toFloat() + Math.PI.toFloat() / 2f
    val x = radius * sin(hueRadians)
    val y = radius * cos(hueRadians)
    return Offset(x, y)
}