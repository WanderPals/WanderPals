package com.github.se.wanderpals.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.Dispatchers

/**
 * ViewModel to manage data for the main user interface components. It handles the initialization
 * and provision of the TripsRepository.
 *
 * @param application The context of the application passed to AndroidViewModel for use with
 *   application-wide tasks.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

  private lateinit var tripsRepository: TripsRepository

  /**
   * Initializes the TripsRepository with a specific user ID. This method must be called immediately
   * after a user successfully signs in.
   *
   * @param userId The unique identifier for the user, used to initialize the repository.
   */
  fun initRepository(userId: String) {
    tripsRepository = TripsRepository(userId, Dispatchers.IO)
    tripsRepository.initFirestore()
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
