package com.godaddy.android.colorpicker.harmony

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.HsvColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

@ExperimentalGraphicsApi
@Composable
fun HarmonyColorPicker(
    harmonyMode: ColorHarmonyMode,
    color: Color = Color.Red,
    onColorsChanged: (HsvColor, List<HsvColor>) -> Unit) {

    BoxWithConstraints(
        Modifier
            .padding(24.dp)
            .fillMaxSize()
            .aspectRatio(1f)
    ) {
        val diameter = constraints.maxWidth
        var hsvColor by remember { mutableStateOf(HsvColor.from(color)) }

        val colorWheel = remember(diameter) { ColorWheel(diameter) }

        val internalHarmonyMode = remember(harmonyMode) {
            mutableStateOf(harmonyMode)
        }

        val inputModifier = Modifier.pointerInput(colorWheel) {
            fun updateColorWheel(newPosition: Offset) {
                // Work out if the new position is inside the circle we are drawing, and has a
                // valid color associated to it. If not, keep the current position
                val newColor = colorWheel.colorForPosition(newPosition)
                if (newColor != null) {
                    hsvColor =  newColor
                    onColorsChanged(newColor, newColor.getColors(internalHarmonyMode.value))
                }
            }

            forEachGesture {
                awaitPointerEventScope {
                    val down = awaitFirstDown()
                    updateColorWheel(down.position)
                    drag(down.id) { change ->
                        change.consumePositionChange()
                        updateColorWheel(change.position)
                    }
                }
            }
        }

        Box(Modifier.fillMaxSize()) {
            Image(modifier = inputModifier, contentDescription = null, bitmap = colorWheel.image)
            val size = IntSize(diameter, diameter)
            val position = positionForColor(hsvColor, size)

            Magnifier(position = position, color = hsvColor)

            hsvColor.getColors(harmonyMode).forEach { hsvColor ->
                Magnifier(position = positionForColor(hsvColor, size), color = hsvColor)
            }
        }
    }
}

fun HsvColor.getColors(colorHarmonyMode: ColorHarmonyMode): List<HsvColor> {
    return when (colorHarmonyMode) {
        ColorHarmonyMode.Complementary -> getComplementaryColor()
        ColorHarmonyMode.Analogous -> getAnalagousColors()
        ColorHarmonyMode.SplitComplementary -> getSplitComplementaryColors()
        ColorHarmonyMode.Triadic -> getTriadicColors()
        ColorHarmonyMode.Tetradic -> getTetradicColors()
        ColorHarmonyMode.Monochromatic -> getMonochromaticColors()
    }
}

/**
 * Magnifier displayed on top of [position] with the currently selected [color].
 */
@ExperimentalGraphicsApi
@Composable
private fun Magnifier(position: Offset, color: HsvColor) {
    val offset = with(LocalDensity.current) {
        Modifier.offset(
            position.x.toDp() - MagnifierWidth / 2,
            // Align with the center of the selection circle
            position.y.toDp() - (MagnifierHeight - (SelectionCircleDiameter / 2))
        )
    }

    Column(
        offset.size(width = MagnifierWidth, height = MagnifierHeight)
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier
                .fillMaxWidth()
                .height(SelectionCircleDiameter),
            contentAlignment = Alignment.Center
        ) {
            MagnifierSelectionCircle(Modifier.size(SelectionCircleDiameter), color)
        }
    }

}

private val MagnifierWidth = 110.dp
private val MagnifierHeight = 100.dp
private val SelectionCircleDiameter = 30.dp


/**
 * Selection circle drawn over the currently selected pixel of the color wheel.
 */
@ExperimentalGraphicsApi
@Composable
private fun MagnifierSelectionCircle(modifier: Modifier, color: HsvColor) {
    Surface(
        modifier,
        shape = CircleShape,
        elevation = 4.dp,
        color = color.toColor(),
        border = BorderStroke(2.dp, SolidColor(Color.Black.copy(alpha = 0.75f))),
        content = {}
    )
}

/**
 * A color wheel with an [ImageBitmap] that draws a circular color wheel of the specified diameter.
 */
private class ColorWheel(diameter: Int) {

    val image = ImageBitmap(diameter, diameter).also { imageBitmap ->
        var colour: HsvColor?
        val bitmap =  imageBitmap.asAndroidBitmap()
        for (x in 0 until imageBitmap.width) {
            for (y in 0 until imageBitmap.height) {
                colour = colorForPosition(IntOffset(x, y), IntSize(imageBitmap.width, imageBitmap.height))
                bitmap.setPixel(x, y, colour?.toColorInt() ?: android.graphics.Color.TRANSPARENT)
            }
        }
    }
}

private fun colorForPosition(position: IntOffset, size: IntSize): HsvColor? {
    val centerX : Double = size.width / 2.0
    val centerY : Double = size.height / 2.0
    val radius : Double = min(centerX, centerY)
    val xOffset : Double = position.x - centerX
    val yOffset : Double = position.y - centerY
    val centerOffset = hypot(xOffset, yOffset)
    val rawAngle = Math.toDegrees(atan2(yOffset, xOffset))
    val centerAngle = (rawAngle + 360.0) % 360.0
    return if (centerOffset <= radius){
        HsvColor(
            hue = centerAngle.toFloat(),
            saturation = (centerOffset / radius).toFloat(),
            value = 1.0f,
            alpha = 1.0f
        )
    } else {
        null
    }
}

/**
 * @return the matching color for [position] inside [ColorWheel], or `null` if there is no color
 * or the color is partially transparent.
 */
private fun ColorWheel.colorForPosition(position: Offset): HsvColor? {
    val x = position.x.toInt().coerceIn(0, image.width)
    val y = position.y.toInt().coerceIn(0, image.height)
    with(image.toPixelMap()) {
        if (x >= width || y >= height) return null
        val color = this[x, y].takeIf { it.alpha == 1f } ?: return null
        return HsvColor.from(color)
    }
}

private fun positionForColor(color: HsvColor, size: IntSize): Offset {
    val radians = Math.toRadians(color.hue.toDouble())
    val phi = color.saturation
    val x : Float = ((phi * cos(radians)).toFloat() + 1) / 2f
    val y : Float = ((phi * sin(radians)).toFloat() + 1) / 2f
    return Offset( x = (x * size.width),
        y = (y * size.height)
    )
}

