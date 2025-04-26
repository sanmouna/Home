import android.content.Context
import com.google.gson.GsonBuilder
import com.smart.home.Category
import com.smart.home.DevicesList
import com.smart.home.ScheduleMetaData
import com.smart.home.SharedViewModel
import com.smart.home.SnackbarManager
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import java.util.Timer

class WebSocketService(private val context: Context) {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)  // Disable timeout, WebSocket is long-lived
        .build()

    private lateinit var webSocket: WebSocket
    private var reconnectionAttempts = 0
    private val maxReconnectionAttempts = 100
    private val baseReconnectionDelay = 1000L // Start with 2 seconds

    fun connectWebSocket(sharedViewModel: SharedViewModel) {
        val sharedData= sharedViewModel.globalViewModelData.value
        val request = Request.Builder()
            .url("ws://192.168.0.185:8080") // Your WebSocket URL
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                reconnectionAttempts = 0 // Reset attempts on successful connection
                 SnackbarManager.showSnackbar("WebSocket Connected!")
               webSocket.send("!TAB!")
                webSocket.send("Rooms")
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Receiving: $text")

                 try {

                      if(text.startsWith("Rooms"))
                     {
                         val devicesList: Array<Category> = GsonBuilder().create().fromJson(
                             text.replace("Rooms", "").replace("#",""),
                             Array<Category>::class.java
                         )
                         val mutableDevicesList: MutableList<Category> =
                             devicesList.toMutableList()
                         sharedData.categories = mutableDevicesList
                         sharedViewModel.updateGlobalData(sharedData)
                     }
                     else if(text.startsWith("AddRoom"))
                     {
                         val room: Category = GsonBuilder().create().fromJson(
                             text.replace("AddRoom", ""),
                             Category::class.java
                         )
                         sharedViewModel.addCategory(room)
                       }
                     else if(text.startsWith("AddDevice"))
                     {
                         val device: DevicesList = GsonBuilder().create().fromJson(
                             text.replace("AddDevice", ""),
                             DevicesList::class.java
                         )
                         SnackbarManager.showSnackbar(device.roomId)
                         sharedViewModel.addDeviceToRoom(device.roomId,device)
                     }
                      else if(text.startsWith("insertschedule_"))
                      {
                          val scheduleMetaData: ScheduleMetaData = GsonBuilder().create().fromJson(
                              text.replace("insertschedule_", ""),
                              ScheduleMetaData::class.java
                          )
                          sharedViewModel.addScheduleToDeviceInRoom(scheduleMetaData)
                      } else if(text.startsWith("editschedule_"))
                      {
                          val scheduleMetaData: ScheduleMetaData = GsonBuilder().create().fromJson(
                              text.replace("editschedule_", ""),
                              ScheduleMetaData::class.java
                          )
                          sharedViewModel.editScheduleInDeviceInRoom(scheduleMetaData)
                      }
                      else if(text.startsWith("sched_change_"))
                      {
                          val scheduleMetaData: ScheduleMetaData = GsonBuilder().create().fromJson(
                              text.replace("sched_change_", ""),
                              ScheduleMetaData::class.java
                          )
                          sharedViewModel.editScheduleInDeviceInRoom(scheduleMetaData)
                      }
                      else if(text.startsWith("deleteschedule_"))
                      {
                          val scheduleMetaData: ScheduleMetaData = GsonBuilder().create().fromJson(
                              text.replace("deleteschedule_", ""),
                              ScheduleMetaData::class.java
                          )
                          sharedViewModel.deleteScheduleFromDeviceInRoom(scheduleMetaData)
                      }

                      else if(text.startsWith("DeleteDevice"))
                      {
                          val device: DevicesList = GsonBuilder().create().fromJson(
                              text.replace("DeleteDevice", ""),
                              DevicesList::class.java
                          )
                           sharedViewModel.removeDeviceFromRoom(device.roomId,device)
                      }
                     else if(text.startsWith("DeleteRoom"))
                     {
                         sharedViewModel.deleteCategoryById(text.replace("DeleteRoom",""))
                     }
                     else if(text.isNotEmpty()&&text.startsWith("$"))  {

                         SnackbarManager.showSnackbar(text)
                     }
                 }
                 catch (e:Error)
                 {
                  println(e)
                     SnackbarManager.showSnackbar("--/////--"+e.message)
                 }


            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                println("Receiving bytes: ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Closing: $code / $reason")
                webSocket.close(1000, "Connection closed by client.")
                attemptReconnection(sharedViewModel)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Failure: ${t.message}")
               SnackbarManager.showSnackbar("Error: ${t.message}")
                attemptReconnection(sharedViewModel)
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        if (::webSocket.isInitialized) {
            //SnackbarManager.showSnackbar("Sending.....")
            webSocket.send(message) // Send the message
        } else {
            println("WebSocket is not initialized!")
        }
    }

    fun closeWebSocket() {
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "Goodbye!")
        }
    }

    private fun attemptReconnection(sharedViewModel: SharedViewModel) {
        if (reconnectionAttempts < maxReconnectionAttempts) {
            reconnectionAttempts++
            val reconnectionDelay = baseReconnectionDelay * reconnectionAttempts
            println("Reconnecting in ${reconnectionDelay / 1000} seconds... (Attempt $reconnectionAttempts)")

            Timer().schedule(reconnectionDelay) {
              SnackbarManager.showSnackbar("Reconnecting....")
                connectWebSocket(sharedViewModel)
            }
        } else {
            println("Max reconnection attempts reached. Giving up.")
           // SnackbarManager.showSnackbar("Failed to reconnect after $maxReconnectionAttempts attempts.")
        }
    }
}
