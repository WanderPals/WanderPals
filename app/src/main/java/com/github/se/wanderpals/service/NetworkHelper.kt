package com.github.se.wanderpals.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import com.github.se.wanderpals.model.repository.TripsRepository

class NetworkHelper(context: Context, private val repository: TripsRepository) {
  private val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  init {
    val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
          override fun onAvailable(network: Network) {
            repository.isNetworkEnabled = true
            SessionManager.isNetworkAvailable = true
            Log.d("NetworkHelper", "Network is available.")
          }

          override fun onLost(network: Network) {
            repository.isNetworkEnabled = false
            SessionManager.isNetworkAvailable = false

            Log.d("NetworkHelper", "Network is lost.")
          }
        }

    connectivityManager.registerDefaultNetworkCallback(networkCallback)
  }
}
