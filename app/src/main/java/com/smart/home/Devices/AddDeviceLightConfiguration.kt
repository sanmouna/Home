package com.smart.home.Devices


import WebSocketService
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.smart.home.DeviceMetaData
import com.smart.home.Utils.DeviceType
import com.smart.home.Utils.allDevicesList

import java.math.BigDecimal
import java.math.RoundingMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightConfigurationUI(
    light: DeviceMetaData = DeviceMetaData(),
    onLightChange: (DeviceMetaData) -> Unit
) {
    var isEnabled by remember { mutableStateOf(light.isEnabled) }
    var timeout by remember { mutableStateOf(light.activeTimeout) }
    var isPIRmode by remember { mutableStateOf(light.isPIRmode) }
    var pinType by remember { mutableStateOf(light.pin_type) }
    var numPixels by remember { mutableStateOf(light.num_pixels) }
    var animationType by remember { mutableStateOf(light.animation_type) }
    var animationDelay by remember { mutableStateOf(light.animation_delay) }
    val (defaultRed, defaultGreen, defaultBlue) = light.color.split(",").map { it.toInt() }
    var red by remember { mutableStateOf(defaultRed) }
    var green by remember { mutableStateOf(defaultGreen) }
    var blue by remember { mutableStateOf(defaultBlue) }
    var expandedDeviceIds by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var expandedAnimation by remember { mutableStateOf(false) }
    var selectedLightType by remember { mutableStateOf(light.pin_type) }

    val neoAnimation = listOf("fading", "rainbow", "color_wipe", "theater_chase", "twinkle","solid")
    val smdAnimation = listOf("fading", "growing", "multi_color", "rainbow", "pulse", "sparkle", "color_wipe")


    Column(
        modifier = Modifier  ,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

//        // Is Enabled Switch
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text(text = "Enabled:")
//            Switch(
//                checked = isEnabled==1,
//                onCheckedChange = {
//                    isEnabled = if(it) 1 else 0
//                    onLightChange(light.copy(isEnabled = if(it) 1 else 0))
//                },
//                modifier = Modifier.padding(start = 8.dp)
//            )
//        }

        // Timeout Input
        Text(text = "Type: ${light.type}")
        Text(text = "Timeout: $timeout seconds")
        Slider(
            value = timeout.toFloat(),
            onValueChange = { newValue ->
                timeout = newValue.toInt()
                onLightChange(light.copy(activeTimeout = timeout))
            },
            valueRange = 30f..300f, // Range from 30 to 300 seconds
            steps = 9, // Number of intervals: (300 - 30) / 30 - 1
            modifier = Modifier.fillMaxWidth()
        )

        // PIR Mode Switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "PIR Mode:")
            Switch(
                checked = isPIRmode==1,
                onCheckedChange = {
                    isPIRmode = if(it) 1 else 0
                    onLightChange(light.copy(isPIRmode = if(it) 1 else 0))
                },
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Light Type Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedDeviceIds,
            onExpandedChange = { expandedDeviceIds = !expandedDeviceIds }
        ) {
            OutlinedTextField(
                value = selectedLightType,
                onValueChange = {},
                label = { Text("Light Type: ") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown arrow"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedDeviceIds,
                onDismissRequest = { expandedDeviceIds = false }
            ) {

                allDevicesList.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(text = option)
                        },
                        onClick = {
                            selectedLightType = option
                            expandedDeviceIds = false
                            onLightChange(light.copy(pin_type = option))
                        }
                    )
                }
            }
        }
        if (selectedLightType != "PWM" && selectedLightType != "H_L") {
            // Animation Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedAnimation,
                onExpandedChange = { expandedAnimation = !expandedAnimation }
            ) {
                OutlinedTextField(
                    value = animationType,
                    onValueChange = {},
                    label = { Text("Animation Type") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown arrow"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedAnimation,
                    onDismissRequest = { expandedAnimation = false }
                ) {
                    val animationOptions = when (selectedLightType) {
                        "NeoPixel" -> neoAnimation
                        "SMD5050" -> smdAnimation
                        else -> emptyList()
                    }

                    animationOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(text = option)
                            },
                            onClick = {
                                animationType = option
                                expandedAnimation = false
                                onLightChange(light.copy(animation_type = option))
                            }
                        )
                    }
                }
            }
        }
        if (selectedLightType == DeviceType.PWM )
            Column {
                Text(text = "Brightness: ${numPixels}")
                Slider(
                    value = numPixels.toFloat(),
                    onValueChange = { value ->
                        numPixels = value.toInt()
                        onLightChange(light.copy(num_pixels = value.toInt()))
                    },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        if (selectedLightType == DeviceType.NEOPIXEL )
            OutlinedTextField(
                value = numPixels.toString(),
                onValueChange = {
                    numPixels = it.toIntOrNull() ?: 8
                    onLightChange(light.copy(num_pixels = numPixels))
                },
                singleLine = true,
                label = { Text("Number of Pixels") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        if (selectedLightType == DeviceType.NEOPIXEL ||  selectedLightType == DeviceType.SMD5050 )

            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(red, green, blue))
                    .border(2.dp, Color.White, RoundedCornerShape(10.dp))
            )

        // Color Sliders for Red, Green, and Blue
        if (selectedLightType == DeviceType.NEOPIXEL ||  selectedLightType == DeviceType.SMD5050 )
            Column {
                Text(text = "Red: ${red}")
                Slider(
                    value = red.toFloat(),
                    onValueChange = { value ->
                        red = value.toInt()
                        onLightChange(light.copy(color = "$red,$green,$blue"))
                    },
                    valueRange = 0f..255f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        if (selectedLightType == DeviceType.NEOPIXEL ||  selectedLightType == DeviceType.SMD5050 )
            Column {
            Text(text = "Green: ${green}")
            Slider(
                value = green.toFloat(),
                onValueChange = { value ->
                    green = value.toInt()
                    onLightChange(light.copy(color = "$red,$green,$blue"))
                },
                valueRange = 0f..255f,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (selectedLightType == DeviceType.NEOPIXEL ||  selectedLightType == DeviceType.SMD5050 )
            Column {
            Text(text = "Blue: ${blue}")
            Slider(
                value = blue.toFloat(),
                onValueChange = { value ->
                    blue = value.toInt()
                    onLightChange(light.copy(color = "$red,$green,$blue"))
                },
                valueRange = 0f..255f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (selectedLightType != DeviceType.HighOrLow )
            Text(text = "Animation Delay: ${animationDelay} seconds")
        if (selectedLightType != DeviceType.HighOrLow )
            Slider(
                value = (animationDelay.toString().substringBefore(".") + "." + animationDelay.toString().substringAfter(".").take(2)).toFloat(),
                onValueChange = { newValue ->
                    animationDelay = newValue
                    onLightChange(light.copy(animation_delay = (newValue.toString().substringBefore(".") + "." + newValue.toString().substringAfter(".").take(2)).toFloat()))
                },
                valueRange = 0.01f..0.3f,
                steps = 29, // Adjust steps to match the desired range
                modifier = Modifier.fillMaxWidth()
            )
    }
}









