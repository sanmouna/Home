package com.smart.home.WebSocket.Category

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smart.home.Category
import com.smart.home.SharedViewModel
import com.smart.home.Utils.iconMap
import WebSocketService
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.shadow
import com.notifii.lockers.Utils.getRandomString
import com.smart.home.DevicesList
import com.smart.home.Devices.AddDevice
import com.smart.home.Devices.DeviceConfiguration
import com.smart.home.SnackbarManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CategoryViewer(webSocketClient : WebSocketService, navController: NavController,
                   sharedViewModel: SharedViewModel, selectedCategory: Category=Category())
{
    val globalViewModel = sharedViewModel.globalViewModelData.collectAsState()
    val category = globalViewModel.value.categories.find { it.id == selectedCategory.id }
    var devicesList by remember { mutableStateOf(DevicesList()) }
    var mode by remember { mutableStateOf("") }
    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd)
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.clickable {
                    navController.popBackStack()
                })
                {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Add Device")
                }
                Text(modifier = Modifier.weight(1f), text = "${selectedCategory.name} ${getRandomString()}", fontSize = 20.sp)
                Box(modifier = Modifier.clickable {
                    mode="create"
                    devicesList=DevicesList()
                })
                {
                    Icon( Icons.Rounded.Devices, contentDescription = "Pair Device")
                }
            }

           Row(modifier = Modifier.fillMaxSize().fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)){
               category?.let {
                   val devices = category.devices
                   if (devices.isNotEmpty()) {
                       Column(modifier = Modifier.weight(1f),
                           verticalArrangement = Arrangement.spacedBy(10.dp))
                       {
                           Text(text = "Devices: ", fontSize = 16.sp, color = Color.Gray)

                           LazyVerticalGrid(
                               columns = GridCells.Fixed(2), // Set to 2 columns
                               modifier = Modifier
                                   .fillMaxSize().fillMaxWidth().fillMaxHeight() ,
                               horizontalArrangement = Arrangement.spacedBy(2.dp),
                               verticalArrangement = Arrangement.spacedBy(2.dp)
                           ) {
                               items(devices) { device ->
                                   DeviceItem(device,devicesList, onDelete = {
                                     if(device.sTableName.isNotEmpty())
                                     {
                                         val jsonObject = JsonObject(mapOf(
                                             "id" to JsonPrimitive(device.id),
                                             "tableName" to JsonPrimitive(device.roomId),
                                             "roomId" to JsonPrimitive(device.roomId),
                                             "scheduleTable" to JsonPrimitive(device.sTableName)
                                         ))
                                         val objectString = Json.encodeToString(JsonObject.serializer(), jsonObject)
                                        webSocketClient.sendMessage("DeleteDevice$objectString")
                                     }
                                       else{
                                           SnackbarManager.showSnackbar("empty sname")
                                     }
                                   }, clickabled = {
                                      devicesList=device
                                      mode=""
                                       })
                               }
                           }
                       }
                   }
               }
                Column (modifier = Modifier.weight(2f).fillMaxHeight())
                {

                     if(mode=="create"|| category?.devices?.isEmpty() == true)
                    {
                        AddDevice(webSocketClient,navController,sharedViewModel,selectedCategory,true)
                    }
                     else  if( mode==""&&devicesList.name.isNotEmpty())
                    {
                        DeviceConfiguration(
                            sharedViewModel=sharedViewModel,
                            selectedCategory=selectedCategory,
                            webSocketClient=webSocketClient,
                            devicesList = devicesList,
                            onDeviceChange = {
                            },  isItNew = false
                        )
                    }
                }
            }
        }

    FloatingActionButton(
        onClick = {  },
        modifier = Modifier.offset(x= (-20).dp,y=(-20).dp)
            .shadow(8.dp, RoundedCornerShape(32.dp))
            .size(56.dp).background (Color.White)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Lights Icon",
            tint = Color.White, // Customize the tint color as needed
            modifier = Modifier.size(48.dp) // Adjust icon size
        )
    }
    }
}

@Composable
fun DeviceItem(
    device: DevicesList, selectedDevicesList: DevicesList,
    onDelete:()->Unit,
    clickabled: () -> Unit) {
    Box(
        modifier = Modifier.clickable { clickabled() }
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if(selectedDevicesList.id==device.id) Color.Blue else Color.Gray)

    ) {
        Column(modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)){
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                iconMap[device.icon]?.let { Icon(imageVector = it, contentDescription = "Add Device") }
                Column(modifier = Modifier.weight(1f))
                {
                    Text(text = device.name)

                }

            }
            Text(text = "roomId: ${selectedDevicesList.roomId}", )
            Text(text = "Id: ${device.id}", )
            Text(text = "Type: ${device.type}", )
            Text(text = "Schedule: ${device.schedule.size}", )
            Text(text = "Destination: ${device.destination}", )
            IconButton(onClick = {  onDelete() })
            {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Edit Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
