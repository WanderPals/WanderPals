package com.github.se.wanderpals.model.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.wanderpals.MainActivity
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * ViewModel to manage data for the main user interface components. It handles the initialization
 * and provision of the TripsRepository.
 *
 * @param application The context of the application passed to AndroidViewModel for use with
 *   application-wide tasks.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

  private lateinit var tripsRepository: TripsRepository
  private lateinit var shortcutManager: ShortcutManager
  private var overviewJoinTripDialogIsOpen = false

  /**
   * Initializes the TripsRepository with a specific user ID. This method must be called immediately
   * after a user successfully signs in.
   *
   * @param userId The unique identifier for the user, used to initialize the repository.
   */
  fun initRepository(userId: String) {
    Log.d("MainViewModel", "initRepository: $userId")
    if (::tripsRepository.isInitialized) {
      tripsRepository.uid = userId
    } else {
      tripsRepository = TripsRepository(userId, Dispatchers.IO)
      tripsRepository.initFirestore()
    }
  }

  /** Checks if the TripsRepository has been initialized. */
  fun isRepositoryInitialized(): Boolean {
    return ::tripsRepository.isInitialized
  }

  /**
   * Initializes the ShortcutManager for the application. This method must be called immediately
   *
   * @param shortcutManager The ShortcutManager instance to be used by the application.
   */
  fun initShortcutManager(shortcutManager: ShortcutManager) {
    this.shortcutManager = shortcutManager
  }

  /**
   * Retrieves the instance of TripsRepository initialized with a user's ID. This repository handles
   * all data operations related to trips.
   *
   * @return The instance of TripsRepository.
   */
  fun getTripsRepository(): TripsRepository {
    return tripsRepository
  }

  /**
   * Adds a dynamic shortcut to the application. This shortcut will be visible in the launcher and
   *
   * @param route The route to navigate to when the shortcut is clicked.
   * @param shortLabel The short label for the shortcut.
   * @param longLabel The long label for the shortcut.
   */
  private fun addDynamicShortcut(route: String, shortLabel: String, longLabel: String) {
    val shortcut =
        ShortcutInfoCompat.Builder(getApplication(), "dynamic_$route")
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setIcon(IconCompat.createWithResource(getApplication(), R.drawable.logo_projet))
            .setIntent(
                Intent(getApplication(), MainActivity::class.java).apply {
                  action = Intent.ACTION_VIEW
                  putExtra("shortcut_id", route)
                })
            .build()
    ShortcutManagerCompat.pushDynamicShortcut(getApplication(), shortcut)
  }

  /**
   * Adds a dynamic shortcut to the application. This shortcut will be visible in the launcher and
   * can be used to create a new trip.
   *
   * @see ShortcutManagerCompat
   */
  fun addDynamicShortcutCreateTrip() {
    addDynamicShortcut(Route.CREATE_TRIP, "Create trip", "Clicking this will create a new trip.")
  }

  /**
   * Adds a dynamic shortcut to the application. This shortcut will be visible in the launcher and
   * can be used to create a new trip.
   *
   * @see ShortcutManagerCompat
   */
  fun addDynamicShortcutJoinTrip() {
    addDynamicShortcut(Route.OVERVIEW, "Join trip", "Clicking this will join a new trip.")
  }

  /** Removes all dynamic shortcuts from the application. */
  fun removeAllDynamicShortcuts() {
    ShortcutManagerCompat.removeAllDynamicShortcuts(getApplication())
  }

  /**
   * Adds a pinned shortcut to the application. This shortcut will be visible in the home screen of
   * the phone and can be used to open a specific trip.
   *
   * @param trip The trip to create a pinned shortcut for.
   */
  @SuppressLint("ObsoleteSdkInt")
  fun addPinnedShortcutTrip(trip: Trip) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return
    }

    if (shortcutManager.isRequestPinShortcutSupported) {
      val shortcut =
          ShortcutInfo.Builder(getApplication(), "pinned_$trip.tripId")
              .setShortLabel(trip.title)
              .setLongLabel("Open ${trip.title}")
              .setIcon(Icon.createWithResource(getApplication(), R.drawable.logo_projet))
              .setIntent(
                  Intent(getApplication(), MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtras(
                        Bundle().apply {
                          putString("shortcut_id", Route.TRIP)
                          putString("trip_id", trip.tripId)
                        })
                  })
              .build()

      val callbackIntent = shortcutManager.createShortcutResultIntent(shortcut)
      val successPendingIntent =
          PendingIntent.getBroadcast(
              getApplication(), 0, callbackIntent, PendingIntent.FLAG_IMMUTABLE)
      shortcutManager.requestPinShortcut(shortcut, successPendingIntent.intentSender)
    }
  }

  /**
   * Handles an intent received by the application. This method is called when the application is
   * launched from a shortcut.
   *
   * @param intent The intent received by the application.
   * @param navigationActions The navigation actions to be performed based on the intent.
   */
  fun handleIntent(intent: Intent?, navigationActions: NavigationActions) {
    Log.d("MainActivity", "Handling intent")
    intent?.let {
      when (intent.getStringExtra("shortcut_id")) {
        Route.CREATE_TRIP -> {
          overviewJoinTripDialogIsOpen = false
          navigationActions.mainNavigation.setStartDestination(Route.CREATE_TRIP)
        }
        Route.OVERVIEW -> {
          overviewJoinTripDialogIsOpen = true
          navigationActions.mainNavigation.setStartDestination(Route.OVERVIEW)
        }
        Route.TRIP -> {
          overviewJoinTripDialogIsOpen = false
          val tripId = intent.getStringExtra("trip_id")
          navigationActions.mainNavigation.setStartDestination(Route.TRIP)
          navigationActions.setVariablesTrip(tripId!!)
        }
        else -> {
          Log.d("MainActivity", "No shortcut id found")
        }
      }
    }
  }

  /**
   * Get a username
   *
   * @param email The email to get the username for.
   */
  fun getUserName(email: String): String {
    val userName = email.substringBefore('@')
    val after = email.substringAfter('@')
    return if (after == "gmail.com") {
      val returnName = "$userName@gml"
      Log.d("MainViewModel", "getUserName: $returnName")
      returnName
    } else {
      val returnName = "$userName@mle${after.hashCode().mod(1000)}"
      Log.d("MainViewModel", "getUserName: $returnName")
      returnName
    }
  }

  /**
   * Set a username
   *
   * @param email The email to set the username for.
   */
  fun setUserName(email: String) {
    runBlocking { tripsRepository.addEmailToUsername(getUserName(email), email) }
  }

  fun setUserToken(token: String) {
    runBlocking { tripsRepository }
  }

  /** OverviewJoinTripDialogIsOpen getter */
  fun getOverviewJoinTripDialogIsOpen(): Boolean {
    return overviewJoinTripDialogIsOpen
  }

  /**
   * Factory for creating instances of the MainViewModel. Ensures the ViewModel is constructed with
   * the necessary application context.
   */
  class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return MainViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
