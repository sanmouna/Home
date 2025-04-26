package com.smart.home.Devices

import WebSocketService
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.gson.GsonBuilder
import com.smart.home.DeviceMetaData
import com.smart.home.DevicesList
import com.smart.home.ScheduleMetaData
import com.smart.home.SnackbarManager
import com.smart.home.Utils.DeviceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun FullScreenScheduleUpdatePopup(
    tableName: String = "",
    webSocketClient: WebSocketService,
    schedule: ScheduleMetaData=ScheduleMetaData(),
    devicesList: DevicesList=DevicesList(),
    onDone: () -> Unit,

) {
    val controller = rememberColorPickerController()
    var enabled by remember { mutableStateOf(schedule.enabled.toString()) }
    var startTime by remember { mutableStateOf(LocalTime.parse(schedule.startTime)) }
    var endTime by remember { mutableStateOf(LocalTime.parse(schedule.endTime)) }
    var priority by remember { mutableIntStateOf(schedule.priority) }
    var name by remember { mutableStateOf(schedule.name) }
    var sensors by remember { mutableStateOf(schedule.sensors) }
    var deviceMetaData by remember { mutableStateOf(schedule.deviceMetaData.copy(type = devicesList.type,id=devicesList.outputMode, destination = devicesList.destination)) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf(1, 2, 3, 4, 5)
    var redValue by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp)
            .clickable(enabled = false) {} // Disables clicks on content to avoid closing
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(modifier = Modifier.clickable {
                onDone()
            })
            {
                Icon(Icons.Filled.ArrowBack,tint = MaterialTheme.colorScheme.primary, contentDescription = "Add Device")
            }

            Text(modifier = Modifier.weight(1f), text = "Update Schedule", fontSize = 20.sp)
            Box(modifier = Modifier.clickable {
                if (name.isEmpty()) {
                    SnackbarManager.showSnackbar("Please enter schedule name")
                    return@clickable
                }

                val scheduleMetaData = ScheduleMetaData(
                    roomId = schedule.roomId,
                    deviceId = schedule.deviceId,
                    id = schedule.id,
                    sensors = sensors,
                    name = name,
                    tableName = tableName,
                    enabled = enabled.toInt(),
                    startTime = startTime.toString(),
                    endTime = endTime.toString(),
                    priority = priority,
                    deviceMetaData = deviceMetaData
                )
                if(scheduleMetaData.deviceMetaData.animation_type.isEmpty()&&deviceMetaData.type!="H_L")
                    return@clickable SnackbarManager.showSnackbar("Fill All")
                webSocketClient.sendMessage(GsonBuilder().create().toJson(scheduleMetaData))
                onDone()
            })
            {
                Icon(Icons.Filled.Done, tint = MaterialTheme.colorScheme.primary, contentDescription = "Add Device")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Basic Info Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Basic Info", style = MaterialTheme.typography.titleMedium)


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Schdule name:")
                        Text(text = "${schedule.name}")
                    }



                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Enabled: ")
                        Switch(
                            checked = enabled == "1",
                            onCheckedChange = { newEnabled ->
                                enabled = if (newEnabled) "1" else "0"
                            }
                        )
                    }
                    Text("Destination: ${deviceMetaData.destination}")
                    Text(text = "Type: ${deviceMetaData.type}")
                    Text(
                        text = "Start Time: ${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        modifier = Modifier.clickable { showStartTimePicker = true }
                    )
                    Text(
                        text = "End Time: ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        modifier = Modifier.clickable { showEndTimePicker = true }
                    )
                }
            }
            if(deviceMetaData.pin_type==DeviceType.NEOPIXEL||deviceMetaData.pin_type==DeviceType.SMD5050)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                            .padding(10.dp),
                        controller = controller,

                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                         try {
                             val hexCode = colorEnvelope.hexCode
                             val red = Integer.valueOf(hexCode.substring(2, 4), 16)   // Extract and convert red
                             val green = Integer.valueOf(hexCode.substring(4, 6), 16) // Extract and convert green
                             val blue = Integer.valueOf(hexCode.substring(6, 8), 16)  // Extract and convert blue

                             println("Red: $red, Green: $green, Blue: $blue")
                             deviceMetaData = deviceMetaData.copy(
                                 initial_state = 1,
                                 color = "${red},${green},${blue}"
                             )

                         }
                         catch (e:Exception)
                         {

                         }
                          }
                    )

                    Box(modifier = Modifier.clickable {
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(200);
                            webSocketClient.sendMessage(
                                GsonBuilder().create().toJson(
                                    schedule.copy(  deviceMetaData = deviceMetaData.copy(key = "color")  )
                                )
                            )

                        }

                    })
                    {
                        Icon(Icons.Filled.Done, tint = MaterialTheme.colorScheme.primary, contentDescription = "Add Device")
                    }

                }
            }
            // Sensors Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LightConfigurationUIForUpdateColor(
                        light = deviceMetaData.copy(
                            type = devicesList.type,
                            pin_type = devicesList.type
                        )
                    ) {
                        deviceMetaData = it
                        webSocketClient.sendMessage(
                            GsonBuilder().create().toJson(
                                schedule.copy(deviceMetaData = deviceMetaData)))
                    }
                }
            }
        }

        // Time Pickers
        if (showStartTimePicker) {
            TimePickerDialog(
                time = startTime,
                onTimeSelected = {
                    startTime = it
                    showStartTimePicker = false
                },
                onDismiss = { showStartTimePicker = false }
            )
        }
        if (showEndTimePicker) {
            TimePickerDialog(
                time = endTime,
                onTimeSelected = {
                    endTime = it
                    showEndTimePicker = false
                },
                onDismiss = { showEndTimePicker = false }
            )
        }
    }

}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightConfigurationUIForUpdateColor(
    type:Boolean=false,
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
     var red by remember { mutableStateOf(0) }
    var green by remember { mutableStateOf(0) }
    var blue by remember { mutableStateOf(0) }
    var expandedAnimation by remember { mutableStateOf(false) }
    var selectedLightType by remember { mutableStateOf(light.pin_type) }

    val neoAnimation = listOf("comet","confetti","fire","breathing","fading","wave_pulse","meteor_rain","running_lights", "rainbow", "color_wipe", "theater_chase", "twinkle","solid")
    val smdAnimation = listOf("fading", "growing", "multi_color", "rainbow", "pulse", "sparkle", "color_wipe")


    Column(
        modifier = Modifier  ,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

  if(type==false)
  {

      Text(text = "Timeout: $timeout seconds")
      Slider(
          value = timeout.toFloat(),
          onValueChange = { newValue ->
              timeout = newValue.toInt()
          },
          onValueChangeFinished = {
              onLightChange(light.copy(activeTimeout = timeout))
            },
          valueRange = 30f..300f, // Range from 30 to 300 seconds
          steps = 10, // Number of intervals: (300 - 30) / 30 - 1
          modifier = Modifier.fillMaxWidth()
      )

      Text(text = "Brightness: $numPixels")
      Slider(
          value = numPixels.toFloat(),
          onValueChange = { newValue ->
              numPixels = newValue.toInt()
          },
          onValueChangeFinished = {
              onLightChange(light.copy(num_pixels =  numPixels))

          },
          valueRange = 5f..100f, // Range from 30 to 300 seconds
          steps = 95, // Number of intervals: (300 - 30) / 30 - 1
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
                              onLightChange(light.copy(animation_type = option, key = "animation_type"))
                          }
                      )
                  }
              }
          }


              Text(text = "Animation Delay: ${animationDelay} seconds")

              Slider(
                  value = (animationDelay.toString().substringBefore(".") + "." + animationDelay.toString().substringAfter(".").take(2)).toFloat(),
                  onValueChange = { newValue ->
                      animationDelay = newValue
                   },
                  onValueChangeFinished = {
                        onLightChange(light.copy(animation_delay = animationDelay))
                    },
                  valueRange = 0.01f..0.3f,
                  steps = 29, // Adjust steps to match the desired range
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
  }

  else{


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
                  onValueChangeFinished = {
                      onLightChange(light.copy(color = "$red,$green,$blue"))
                  },
                  onValueChange = { value ->
                      red = value.toInt()
                   },
                  valueRange = 0f..255f,
                  modifier = Modifier.fillMaxWidth()
              )
          }

      if (selectedLightType == DeviceType.NEOPIXEL ||  selectedLightType == DeviceType.SMD5050 )
          Column {
              Text(text = "Green: ${green}")
              Slider(
                  onValueChangeFinished = {
                      onLightChange(light.copy(color = "$red,$green,$blue"))
                  },
                  value = green.toFloat(),
                  onValueChange = { value ->
                      green = value.toInt()
                   },
                  valueRange = 0f..255f,
                  modifier = Modifier.fillMaxWidth()
              )
          }
      if (selectedLightType == DeviceType.NEOPIXEL ||  selectedLightType == DeviceType.SMD5050 )
          Column {
              Text(text = "Blue: ${blue}")

                  Slider(
                      onValueChangeFinished = {
                          SnackbarManager.showSnackbar("Clicked")
                      },
                      value = blue.toFloat(),
                      onValueChange = { value ->
                          blue = value.toInt()
                      },
                      valueRange = 0f..255f,
                      modifier = Modifier.fillMaxWidth()
                  )
          }



  }
  }


}



