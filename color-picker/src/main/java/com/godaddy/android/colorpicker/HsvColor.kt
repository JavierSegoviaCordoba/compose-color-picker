package com.godaddy.android.colorpicker

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.toArgb
import kotlinx.parcelize.Parcelize

/**
 * A representation of Color in Hue, Saturation and Value form.
 */
@Parcelize
data class HsvColor(

    @FloatRange(from = 0.0, to = 360.0)
    val hue: Float,

    @FloatRange(from = 0.0, to = 1.0)
    val saturation: Float,

    @FloatRange(from = 0.0, to = 1.0)
    val value: Float,

    @FloatRange(from = 0.0, to = 1.0)
    val alpha: Float
) : Parcelable {

    @ExperimentalGraphicsApi
    fun toColor(): Color {
        return Color.hsv(hue, saturation, value, alpha)
    }

    /**
     * Transforms HsvColor to Color Int value.
     */
    @ColorInt
    fun toColorInt(): Int {
        return android.graphics.Color.HSVToColor((alpha * 255).toInt(), floatArrayOf(hue, saturation, value))
    }

    fun getComplementaryColor(): List<HsvColor> {
        return listOf(this.copy(hue = (hue + 180) % 360))
    }

    fun getSplitComplementaryColors(): List<HsvColor> {
        return listOf(this.copy( hue = (hue + 150) % 360), this.copy( hue = (hue + 210 ) % 360))
    }

    fun getTriadicColors() : List<HsvColor> {
        return listOf(
            this.copy(hue = (hue + 120) % 360),
            this.copy( hue = (hue + 240) % 360))
    }

    fun getTetradicColors() : List<HsvColor> {
        return listOf(
            this.copy(hue = (hue + 90) % 360),
            this.copy( hue = (hue + 180) % 360),
            this.copy( hue = (hue + 270) % 360),
        )
    }

    fun getAnalagousColors() : List<HsvColor> {
        return listOf(
            this.copy(hue = (hue + 30) % 360),
            this.copy( hue = (hue + 60) % 360),
            this.copy( hue = (hue + 90) % 360),
        )
    }

    fun getMonochromaticColors() : List<HsvColor> {
        return listOf(
            this.copy(saturation =  (saturation + 0.5f).coerceAtMost(1f)),
        )
    }

    companion object {

        val DEFAULT = HsvColor(360f, 1.0f, 1.0f, 1.0f)

        /**
         * Creates an HsvColor from Color Int
         */
        fun from(@ColorInt color: Int): HsvColor {
            val extractedHsvArray = FloatArray(3)
            android.graphics.Color.colorToHSV(color, extractedHsvArray)
            return HsvColor(
                hue = extractedHsvArray[0],
                saturation = extractedHsvArray[1],
                value = extractedHsvArray[2],
                alpha = android.graphics.Color.alpha(color) / 255f
            )
        }

        /**
         * Creates an HsvColor from Color
         */
        fun from(color: Color): HsvColor {
            val extractedHsvArray = FloatArray(3)
            val argb = color.toArgb()
            android.graphics.Color.colorToHSV(argb, extractedHsvArray)
            return HsvColor(
                hue = extractedHsvArray[0],
                saturation = extractedHsvArray[1],
                value = extractedHsvArray[2],
                alpha = color.alpha
            )
        }
    }
}
