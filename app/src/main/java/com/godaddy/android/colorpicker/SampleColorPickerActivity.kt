package com.godaddy.android.colorpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import com.godaddy.android.colorpicker.harmony.getColors
import com.godaddy.android.colorpicker.theme.ComposeColorPickerTheme

class SampleColorPickerActivity : ComponentActivity() {

    @ExperimentalGraphicsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeColorPickerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        TopAppBar(title = {
                            Text(stringResource(R.string.compose_color_picker_sample))
                        })
                        val currentColor = remember {
                            mutableStateOf(Color.Companion.Red)
                        }
                        val harmonyMode = remember {
                            mutableStateOf(ColorHarmonyMode.Triadic)
                        }
                        val harmonyColors = remember(harmonyMode) {
                            mutableStateOf(listOf<HsvColor>())
                        }
                        val expanded = remember {
                            mutableStateOf(false)
                        }

                        Text(harmonyMode.value.name,
                            modifier = Modifier
                                .clickable {
                                    expanded.value = !expanded.value
                                }
                                .padding(16.dp))

                        DropdownMenu(expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }) {
                            ColorHarmonyMode.values().forEach {
                                DropdownMenuItem(onClick = {
                                    harmonyMode.value = it
                                    expanded.value = false
                                }) {
                                    Text(it.name)
                                }
                            }

                        }
                        ColorPreviewInfo(currentColor = currentColor.value,
                            harmonyColors = harmonyColors.value)
                        HarmonyColorPicker(harmonyMode = harmonyMode.value,
                            onColorsChanged = { color, _ ->
                            currentColor.value = color.toColor()
                            harmonyColors.value = color.getColors(harmonyMode.value)
                        })
                    }
                }
            }
        }
    }
}

fun Color.toHexString(): String {
    return String.format("#%08X", -0x1 and this.hashCode())
}

@ExperimentalGraphicsApi
@Composable
fun ColorPreviewInfo(currentColor: Color, harmonyColors: List<HsvColor>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start) {
            Spacer(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        currentColor,
                        shape = CircleShape
                    )
                    .size(48.dp)
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = currentColor.toHexString()
            )

        }
        Row(modifier = Modifier.fillMaxWidth()) {
            harmonyColors.forEach {
                Spacer(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(
                            it.toColor(),
                            shape = CircleShape
                        )
                        .size(48.dp)


                )
            }
        }
    }
}


@ExperimentalGraphicsApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeColorPickerTheme {
        ClassicColorPicker(
            modifier = Modifier.height(300.dp),
            color = Color.Green,
            onColorChanged = {

            })

    }
}

@ExperimentalGraphicsApi
@Preview(showBackground = true)
@Composable
fun NoAlphaBarPreview() {
    ComposeColorPickerTheme {
        ClassicColorPicker(
            modifier = Modifier.height(300.dp),
            color = Color.Magenta,
            showAlphaBar = false,
            onColorChanged = {

            })
    }
}