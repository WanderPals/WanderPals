package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.BroadcastNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

/**
 * ViewModel responsible for managing notifications. Provides functionality to send notifications to
 * users.
 *
 * @constructor Creates a NotificationAPI.
 * @property state The state of the notification.
 * @property client The OkHttpClient.
 * @property interceptor The HttpLoggingInterceptor.
 * @property clientBuilder The OkHttpClient.Builder.
 * @property api The NotificationInterface.
 */
open class NotificationAPI : ViewModel() {
  var state by mutableStateOf(true)

  private val client = OkHttpClient()
  private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
  private val clientBuilder: OkHttpClient.Builder =
      client.newBuilder().addInterceptor(interceptor as HttpLoggingInterceptor)

  private val api: NotificationInterface =
      Retrofit.Builder()
          .baseUrl("http://wanderpals.duckdns.org:8080/")
          .addConverterFactory(GsonConverterFactory.create())
          .client(clientBuilder.build())
          .build()
          .create(NotificationInterface::class.java)

  open fun sendNotification(tokenList: List<String>, body: String) {
    viewModelScope.launch(Dispatchers.IO) {
      Log.d("Notification", "Sending notification")
      val newNotif = BroadcastNotification(tokenList, body, "WanderPals")
      try {
        api.sendMessage(newNotif)
      } catch (e: HttpException) {
        e.printStackTrace()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }
}
