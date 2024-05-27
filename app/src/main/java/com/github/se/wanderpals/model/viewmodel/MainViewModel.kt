package com.github.se.wanderpals.model.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.ShortcutManager
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.wanderpals.MainActivity
import com.github.se.wanderpals.R
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

  /** Initializes the ShortcutManager for the application. This method must be called immediately */
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
   * can be used to create a new trip.
   *
   * @see ShortcutManagerCompat
   */
  fun addDynamicShortcutCreateTrip() {
    val shortcut =
        ShortcutInfoCompat.Builder(getApplication(), "dynamic_" + Route.CREATE_TRIP)
            .setShortLabel("Create trip")
            .setLongLabel("Clicking this will create a new trip.")
            .setIcon(IconCompat.createWithResource(getApplication(), R.drawable.logo_projet))
            .setIntent(
                Intent(getApplication(), MainActivity::class.java).apply {
                  action = Intent.ACTION_VIEW
                  putExtra("shortcut_id", Route.CREATE_TRIP)
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
  fun addDynamicShortcutJoinTrip() {
    val shortcut =
        ShortcutInfoCompat.Builder(getApplication(), "dynamic_" + Route.OVERVIEW)
            .setShortLabel("Join trip")
            .setLongLabel("Clicking this will join a new trip.")
            .setIcon(IconCompat.createWithResource(getApplication(), R.drawable.logo_projet))
            .setIntent(
                Intent(getApplication(), MainActivity::class.java).apply {
                  action = Intent.ACTION_VIEW
                  putExtra("shortcut_id", Route.OVERVIEW)
                })
            .build()
    ShortcutManagerCompat.pushDynamicShortcut(getApplication(), shortcut)
  }

  /** Removes all dynamic shortcuts from the application. */
  fun removeAllDynamicShortcuts() {
    ShortcutManagerCompat.removeAllDynamicShortcuts(getApplication())
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
          navigationActions.mainNavigation.setStartDestination(Route.CREATE_TRIP)
        }
        Route.OVERVIEW -> {
          overviewJoinTripDialogIsOpen = true
          navigationActions.mainNavigation.setStartDestination(Route.OVERVIEW)
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
