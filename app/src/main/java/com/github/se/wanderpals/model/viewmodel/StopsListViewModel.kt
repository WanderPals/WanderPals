package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow

open class StopsListViewModel(
    private val tripsRepository: TripsRepository,
    private val tripId: String
) : ViewModel() {
  private val _stops = MutableStateFlow(emptyList<Stop>())
  val stops: MutableStateFlow<List<Stop>> = _stops

  class StopsListViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(StopsListViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return StopsListViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
