package com.smart.home.Devices


import WebSocketService
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.GsonBuilder
import com.smart.home.Category
import com.smart.home.DeviceMetaData
import com.smart.home.DevicesList
import com.smart.home.ScheduleMetaData
import com.smart.home.SharedViewModel
import com.smart.home.SnackbarManager
import com.smart.home.Utils.DeviceType
import com.smart.home.Utils.iconMap
import java.time.LocalTime
import java.time.format.DateTimeFormatter



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeviceConfiguration(sharedViewModel: SharedViewModel,
                        webSocketClient: WebSocketService,
                        devicesList:DevicesList,
                        onDeviceChange: (DevicesList) -> Unit,
                        isItNew:Boolean=false, selectedCategory: Category =Category()
)
{

    val globalViewModel = sharedViewModel.globalViewModelData.collectAsState()
    val category = if(isItNew) Category() else globalViewModel.value.categories.find { it.id == selectedCategory.id }
    val tableName=devicesList.sTableName
    var enabled by remember { mutableIntStateOf(devicesList.enabled) }
    var range by remember { mutableIntStateOf(devicesList.range) }
    var destination by remember { mutableStateOf(devicesList.destination) }
    var schedule by remember { mutableStateOf(devicesList.schedule) }
    var addSchedule by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    Card(
        modifier = Modifier.fillMaxHeight(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(10.dp))
        {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "Pair Device Configuration"
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Enabled: ", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = enabled == 1,
                        onCheckedChange = {
                            enabled = if (it) 1 else 0
                            onDeviceChange(devicesList.copy(enabled = if (it) 1 else 0))
                        }
                    )
                }

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Destination: " +destination
                ) // Display current value

                if (devicesList.type == "FAN" && isItNew) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth())
                    {
                        MovableCircularGauge(
                            fanSpeed = devicesList.range.toFloat(),
                            onFanSpeedChange = {
                                devicesList.range = it.toInt()
                                onDeviceChange(devicesList.copy(range = it.toInt()))
                            }
                        )
                    }

                } else if (devicesList.type.isEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Range: ${range}") // Display current value
                    Slider(
                        value = range.toFloat(),
                        onValueChange = { range = it.toInt() },
                        valueRange = 1f..100f, // Adjust the range as needed
                        steps = 99 // 100 steps
                    )
                }

                Text(text="${devicesList.outputMode} HERE")

                if (!isItNew)
                    category?.devices!!.find { it.id == devicesList.id }?.schedule?.forEachIndexed { index, it ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(10.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Text(
                                        text = "Schedule Id: ${it.id}",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    IconButton(onClick = {
                                        selectedIndex = index
                                        addSchedule = "edit"
                                    })
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Icon",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = {
//                                val jsonObject = JsonObject(mapOf(
//                                    "id" to JsonPrimitive(it.id),
//                                    "tableName" to JsonPrimitive(tableName),
//                                    "deviceId" to JsonPrimitive(it.deviceId),
//                                    "roomId" to JsonPrimitive(it.roomId),
//                                ))
//                                val objectString = Json.encodeToString(JsonObject.serializer(), jsonObject)
                                        webSocketClient.sendMessage(
                                            "deleteschedule_${
                                                GsonBuilder().create().toJson(it)
                                            }"
                                        )
                                    })
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Edit Icon",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Text(
                                    text = "Name: ${it.name}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Device Id: ${it.deviceId}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Enabled: ${it.enabled}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Running: ${it._running}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Time Icon",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "StartTime: ${it.startTime}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Time Icon",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "EndTime: ${it.endTime}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Text(
                                    text = "Priority: ${it.priority}",
                                    style = MaterialTheme.typography.titleMedium
                                )


                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        // Icon and title row
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            iconMap[devicesList.icon]?.let {
                                                Icon(
                                                    imageVector = it,
                                                    contentDescription = ""
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))

                                            Column {
                                                Text(
                                                    text = it.deviceMetaData.type.replace("_", " ")
                                                        .lowercase()
                                                        .replaceFirstChar { it.uppercase() },

                                                    )
                                                Text(
                                                    text = if (it.deviceMetaData.initial_state==1) "Active" else "Inactive",

                                                    )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))


                                        // Display additional attributes with animations
                                        Text(text = "Range: ${it.deviceMetaData.range}")
                                        Text(text = "Mode: ${it.deviceMetaData.mode}")
                                        Text(text = "Destination: ${devicesList.destination}")
                                        Text(text = "Timeout: ${it.deviceMetaData.activeTimeout} sec")
                                        Spacer(modifier = Modifier.height(8.dp))

                                    }
                                }

                            }
                        }
                    }


                if (!isItNew && addSchedule.isEmpty()) {
                    Button(
                        onClick = {
                            addSchedule = "add"
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(text = "Add Schedule")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

            }
            if (addSchedule == "add" || addSchedule == "edit") {
                FullScreenScheduleEditorPopup(
                    selectedIndex = selectedIndex,
                    devicesList = devicesList,
                    selectedCategory = selectedCategory,
                    tableName = tableName,
                    addSchedule = addSchedule,
                    webSocketClient = webSocketClient,
                    schedule = if (addSchedule == "edit") schedule[selectedIndex] else ScheduleMetaData(
                        roomId = category?.id!!,
                        deviceMetaData = DeviceMetaData(destination = devicesList.destination, type = devicesList.type,id=devicesList.outputMode),
                        deviceId = devicesList.id
                    ),
                    onDone = {
                        addSchedule = ""
                    }
                )

            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCard(
    tableName: String = "",
    webSocketClient: WebSocketService,
    edit: Boolean = false,
    schedule: ScheduleMetaData,
    onDone: () -> Unit,
    selectedCategory: Category,
    devicesList: DevicesList
) {
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

            Text(modifier = Modifier.weight(1f), text = "${ if(edit) "Edit" else "Add"} Schedule", fontSize = 20.sp)
            Box(modifier = Modifier.clickable {
                if (name.isEmpty()) {
                    SnackbarManager.showSnackbar("Please enter schedule name")
                    return@clickable
                }
                val action = if (edit) "editschedule_" else "insertschedule_"
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
                if(scheduleMetaData.deviceMetaData.animation_type.isEmpty()&&deviceMetaData.type!="H_L"&&deviceMetaData.type!= DeviceType.PWM)
                    return@clickable SnackbarManager.showSnackbar("Fill All")
                webSocketClient.sendMessage(action + GsonBuilder().create().toJson(scheduleMetaData))
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
                modifier = Modifier.weight(1f).fillMaxHeight(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Basic Info", style = MaterialTheme.typography.titleMedium)


                    OutlinedTextField(
                        singleLine = true,
                        value = name,
                        onValueChange = { newValue -> name = newValue },
                        label = { Text("Schedule Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = "Priority: $priority",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                        contentDescription = "Dropdown Icon"
                                    )
                                }
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {

                            priorities.forEach {
                                DropdownMenuItem(onClick = {

                                }, text = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.clickable {
                                            priority = it
                                            expanded = false
                                        }
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp))
                                    {
                                        Text(text = "Priority $it")
                                    }
                                })
                            }


                        }
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

            // Sensors Card
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {


                    LightConfigurationUI(light =  deviceMetaData.copy(type = devicesList.type, pin_type = devicesList.type)) {
                        deviceMetaData=it
                    }
//                    Text(text = "Sensors", style = MaterialTheme.typography.titleMedium)
//
//                    Text("Range: ${devicesList.range}")
//                    Text("Mode: ${devicesList.mode}")
//                    Text("Destination: ${devicesList.destination}")
//                    Text("Timeout: ${schedule.deviceMetaData.activeTimeout} sec")



                    //                    sensors.forEachIndexed { index, sensor ->
//                        SensorCard(
//                            sensor = sensor,
//                            index = index,
//                            onSensorChange = { updatedSensor ->
//                                sensors =
//                                    sensors.toMutableList().apply { set(index, updatedSensor) }
//                            },
//                            onRemoveSensor = {
//                                sensors = sensors.toMutableList().apply { removeAt(index) }
//                            }
//                        )
//                    }
//
//                    Button(
//                        onClick = {
//                            if (sensors.all { sensor ->
//                                    sensor.name.isNotBlank() && sensor.priority in 1..5 && sensor.triggerAt >= 0 &&
//                                            sensor.type.isNotBlank() && sensor.sensorName.isNotEmpty()
//                                }
//                            ) {
//                                sensors = sensors + SensorMetaData()
//                            } else {
//                                SnackbarManager.showSnackbar("Fill all fields")
//                            }
//                        },
//                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
//                    ) {
//                        Text("Add More Sensors")
//                    }
                }
            }

            // Device Metadata Card
//            Card(
//                modifier = Modifier.weight(1f).fillMaxHeight(),
//                elevation = CardDefaults.cardElevation(4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(10.dp).verticalScroll(rememberScrollState()),
//                    verticalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    Text(text = "Device Info ${devicesList}", style = MaterialTheme.typography.titleMedium)
//
//                    if(devicesList.type== DeviceType.LIGHT)
//                    {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            iconMap[devicesList.icon]?.let {
//                                Icon(
//                                    imageVector = it,
//                                    contentDescription = "Device Icon"
//                                )
//                            }
//                            Spacer(modifier = Modifier.width(16.dp))
//                            Column {
//                                Text(
//                                    text = devicesList.type.replace("_", " ").lowercase()
//                                        .replaceFirstChar { it.uppercase() })
//                                Text(text = if (schedule.deviceMetaData._isActive) "Active" else "Inactive")
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//BulbControlUI()
//                    }
//               }
//            }
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            onTimeSelected(LocalTime.of(hour, minute))
        },
        time.hour,
        time.minute,
        true
    )

    DisposableEffect(Unit) {
        timePickerDialog.show()
        onDispose {
            timePickerDialog.dismiss()
        }
    }
}
