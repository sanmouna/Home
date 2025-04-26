package com.smart.home


import androidx.lifecycle.ViewModel
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable




class SharedViewModel : ViewModel() {
    // StateFlow for managing the message state

    private val _message = MutableStateFlow("Initial Message")
    val message: StateFlow<String> get() = _message

    // Function to update the message
    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }
    private val _globalViewModelData = MutableStateFlow(GlobalViewModel())
    val globalViewModelData: StateFlow<GlobalViewModel> get() = _globalViewModelData

    // Function to update GlobalViewModel
    fun updateGlobalData(newData: GlobalViewModel) {
        _globalViewModelData.value = newData
    }
    fun deleteCategoryById(id: String) {
        val currentCategories = globalViewModelData.value.categories
        val updatedList = currentCategories.filter { it.id != id }

        updateGlobalData(
            globalViewModelData.value.copy(
                categories = updatedList.toMutableList()
            )
        )
    }
    fun addDeviceToRoom(roomId: String, newDevice: DevicesList) {
        val currentRooms = globalViewModelData.value.categories // Assuming categories contain rooms
        val roomIndex = currentRooms.indexOfFirst { "devices_"+it.id == roomId }

        if (roomIndex != -1) {
            // Create a new instance of the devices list with the new device added
            val room = currentRooms[roomIndex]
            val updatedDevices = room.devices.toMutableList().apply { add(newDevice) }

            // Create a new room instance with the updated devices
            val updatedRoom = room.copy(devices = updatedDevices)

            // Create a new categories list with the updated room
            val updatedCategories = currentRooms.toMutableList().apply {
                set(roomIndex, updatedRoom)
            }

            // Update the global data with the new categories list
            updateGlobalData(globalViewModelData.value.copy(categories = updatedCategories))
        } else {
            println("Room with ID $roomId not found.")
        }
    }

    fun removeDeviceFromRoom(roomId: String, device: DevicesList) {
        val currentRooms = globalViewModelData.value.categories // Assuming categories contain rooms
        val roomIndex = currentRooms.indexOfFirst { "devices_"+it.id == roomId }

        if (roomIndex != -1) {
            // Get the room and create a new instance of the devices list without the device to be removed
            val room = currentRooms[roomIndex]
            val updatedDevices = room.devices.filterNot { it.id == device.id }

            // Create a new room instance with the updated devices
            val updatedRoom = room.copy(devices = updatedDevices.toMutableList())

            // Create a new categories list with the updated room
            val updatedCategories = currentRooms.toMutableList().apply {
                set(roomIndex, updatedRoom)
            }

            // Update the global data with the new categories list
            updateGlobalData(globalViewModelData.value.copy(categories = updatedCategories))
        } else {
            println("Room with ID $roomId not found.")
        }
    }

    // Function to add a category
    fun addCategory(category: Category) {
        val updatedList = globalViewModelData.value.categories.toMutableList().apply { add(category) }

        updateGlobalData(
            globalViewModelData.value.copy(
                categories = updatedList
            )
        )
    }

    fun addScheduleToDeviceInRoom(  newSchedule: ScheduleMetaData) {
        val currentRooms = globalViewModelData.value.categories // Assuming categories contain rooms
        val roomIndex = currentRooms.indexOfFirst { it.id == newSchedule.roomId }

        if (roomIndex != -1) {
            val room = currentRooms[roomIndex]
            val deviceIndex = room.devices.indexOfFirst { it.id == newSchedule.deviceId }

            if (deviceIndex != -1) {
                // Get the device and create a new instance with the updated schedule list
                val device = room.devices[deviceIndex]
                val updatedSchedules = device.schedule + newSchedule // Add new schedule to the list

                // Create a new device instance with the updated schedules
                val updatedDevice = device.copy(schedule = updatedSchedules)

                // Create a new list of devices for the room with the updated device
                val updatedDevices = room.devices.toMutableList().apply {
                    set(deviceIndex, updatedDevice)
                }

                // Create a new room instance with the updated devices
                val updatedRoom = room.copy(devices = updatedDevices)

                // Update the categories list with the modified room
                val updatedCategories = currentRooms.toMutableList().apply {
                    set(roomIndex, updatedRoom)
                }

                // Update the global data with the new categories list
                updateGlobalData(globalViewModelData.value.copy(categories = updatedCategories))
            } else {
                println("Receiving Device with ID $newSchedule.deviceId not found in room $newSchedule.roomId.")
            }
        } else {
            println("Receiving Room with ID $newSchedule.roomId not found.")
        }
    }
    fun editScheduleInDeviceInRoom(updatedSchedule: ScheduleMetaData) {
        val currentRooms = globalViewModelData.value.categories // Assuming categories contain rooms
        val roomIndex = currentRooms.indexOfFirst { it.id == updatedSchedule.roomId }


        if (roomIndex != -1) {
            val room = currentRooms[roomIndex]
            val deviceIndex = room.devices.indexOfFirst { it.id == updatedSchedule.deviceId }

            if (deviceIndex != -1) {
                // Get the device and find the index of the schedule to update
                val device = room.devices[deviceIndex]
                val scheduleIndex = device.schedule.indexOfFirst { it.id == updatedSchedule.id }

                if (scheduleIndex != -1) {
                    // Create a new list of schedules with the updated schedule
                    val updatedSchedules = device.schedule.toMutableList().apply {
                        set(scheduleIndex, updatedSchedule) // Replace the old schedule with the updated one
                    }

                    // Create a new device instance with the updated schedules
                    val updatedDevice = device.copy(schedule = updatedSchedules)

                    // Create a new list of devices for the room with the updated device
                    val updatedDevices = room.devices.toMutableList().apply {
                        set(deviceIndex, updatedDevice)
                    }

                    // Create a new room instance with the updated devices
                    val updatedRoom = room.copy(devices = updatedDevices)

                    // Update the categories list with the modified room
                    val updatedCategories = currentRooms.toMutableList().apply {
                        set(roomIndex, updatedRoom)
                    }

                    println("sched_change_ with ID ${updatedSchedule.id}")

                    updateGlobalData(globalViewModelData.value.copy(categories = updatedCategories))
                } else {
                    println("Schedule with ID ${updatedSchedule.id} not found in device ${updatedSchedule.deviceId}.")
                }
            } else {
                println("Device with ID ${updatedSchedule.deviceId} not found in room ${updatedSchedule.roomId}.")
            }
        } else {
            println("Room with ID ${updatedSchedule.roomId} not found.")
        }
    }

    fun deleteScheduleFromDeviceInRoom(scheduleToDelete: ScheduleMetaData) {
        val currentRooms = globalViewModelData.value.categories // Assuming categories contain rooms
        val roomIndex = currentRooms.indexOfFirst { it.id == scheduleToDelete.roomId }

        if (roomIndex != -1) {

            val room = currentRooms[roomIndex]
            val deviceIndex = room.devices.indexOfFirst { it.id == scheduleToDelete.deviceId }

            if (deviceIndex != -1) {
                // Get the device and filter out the schedule to be deleted
                val device = room.devices[deviceIndex]
                val updatedSchedules = device.schedule.filter { it.id != scheduleToDelete.id }

                // Create a new device instance with the updated schedules
                val updatedDevice = device.copy(schedule = updatedSchedules)

                // Create a new list of devices for the room with the updated device
                val updatedDevices = room.devices.toMutableList().apply {
                    set(deviceIndex, updatedDevice)
                }

                // Create a new room instance with the updated devices
                val updatedRoom = room.copy(devices = updatedDevices)

                // Update the categories list with the modified room
                val updatedCategories = currentRooms.toMutableList().apply {
                    set(roomIndex, updatedRoom)
                }

                // Update the global data with the new categories list
                updateGlobalData(globalViewModelData.value.copy(categories = updatedCategories))
            } else {
                println("Device with ID ${scheduleToDelete.deviceId} not found in room ${scheduleToDelete.roomId}.")
            }
        } else {
            println("Room with ID ${scheduleToDelete.roomId} not found.")
        }
    }

}

