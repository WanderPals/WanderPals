package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.BroadcastNotification
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

open class NotificationAPI : ViewModel() {

  private val api: NotificationInterface =
      Retrofit.Builder()
          .baseUrl("http://10.0.2.2:8080/")
          .addConverterFactory(MoshiConverterFactory.create())
          .build()
          .create()

  open fun sendNotification(tokenList: List<String>, body: String) {
    viewModelScope.launch {
      Log.d("Notification", "Sending notification")
      val newNotif = BroadcastNotification(tokenList, body, "WanderPals")
      try {
        api.sendMessage(newNotif)
      } catch (e: HttpException) {
        e.printStackTrace()
      }
    }
  }
}
