package com.smart.home.Devices

import WebSocketService
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smart.home.Category
import com.smart.home.DeviceMetaData
import com.smart.home.DevicesList
import com.smart.home.ScheduleMetaData

@SuppressLint("NewApi")
@Composable
fun FullScreenScheduleEditorPopup(
    addSchedule: String,
    devicesList: DevicesList,
    selectedCategory: Category = Category(),
    tableName: String,
    webSocketClient: WebSocketService,
    schedule: ScheduleMetaData,
    selectedIndex: Int,
    onDone: () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDone
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.secondary) // Dimmed overlay
                .clickable { } // Dismiss on background click
        ) {

            // Schedule Card
            ScheduleCard(
                devicesList = devicesList,
                selectedCategory = selectedCategory,
                tableName = tableName,
                edit = (addSchedule == "edit"),
                webSocketClient = webSocketClient,
                schedule = if (addSchedule == "edit") schedule else ScheduleMetaData(
                    roomId = selectedCategory.id,
                    deviceMetaData = DeviceMetaData(type = devicesList.type),
                    deviceId = devicesList.id,
                ),
                onDone = onDone
            )
        }

    }
}