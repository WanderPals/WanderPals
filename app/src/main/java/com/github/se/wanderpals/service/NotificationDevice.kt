package com.github.se.wanderpals.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.github.se.wanderpals.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

const val FCM_ENDPOINT2 = "https://fcm.googleapis.com/fcm/send"

const val TOKEN =
    "AAAALI85TW0:APA91bHQIiTEFkzRUv6FQMlyL1TxtPBztg6lByt18vDaVLkssIEXkPrQu1WLX5Wc_WmTdYqoOWBITP2yp7ej4gH4LeH_iMZbz9lkQQJ-DVC8w2gRxhW8lp8gAuzCqaY136urySlFw-0p"

/** Class responsible for handling notifications on the device. */
class NotificationDevice : FirebaseMessagingService() {
  override fun onNewToken(token: String) {
    super.onNewToken(token)
    // send to the token to the server
    Log.d("New_Token", token)
    SessionManager.setNotificationToken(token)
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    Log.d("Message", remoteMessage.notification?.body.toString())

    val channelId = "1234"
    val description = "Trip Notification"
    // handle the message with NotificationCompat
    val notification =
        NotificationCompat.Builder(this, channelId)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.logo_email)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(channelId, description, IMPORTANCE_DEFAULT)
      notificationManager.createNotificationChannel(channel)
    }
    notificationManager.notify(0, notification.build())
  }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun NotificationPermission(context: Context) {
  Log.d("Permission", "Checking Permission")

  var permissionDenied by remember { mutableStateOf(false) }

  val requestPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission(),
          onResult = { isGranted: Boolean ->
            if (isGranted) {
              Log.d("Permission", "Permission Granted")
              // post notif ok

            } else {
              // display another dialog
              permissionDenied = true
            }
          })
  Log.d("Permission", "Checking Permission2")
  Log.d("Permission", "${Build.VERSION.SDK_INT}")

  // This is only necessary for API level >= 33 (TIRAMISU)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED) {
      Log.d("Permission", "Permission Granted")
      // FCM SDK (and your app) can post notifications.
    } else {
      // Directly ask for the permission
      SideEffect { requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
    }
  }
  Log.d("Permission", "Permission already granted")
}

@SuppressLint("SuspiciousIndentation")
suspend fun sendMessageToListOfUsers(deviceToken: String, message: String) {
  // Create the notification payload
  val notificationPayload =
      mapOf("notification" to mapOf("body" to message, "time" to "Wanderpals"), "to" to deviceToken)

  // log the payload
  Log.d("FCM", "Payload: $notificationPayload")

  // Convert the payload to JSON
  val gson = Gson()
  val jsonPayload = gson.toJson(notificationPayload)

  // Create an OkHttpClient instance
  val client = OkHttpClient()

  // Create a request body with JSON content type
  val requestBody = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())

  return withContext(Dispatchers.IO) {
    // Create a POST request to the FCM endpoint
    val request =
        Request.Builder()
            .url(FCM_ENDPOINT2)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "key=${TOKEN}")
            .post(requestBody)
            .build()
    Log.d("FCM", "Sending message to $deviceToken")

    // Execute the request
    val response = client.newCall(request).execute()

    // Print the response
    println(response.body?.string())
  }
}