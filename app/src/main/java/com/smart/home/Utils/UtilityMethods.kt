package com.smart.home.Utils

// ThemeUtils.kt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

// Define custom colors
val LightBlue = Color(0xFF1E88E5)
val DarkBlueGray = Color(0xFF37474F)
val LightGrayBlue = Color(0xFFB0BEC5)
val Amber = Color(0xFFFFC107)
val ThumbInactive = Color(0xFF90A4AE)
val TextWhite = Color(0xFFFAFAFA)

// Utility method for color palette

object ColorConstants {
    val primary =  LightBlue
    val secondary = Amber
    val background =DarkBlueGray
    val textWhite = TextWhite
    val inActive=ThumbInactive
}
object DeviceType {
    const val TV = "TV"
    const val FAN = "FAN"
    const val LIGHT = "LIGHT"
    const val NIGHTLAMP = "NIGHTLAMP"
    const val MUSIC = "MUSIC"
    const val SOCKET = "SOCKET"
    const val NEOPIXEL = "NeoPixel"
    const val SMD5050 = "SMD5050"
    const val PWM="PWM"
    const val  HighOrLow="H_L"
}
object StringConstants {
    const val HOME = "Home"
    const val MAINHALL = "MAIN HALL"
    const val AddRoom = "AddRoom"
    const val CATEGORYVIEWER = "CATEGORYVIEWER"
    const val ADDDEVICE = "ADDDEVICE"
    const val IMPORTDEVICE = "IMPORTDEVICE"
    const val PAIRDEVICE = "PAIRDEVICE"
    const val SCHEDULEEDITOR="SCHEDULEEDITOR"
}
val destinations= listOf("PICO2","ESP2","MASTER")

val allDevicesList= listOf(
    "WRProfileLight",
    "WRLight1","WRLight2",
    "TempleProfileLight",
    "WRNeoPixel",
    "WRRoofNeoPixel",
    "bedLightNeo",
    "roofNeoPixelI",
    "KitchenLightH",
    "KitchenLightV",
    "roofSMD",

    "PWM1","M_L1","M_L2","M_L3","M_L4",
    "M_BedLight",
    "roofSMD",
    "roofNeoPixelI",
    "roofNeoPixelO","bedLightNeo")

fun randomColor(): Color {
    return Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
}


fun categorizedDevice(device: String = ""): Array<String> {
    return when {
        device.contains("SMD5050", ignoreCase = true) -> arrayOf("roofSMD",)
        device.contains("NeoPixel", ignoreCase = true) -> arrayOf("roofNeoPixelI","roofNeoPixelO","bedLightNeo")
        device.contains("PWM", ignoreCase = true) -> arrayOf()
        device.contains("H_L", ignoreCase = true) -> arrayOf("M_L1","M_L2","M_L3","M_L4","M_BedLight")
        else -> arrayOf()
    }
}


val iconMap = mapOf(
    "MeetingRoom" to Icons.Filled.MeetingRoom,
    "Home" to Icons.Filled.Home,
    "Settings" to Icons.Filled.Settings,
    "Lightbulb" to Icons.Filled.Lightbulb,
    "Air" to Icons.Filled.Air,
    "Tv" to Icons.Filled.Tv,
    "WbSunny" to Icons.Filled.WbSunny,
    "ModeNight" to Icons.Filled.ModeNight,
    "RunCircle" to Icons.Filled.RunCircle,
    "Festival" to Icons.Filled.Festival,
    "Restaurant" to Icons.Filled.Restaurant,
    "RestaurantMenu" to Icons.Filled.RestaurantMenu,
    "Security" to Icons.Filled.Security,
    "Movie" to Icons.Filled.Movie,
    "Emergency" to Icons.Filled.Emergency,
    "Favorite" to Icons.Filled.Favorite,
    "FavoriteBorder" to Icons.Filled.FavoriteBorder,
    "PowerSettingsNew" to Icons.Filled.PowerSettingsNew,
    "Power" to Icons.Filled.Power,
    "PowerOff" to Icons.Filled.PowerOff,
    "Sync" to Icons.Filled.Sync,
    "SettingsRemote" to Icons.Filled.SettingsRemote
)