@Serializable
data class DeviceTypeModel(
    @SerializedName("brightness")
    val brightness: Int?=0,
    @SerializedName("maxBrightness")
    val maxBrightness: Int?=100  ,
    @SerializedName("icon")
    val icon: String?="",
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("isRGB")
    val isRGB: Int?=0,
    @SerializedName("maxLevel")
    val maxLevel: Int?=100,
    @SerializedName("level")
    val level: Int?=1,
    @SerializedName("mode")
    val mode: String?="0",
    @SerializedName("modeValue")
    val modeValue: String?="",
    @SerializedName("more")
    val more: String?="",
    @SerializedName("name")
    var name: String?="",
    @SerializedName("rgbColor")
    val rgbColor: String?="",
)

@Serializable
data class GlobalViewModel(
    @SerializedName("sensors")
    val sensors: List<String> = listOf("ldr", "pir", "temperature"),
    @SerializedName("sensorMetaData")
    val sensorMetaData: SensorMetaData = SensorMetaData(),
    @SerializedName("devicesTypes")
    val devicesTypes: List<PairDevice> = listOf(),
    @SerializedName("devicesCat")
    val devicesCat: List<String> = listOf("FAN","TV","SMD5050","NeoPixel","PWM","H_L"),
    @SerializedName("scenes")
    val scenes: List<Scene> = listOf(
        Scene(
        id = "1",
        name = "Good Morning",
        categories = listOf(

        ),
        schedules = listOf(
            ScheduleMetaData(
                priority = 1,
                enabled = 1,
                startTime = "07:00",
                endTime = "09:00"
            )
        )
    )),
    @SerializedName("categories")
    var categories: MutableList<Category> = mutableListOf(
       Category(id = "BR", name = "Bedroom",  )
    )

)


