package com.smart.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.rounded.Cottage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smart.home.Utils.StringConstants
import WebSocketService
import android.os.Build
import androidx.annotation.RequiresApi

import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smart.home.Devices.FullScreenScheduleUpdatePopup
import com.smart.home.Devices.ScheduleCard

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(navController: NavController, sharedViewModel: SharedViewModel,webSocketClient : WebSocketService) {
    var isDarkMode by remember { mutableStateOf(false) }
    val globalViewModel by sharedViewModel.globalViewModelData.collectAsState()
    val message by sharedViewModel.message.collectAsState()
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1c1c1a)),
        contentAlignment = Alignment.BottomEnd) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row (
                modifier = Modifier
                    .fillMaxWidth().clip(shape = RoundedCornerShape(10.dp))
                    .shadow(2.dp, spotColor = Color.Red) // Apply shadow here
                    .background(Color(0xFF095F59)),
                verticalAlignment = Alignment.CenterVertically, // Center the content vertically
                horizontalArrangement = Arrangement.Start // Align content to the start (left)
            )           {
                Icon (
                    imageVector = Icons.Rounded.Cottage, // Profile icon
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(10.dp), // Padding inside the icon
                    tint = Color.White // Set the icon color
                )

                Spacer(modifier = Modifier.width(12.dp)) // Space between icon and username

                Text(
                    style = TextStyle(fontWeight = FontWeight.Medium,fontSize=22.sp),
                    text = "Hello, Conversa",
                    color = Color.White // Set text color
                )
            }



            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                globalViewModel.categories .forEach { it->
                    RoundedIconButtonRow(color=Color(0xFFFFA500),
                        icon = Icons.Default.MeetingRoom,text=it.name,
                        onClick = {
                            val gson: Gson = GsonBuilder().create()
                            val category = gson.toJson(it)
                            println("wow"+it)
                            navController.navigate(
                                "${StringConstants.CATEGORYVIEWER}/{category}" //Just modify your route accordingly
                                    .replace(
                                        oldValue = "{category}",
                                        newValue = category.replace("#ESP","ESP")
                                    ),
                            ){
                                launchSingleTop = true
                            }
                        })
                    {
                        SnackbarManager.showSnackbar("DeleteRoom"+it.id)
                        webSocketClient.sendMessage("DeleteRoom"+it.id)
                    }
                }

                RoundedIconButtonRow(color=Color.Black,
                    icon = Icons.Default.Add,text="Add Room",
                    onClick = {
                   navController.navigate(StringConstants.AddRoom){
                       launchSingleTop = true
                   }
                    })
                {

                }

            }
//            Box(     modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(10.dp))
//                .shadow(2.dp, spotColor = Color.Red) // Apply shadow here
//                .background(Color(0xFF095F59))
//                .padding(8.dp))
//            {
//                Text(modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(4.dp) ,
//                    style = TextStyle(fontWeight = FontWeight.Medium,fontSize=18.sp),
//                    text = "Scence",
//                    color = Color.White // Set text color
//                )
//            }

