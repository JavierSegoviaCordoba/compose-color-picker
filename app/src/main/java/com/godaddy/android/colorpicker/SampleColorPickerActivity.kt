package com.godaddy.android.colorpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.disc.DiscColorPicker
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import com.godaddy.android.colorpicker.harmony.getColors
import com.godaddy.android.colorpicker.theme.ComposeColorPickerTheme

class SampleColorPickerActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    @ExperimentalGraphicsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeColorPickerTheme {
                DiscColorExample()
            }
        }
    }
}


@ExperimentalGraphicsApi
@Composable
fun DiscColorExample() {
    Surface(color = MaterialTheme.colors.background) {
        Column {
            TopAppBar(title = {
                Text(stringResource(R.string.compose_color_picker_sample))
            })
            var currentColor by remember {
                mutableStateOf(Color.Companion.Red)
            }

            ColorPreviewInfo(
                currentColor = currentColor,
                harmonyColors = emptyList()
            )
            DiscColorPicker(
                modifier = Modifier.fillMaxSize(),
                color = currentColor,
                onColorChanged = {
                currentColor = it.toColor()
            })
        }
    }
}

@ExperimentalGraphicsApi
@ExperimentalMaterialApi
@Composable
fun HarmonyColorExample() {
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


            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded.value, onExpandedChange = {}) {
                Text(harmonyMode.value.name,
                    modifier = Modifier
                        .clickable {
                            expanded.value = !expanded.value
                        }
                        .padding(16.dp))
                ExposedDropdownMenu(expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }) {
                    ColorHarmonyMode.values().forEach {
                        DropdownMenuItem(onClick = {
                            harmonyMode.value = it
                            expanded.value = false
                            harmonyColors.value =
                                HsvColor.from(currentColor.value).getColors(harmonyMode.value)
                        }) {
                            Text(it.name)
                        }
                    }

                }
            }

            ColorPreviewInfo(
                currentColor = currentColor.value,
                harmonyColors = harmonyColors.value
            )
            HarmonyColorPicker(harmonyMode = harmonyMode.value,
                onColorChanged = { color ->
                    currentColor.value = color.toColor()
                    harmonyColors.value = color.getColors(harmonyMode.value)
                })
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