package com.smart.home.Devices


import WebSocketService
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.home.Category
import com.smart.home.DevicesList
import com.smart.home.SharedViewModel
import com.smart.home.Utils.DeviceType
import com.smart.home.Utils.destinations


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddDeviceConfiguration(  devicesList:DevicesList,
                           onDeviceChange: (DevicesList) -> Unit,
                           isItNew:Boolean=false
)
{
    var enabled by remember { mutableIntStateOf(devicesList.enabled) }
    var range by remember { mutableIntStateOf(devicesList.range) }
    var selectedDestination by remember { mutableStateOf(devicesList.destination) }
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(10.dp))
    {
        Column(
            modifier =  Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(10.dp)).background(Color.DarkGray)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Add Device Configuration")

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Enabled: ")
                Switch(
                    checked = enabled==1,
                    onCheckedChange = {
                        enabled = if(it) 1 else 0
                        onDeviceChange(devicesList.copy(enabled = if(it) 1 else 0))
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth().clickable {
                        expanded=true
                    }
                    .border(
                        width = 1.dp, // Border width
                        color = Color.White, // Border color
                        shape = RoundedCornerShape(6.dp) // Corner radius
                    )
                    .height(55.dp)
                    .background(color = Color.Transparent) ,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                    text = (if(selectedDestination.isEmpty())  "Select Destination"  else selectedDestination),
                    color = Color.White, fontSize = 16.sp)

                Icon(tint = Color.White, modifier = Modifier.padding(horizontal = 10.dp),
                    imageVector =  Icons.Filled.ExpandCircleDown,
                    contentDescription = "")
                if (expanded) {
                    DropdownMenu(
                        expanded = true,
                        modifier = Modifier
                            .fillMaxWidth(0.665f)
                            .background(Color.White).wrapContentHeight() ,
                        onDismissRequest = { expanded = false }) {

                        destinations.forEach { option ->
                            DropdownMenuItem(onClick = {

                            } // Reset search query
                                ,text={
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.clickable {
                                            selectedDestination=option
                                            expanded=false
                                        }
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp))
                                    {
                                        Text(modifier = Modifier.fillMaxWidth(), text = option, color = Color.Black, fontSize = 16.sp)

                                    }
                                })
                        }
                    }
                }
            }

//            if(devicesList.type=="FAN"&&isItNew)
//            {
//                Spacer(modifier = Modifier.height(10.dp))
//                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth())
//                {
//                    MovableCircularGauge(
//                        fanSpeed = devicesList.range.toFloat(),
//                        onFanSpeedChange = {
//                            devicesList.range = it.toInt()
//                            onDeviceChange(devicesList.copy(range=it.toInt()))
//                        }
//                    )
//                }
//
//            }
//            else if(devicesList.type.lowercase()== DeviceType.LIGHT.lowercase())
//            {
//                 LightConfigurationUI {
//                     onDeviceChange(devicesList.copy())
//                    }
//            }
//            else if( devicesList.type.isEmpty())
//            {
//                Spacer(modifier = Modifier.height(10.dp))
//                Text(text = "Range: ${range}") // Display current value
//                Slider(
//                    value = range.toFloat(),
//                    onValueChange = { range = it.toInt() },
//                    valueRange = 1f..  100f, // Adjust the range as needed
//                    steps =   99 // 100 steps
//                )
//            }



            Spacer(modifier = Modifier.height(10.dp))

        }



    }

}

