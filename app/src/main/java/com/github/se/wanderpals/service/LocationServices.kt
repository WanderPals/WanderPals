package com.github.se.wanderpals.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.se.wanderpals.R
import com.github.se.wanderpals.mapManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/** Service that tracks the user's location, sends a notification and updates the map. */
class LocationService : Service() {

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val interval = 10000L
  private val notificationId = 2

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    val channel = NotificationChannel("location", "Location", NotificationManager.IMPORTANCE_LOW)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_START -> start()
      ACTION_STOP -> stop()
    }
    return super.onStartCommand(intent, flags, startId)
  }

  private fun start() {
    val notification =
        NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("You current location is being tracked and uploaded.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setChannelId("location")
            .setOngoing(true)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    mapManager
        .getLocationUpdates(interval)
        .catch { e -> Log.e("LocationService", "Error: $e") }
        .onEach { location ->
          notificationManager.notify(notificationId, notification.build())
          Log.d("LocationService", "Location: $location")
          mapManager.updatePosition(LatLng(location.latitude, location.longitude))
        }
        .launchIn(serviceScope)

    startForeground(notificationId, notification.build())
  }

  private fun stop() {
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
  }

  override fun onDestroy() {
    super.onDestroy()
    serviceScope.cancel()
  }

  companion object {
    const val ACTION_START = "ACTION_START"
    const val ACTION_STOP = "ACTION_STOP"
  }
}