@Serializable
data class SensorMetaData(
    @SerializedName("id")
    val id: String="",
    @SerializedName("priority")
    val priority: Int = 0,
    @SerializedName("enabled")
    val enabled: Int = 0,
    @SerializedName("value")
    val value: Int = 1,
    @SerializedName("sensorName")
    val sensorName: String = "",
    @SerializedName("triggerAt")
    val triggerAt: Int = 1,
    @SerializedName("type")
    val type: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("relayon")
    val relayon: String = "",
)
@Serializable
data class ScheduleMetaData(
    @SerializedName("id")
    val id: Int=-1,
    @SerializedName("deviceId")
    val deviceId: Int = -1,
    @SerializedName("tableName")
    val tableName: String="",
    @SerializedName("_running")
    val _running: Boolean=false,
    @SerializedName("roomId")
    val roomId: String="",
    @SerializedName("name")
    val name: String="",
    @SerializedName("priority")
    val priority: Int = 0,
    @SerializedName("enabled")
    val enabled: Int = 0,
    @SerializedName("startTime")
    val startTime: String = "22:00",
    @SerializedName("endTime")
    val endTime: String = "06:00",
    @SerializedName("deviceMetaData")
    var deviceMetaData: DeviceMetaData = DeviceMetaData(),
    @SerializedName("sensors")
    val sensors: List<SensorMetaData> = listOf()

)




@Serializable
data class DevicesList(
    @SerializedName("id")
    val id: Int=-1,
    @SerializedName("name")
    var name: String="",
    @SerializedName("icon")
    var icon: String="",
    @SerializedName("enabled")
    val enabled: Int = 1,
    @SerializedName("_actionType")
    val _actionType: String = "", //timer , user , sensor
    @SerializedName("_running")
    val _running: Boolean = false,
    @SerializedName("type")
    var type: String="FAN",
    @SerializedName("range")
    var range: Int = 1,
    @SerializedName("mode")
    val mode: String = "", //timer , user , sensor
    @SerializedName("destination")
    val destination: String = "",
    @SerializedName("rgbColor")
    val rgbColor: String = "",
    @SerializedName("outputMode")
    val outputMode: String = "",  //animation
    @SerializedName("schedule")
    val schedule: List<ScheduleMetaData> = listOf(),
    @SerializedName("roomId")
    val roomId: String = "",
    @SerializedName("sTableName")
    val sTableName: String = "",
)