//            Row(
//                modifier = Modifier.align(Alignment.CenterHorizontally).padding(4.dp)
//                    .horizontalScroll(rememberScrollState()),
//                horizontalArrangement = Arrangement.spacedBy(10.dp)
//            ) {
//                RoundedIconButton(color=Color.Red,icon = Icons.Default.WbSunny,text="Good Morning", onClick = {
//
//                })
//                RoundedIconButton(color=Color.Black, icon = Icons.Default.ModeNight,text="Good Night", onClick = {
//
//                })
//                RoundedIconButton(color=Color(0xFFFF7F50),icon = Icons.Default.RunCircle,text="Away From Home", onClick = {
//
//                })
//                RoundedIconButton(color=Color.Red,icon = Icons.Default.Festival,text="Festival Scene", onClick = {
//
//                })
//                RoundedIconButton(color=Color(0xFF3E2723),icon = Icons.Default.PlayForWork,text="Relaxation Scene", onClick = {
//
//                })
//                RoundedIconButton(color=Color(0xFF800080),icon = Icons.Default.Movie,text="Movie Time", onClick = {
//
//                })
//                RoundedIconButton(color=Color.Blue,icon = Icons.Default.Security,text="Secure Mode", onClick = {
//
//                })
//                RoundedIconButton(color=Color.Magenta,icon = Icons.Default.Emergency,text="Emergency Scene", onClick = {
//
//                })
//            }

            Box(     modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(10.dp))
                .shadow(2.dp, spotColor = Color.Red) // Apply shadow here
                .background(Color(0xFF095F59))
                .padding(8.dp))
            {
                Text(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp) ,
                    style = TextStyle(fontWeight = FontWeight.Medium,fontSize=18.sp),
                    text = "Running..",
                    color = Color.White // Set text color
                )
            }
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                globalViewModel.categories .forEach { cat->
                    cat.devices.forEach{ device ->
                        device.schedule.forEachIndexed { index, scheduleMetaData ->
                            RoundedCardButton(
                                webSocketClient=webSocketClient,
                                device=device,
                                scheduleMetaData = scheduleMetaData.copy(deviceMetaData = scheduleMetaData.deviceMetaData.copy(destination = device.destination)),
                                icon = Icons.Default.Favorite,
                                onClick = {
                                     webSocketClient.sendMessage(
                                         GsonBuilder().create().toJson(
                                         scheduleMetaData.copy(
                                             deviceMetaData = scheduleMetaData.
                                             deviceMetaData.copy(
                                         initial_state = if(it) 1 else 0,
                                         destination = device.destination))))
                                },
                            onUpdation={

                            })
                        }
                    }

                }

            }


        }
        FloatingActionButton(
            onClick = {
                navController.navigate(StringConstants.ADDDEVICE){
                    launchSingleTop = true
                }
            },
            // Change background color based on the theme
            contentColor = if (isDarkMode) Color.Black else Color.White, // Change icon color
            modifier = Modifier .offset(x= (-10).dp,y=(-10).dp)
                .shadow(8.dp, RoundedCornerShape(32.dp),
                )
                .size(56.dp).background (if (isDarkMode) Color.White else Color.Black)
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightsStay, // Light and dark mode icons
                contentDescription = "Toggle Theme"
            )
        }
    }
}


@Composable
fun RoundedIconButtonRow(icon: ImageVector, onClick: () -> Unit,
                         text: String="",color:Color=Color.Black,
                         onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(36.dp),) // Apply shadow here
            .background(Color.White)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically, // Center align vertically
            horizontalArrangement = Arrangement.spacedBy(5.dp) // Space between icon and text
        ) {
            Box(
                modifier = Modifier.size(36.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)) // Shadow effect
                    .background(color,
                        shape = RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.padding(6.dp),
                    imageVector = icon,
                    contentDescription = null, // Provide a description if needed
                    tint = Color.White // Set icon color to white for contrast
                )
            }
            Text(
                modifier = Modifier.padding(end = 10.dp),
                style = TextStyle(fontWeight = FontWeight.Medium,fontSize=18.sp),
                text = text,
                color = Color.Black // Set text color
            )
       if(text.lowercase()!="add room")
       {
           Box(
               modifier = Modifier.size(36.dp).clickable {
                   onDelete()
               }
                   .shadow(8.dp, RoundedCornerShape(24.dp)) // Shadow effect
                   .background(color,
                       shape = RoundedCornerShape(24.dp)),
               contentAlignment = Alignment.Center,
           ) {
               Icon(
                   modifier = Modifier.padding(6.dp),
                   imageVector = Icons.Default.DeleteForever,
                   contentDescription = null, // Provide a description if needed
                   tint = Color.White // Set icon color to white for contrast
               )
           }
       }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoundedCardButton(
    webSocketClient: WebSocketService,
    icon: ImageVector, onClick: (Boolean) -> Unit,
    scheduleMetaData: ScheduleMetaData = ScheduleMetaData(),
    device: DevicesList,
    onUpdation: () -> Unit
) {

    var isDialogVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxHeight().width(200.dp)
            .clickable {
                if(scheduleMetaData.deviceMetaData.initial_state==1)
                isDialogVisible = true

            }
            .shadow(4.dp, RoundedCornerShape(10.dp),) // Apply shadow here
            .background(Color(0xFF838382))
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .fillMaxHeight() , // Padding inside the Column
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), // Fills the full width of the Column
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .background(Color(0xFF095F59), shape = RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.padding(6.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                //   CustomSwitchWithIcon()
                VerticalSwitchButton1(isChecked =  scheduleMetaData.deviceMetaData.initial_state==1, onClick =onClick)
            }
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = "Device: "+device.name,
                color = Color.White
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = device.destination,
                color = Color.White
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = "Device Id: "+scheduleMetaData.deviceMetaData.id,
                color = Color.White
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = "Name: "+scheduleMetaData.name,
                color = Color.White
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = "Animation: "+scheduleMetaData.deviceMetaData.animation_type,
                color = Color.White
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = "Animation Delay: "+scheduleMetaData.deviceMetaData.animation_delay.toString(),
                color = Color.White
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = "TimeOut: "+scheduleMetaData.deviceMetaData.activeTimeout.toString(),
                color = Color.White
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                text = "Color: "+scheduleMetaData.deviceMetaData.color,
                color = Color.White
            )
        }
    }


    if(isDialogVisible)
    {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                isDialogVisible=false
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.secondary) // Dimmed overlay

            ) {

                // Schedule Card
                FullScreenScheduleUpdatePopup(
                    webSocketClient=webSocketClient,
                    schedule = scheduleMetaData,
                    devicesList = device,
                    tableName = device.sTableName)
                {
                    isDialogVisible=false
                }
            }

        }
    }

}



