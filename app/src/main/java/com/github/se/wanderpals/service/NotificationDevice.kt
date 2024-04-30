package com.github.se.wanderpals.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.github.se.wanderpals.R
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationDevice: FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        //send to the token to the server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelId = "1234"
        val description = "Test Notification"


        //handle the message with NotificationCompat
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, description, IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notification)
    }


}



    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun NotificationPermission(context: Context) {

        val openDialog = remember { mutableStateOf(false) }
        var hasNotificationPermission by remember { mutableStateOf(false)}
        var permissionDenied by remember {
            mutableStateOf(false)
        }


        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {
                    //post notif ok
                    hasNotificationPermission = true

                } else {
                    //display another dialog
                    permissionDenied = true
                }
            }
        )


            // This is only necessary for API level >= 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    hasNotificationPermission = true
                    // FCM SDK (and your app) can post notifications.
                } else if (ComponentActivity().shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // TODO: display an educational UI explaining to the user the features that will be enabled
                    //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                    //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                    //       If the user selects "No thanks," allow the user to continue without notifications.
                    ShowRationalPermissionDialog(openDialog) {
                        requestPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }

                } else {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }




    @Composable
    fun ShowRationalPermissionDialog(openDialog: MutableState<Boolean>, onclick: () -> Unit) {
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = "Alert") },
                text = { Text("Notification permission is required, to show notification") },
                confirmButton = {
                    TextButton(
                        onClick = { onclick() },
                        modifier = Modifier.testTag("confirmDeleteButton")
                    ) {
                        Text("Ok", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { openDialog.value = false },
                        modifier = Modifier.testTag("cancelDelete")
                    ) {
                        Text("Cancel")
                    }
                },
                modifier = Modifier.testTag("deleteDialog")
            )
        }
    }



//function to subscribe to a topic
/*fun firebaseSuscribedForGroupNotifications(tripName: String, baseContext: Context) {
    Firebase.messaging.subscribeToTopic("Trip_$tripName")
        .addOnCompleteListener {task ->
            var msg = "Subscribed to Trip_$tripName"
            if (!task.isSuccessful) {
                msg = "Failed to subscribe to Trip_$tripName"
            }
            Log.d("Firebase", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

        }
}*/

//function to send message to a topic from a client app

/*@Composable
fun sendMessageToListOfUsers(deviceTokens: List<String>, tripName: String, message: String) {
    val notification = RemoteMessage.Builder("Trip_$tripName")
        .setMessageId("Trip_$tripName")
        .addData("title", title)
        .addData("message", message)
        .build()

    try {
        Firebase.messaging
    } catch (e: Exception) {
        Log.d("Firebase", "Failed to send message to Trip_$title")
    }



}*/


