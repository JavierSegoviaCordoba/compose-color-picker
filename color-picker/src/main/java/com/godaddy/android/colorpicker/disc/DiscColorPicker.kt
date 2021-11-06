package com.godaddy.android.colorpicker.disc

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.disc.HueRing
import com.godaddy.android.colorpicker.disc.SaturationValueCircleSaturationValueArea

@ExperimentalGraphicsApi
@Composable
fun DiscColorPicker(
    modifier: Modifier = Modifier,
    color: MutableState<HsvColor> = rememberSaveable {
        mutableStateOf(HsvColor.DEFAULT)
    },
    showAlphaBar: Boolean = true,
    onColorChanged: (HsvColor) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.aspectRatio(1f)
    ) {
        SaturationValueCircleSaturationValueArea(
            currentColor = color.value,
            onSaturationValueChanged = { s, v ->
                color.value =
                    color.value.copy(saturation = s, value = v)
                onColorChanged(color.value)
            }
        )
        HueRing(currentColor = color.value, onHueChanged = {
            color.value = color.value.copy(hue = it)
            onColorChanged(color.value)
        })

    }
}

