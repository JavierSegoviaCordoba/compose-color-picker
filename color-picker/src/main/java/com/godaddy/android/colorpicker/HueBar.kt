package com.godaddy.android.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Hue side bar Component that invokes onHueChanged when the value is mutated.
 *
 * @param modifier modifiers to set to the hue bar.
 * @param currentColor the initial color to set on the hue bar.
 * @param onHueChanged the callback that is invoked when hue value changes. Hue is between 0 - 360.
 */
@Composable
internal fun HueBar(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onHueChanged: (Float) -> Unit
) {
    val rainbowBrush = remember {
        Brush.verticalGradient(getRainbowColors())
    }
    Canvas(modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .pointerInput(Unit) {
            val size = this.size
            detectTapGestures(
                onTap = { offset ->
                    if (!offset.isValid()) return@detectTapGestures
                    onHueChanged(getHueFromPoint(offset.y, size.height.toFloat()))
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures { pointerInput, _ ->
                if (!pointerInput.position.isValid()) return@detectDragGestures
                pointerInput.consumeAllChanges()
                onHueChanged(getHueFromPoint(pointerInput.position.y, size.height.toFloat()))
            }
        }
    ) {
        drawRect(rainbowBrush)
        drawRect(Color.Gray, style = Stroke(0.5.dp.toPx()))

        val huePoint = getPointFromHue(color = currentColor, height = this.size.height)
        drawSelectorIndicator(huePoint, horizontal = false)
    }
}

private fun getRainbowColors() : List<Color> {
    return listOf(
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
}

private fun getPointFromHue(color: HsvColor, height: Float): Float {
    return height - (color.hue * height / 360f)
}

private fun getHueFromPoint(y: Float, height: Float): Float {
    val newY = y.coerceIn(0f, height)
    return 360f - newY * 360f / height
}
