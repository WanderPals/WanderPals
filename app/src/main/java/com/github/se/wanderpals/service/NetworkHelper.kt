package com.github.se.wanderpals.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import com.github.se.wanderpals.model.repository.TripsRepository

/**
 * Manages network state notifications and updates application components accordingly.
 *
 * This class utilizes the [ConnectivityManager] to monitor network availability changes and informs
 * the [TripsRepository] and [SessionManager] of the current network status. It registers a callback
 * that updates the network availability status in the repository and session manager whenever the
 * network is available or lost.
 *
 * @param context The application context used to access the [ConnectivityManager].
 * @param repository The instance of [TripsRepository] to update with the network status.
 */
class NetworkHelper(context: Context, private val repository: TripsRepository) {
  private val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  init {
    val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
          override fun onAvailable(network: Network) {
            repository.isNetworkEnabled = true
            SessionManager.setIsNetworkAvailable(true)
            Log.d("NetworkHelper", "Network is available.")
          }

          override fun onLost(network: Network) {
            repository.isNetworkEnabled = false
            SessionManager.setIsNetworkAvailable(false)

            Log.d("NetworkHelper", "Network is lost.")
          }
        }

    connectivityManager.registerDefaultNetworkCallback(networkCallback)
  }
}
