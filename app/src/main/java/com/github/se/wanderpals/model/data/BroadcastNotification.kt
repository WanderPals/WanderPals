package com.github.se.wanderpals.model.data

data class BroadcastNotification(
    val targetToken: List<String> = emptyList(),
    val body: String = "",
    val title: String = ""
)