@Serializable
data class DeviceMetaData(
    // Light properties
    @SerializedName("id")
    val id: String="",

    @SerializedName("pin")
    val pin: Int?=-1,

    @SerializedName("isEnabled")
    val isEnabled:  Int?=1,
    
    @SerializedName("isPIRmode")
    val isPIRmode: Int?=0,

    @SerializedName("pin_type")
    val pin_type: String="", //pwm/hl/smd/neo

    @SerializedName("num_pixels")
    val num_pixels: Int=60,

    @SerializedName("color")
    val color: String="255,255,0",
    @SerializedName("key")
    val key: String="",

    @SerializedName("animation_type")
    val animation_type: String="",

    @SerializedName("animation_delay")
    val animation_delay: Float = 0.01f  ,

    @SerializedName("initial_state")
    val initial_state: Int?=0,
    @SerializedName("typeOfAction")
    var typeOfAction: String = "",
    // DeviceMetaData properties with @SerializedName
    @SerializedName("_actionType")
    val _actionType: String = "", // timer, user, sensor
    @SerializedName("type")
    var type: String = "",
    @SerializedName("range")
    var range: Int = 1,   //no pins
    @SerializedName("mode")
    val mode: String = "", // timer, user, sensor
    @SerializedName("destination")
    val destination: String = "",
    @SerializedName("outputMode")
    val outputMode: String = "",
    @SerializedName("activeTimeout")
    val activeTimeout: Int = 0 // New field for active timeout,
)


//@Serializable
//data class DeviceMetaData(
//    @SerializedName("_actionType")
//    val _actionType: String = "", // timer, user, sensor
//    @SerializedName("_isActive")
//    val _isActive: Boolean = false, // Renamed field for active status
//    @SerializedName("type")
//    var type: String = "FAN",
//    @SerializedName("range")
//    var range: Int = 1,   //no pins
//    @SerializedName("mode")
//    val mode: String = "", // timer, user, sensor
//    @SerializedName("destination")
//    val destination: String = "#ESP1",
//    @SerializedName("rgbColor")
//    val rgbColor: String = "",
//    @SerializedName("outputMode")
//    val outputMode: String = "",
//    @SerializedName("activeTimeout")
//    val activeTimeout: Int = 0 // New field for active timeout,
//
//)

//{
//    "id": "NeoPixel4",
//    "isEnabled": true,
//    "timeout": 60,
//    "isPIRmode": true,
//    "pin_type": "Neopixel",
//    "num_pixels": 60, //range
//    "color": "0, 255, 255",
//    "animation_type": "random",
//    "rgb_type": null,
//    "r_pin": null,
//    "g_pin": null,
//    "b_pin": null,
//    "animation_delay": 0.01,
//    "initial_state": true
//},
@Serializable
data class PairDevice(
    @SerializedName("name")
    val name: String="",
    @SerializedName("icon")
    var icon: String="",
    @SerializedName("enabled")
    val enabled: Int = 0,
    @SerializedName("actionType")
    val actionType: String = "", //timer , user , sensor
    @SerializedName("globalStatus")
    val globalStatus: Boolean = false,
    @SerializedName("deviceTypeModel")
    var deviceTypeModel: DeviceTypeModel = DeviceTypeModel(),   //fan , light
    @SerializedName("schedule")
    val schedule: List<ScheduleMetaData> = listOf(),
)

@Serializable
data class Category(
    @SerializedName("id")
    val id: String="",
    @SerializedName("name")
    val name: String="",
    @SerializedName("icon")
    val icon: String="",
    @SerializedName("description")
    val description: String="",
    @SerializedName("devices")
    val devices:MutableList<DevicesList> = mutableListOf()
)

@Serializable
data class Scene(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("categories")
    val categories: List<Category> = listOf(),
    @SerializedName("schedules")
    val schedules: List<ScheduleMetaData> = listOf()
)