@Composable
fun VerticalToggleButton( ) {
    var isOn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(Color.Gray, shape = RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        // Background for the toggle button
        val backgroundColor = if (isOn) Color.Green else Color.Red
        Box(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                .clickable { isOn=!isOn } // Handle click to toggle state
        ) {
            // Content (icon or text) inside the toggle
            Text(
                text = if (isOn) "ON" else "OFF",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
//            Switch(checked = isOn, onCheckedChange = {isOn=it})

        }
    }
}



@Composable
fun VerticalSwitchButton1(
    isChecked:Boolean=false,
    imageVector: ImageVector=Icons.Default.PowerSettingsNew,
    onClick: (Boolean) -> Unit
) {
    // State to determine if the switch is checked


    // Animate the thumb position based on isChecked state
    val thumbPosition by animateDpAsState(
        targetValue = if (!isChecked) 30.dp else 2.dp
    )

    Box(
        modifier = Modifier
            .width(34.dp) // Width for the switch
            .height(64.dp) // Height for the switch
            .background(if(isChecked) Color.White else  Color(0xFF3c3e3e), shape = RoundedCornerShape(34.dp)) // Background color
            .clickable {
                onClick(!isChecked)
                       } ,

        ) {
        // Thumb that moves vertically
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x=2.dp,y=thumbPosition,)
                .background(if(isChecked) Color.Magenta else Color(0xFF7f8182), shape = RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "Lights Icon",
                tint =  Color.White,
                modifier = Modifier.size(20.dp) // Size of the icon inside the thumb
            )
        }
    }
}




@Composable
fun CustomSwitchWithIcon() {
    var isChecked by remember { mutableStateOf(false) }
    val thumbPosition by animateDpAsState(
        targetValue = if (isChecked) 30.dp else 0.dp
    )
    val switchBackgroundColor by animateColorAsState(
        targetValue = if (isChecked) Color.Green else Color.Gray
    )
    Box(
        modifier = Modifier
            .width(60.dp) // Width for the switch
            .height(30.dp) // Height for the switch
            .shadow(8.dp, shape = MaterialTheme.shapes.small) // Shadow effect
            .background(Color.LightGray, shape = MaterialTheme.shapes.small) // Background color
            .clickable { isChecked = !isChecked }, // Toggle the state on click
        contentAlignment = Alignment.Center // Center the content vertically and horizontally
    ) {
        // The thumb with the shutdown icon
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(y = thumbPosition,x=2.dp) // Move the thumb vertically
                .background(Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Shutdown Icon",
                tint = if (isChecked) Color.Green else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomSwitchWithIcon() {
    CustomSwitchWithIcon()
}

@Composable
fun VerticalSwitchButton() {
    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (isChecked) "Switch is ON" else "Switch is OFF")

        Spacer(modifier = Modifier.height(8.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
    }
}




@Composable
fun SecondScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Second Screen")
    }
}