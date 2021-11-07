package com.godaddy.android.colorpicker.disc

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.disc.HueRing
import com.godaddy.android.colorpicker.disc.SaturationValueCircleSaturationValueArea

@ExperimentalGraphicsApi
@Composable
fun DiscColorPicker(
    modifier: Modifier = Modifier,
    color: Color = Color.Red,
    showAlphaBar: Boolean = true,
    onColorChanged: (HsvColor) -> Unit
) {
    val colorPickerValueState = rememberSaveable {
        mutableStateOf(HsvColor.from(color))
    }
    BoxWithConstraints(
        modifier = modifier
            .defaultMinSize(80.dp, 80.dp)
            .aspectRatio(1f)
    ) {
        HueRing(currentColor = colorPickerValueState.value, onHueChanged = {
            colorPickerValueState.value = colorPickerValueState.value.copy(hue = it)
            onColorChanged(colorPickerValueState.value)
        })
        SaturationValueCircleSaturationValueArea(
            currentColor = colorPickerValueState.value,
            onSaturationValueChanged = { s, v ->
                colorPickerValueState.value =
                    colorPickerValueState.value.copy(saturation = s, value = v)
                onColorChanged(colorPickerValueState.value)
            }
        )
    }
}

