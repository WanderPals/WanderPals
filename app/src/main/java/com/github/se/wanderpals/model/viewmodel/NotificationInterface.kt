package com.github.se.wanderpals.model.viewmodel

import com.github.se.wanderpals.model.data.BroadcastNotification
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationInterface {

    @POST("/send")
    suspend fun sendMessage(
        @Body notification: BroadcastNotification
    )
}