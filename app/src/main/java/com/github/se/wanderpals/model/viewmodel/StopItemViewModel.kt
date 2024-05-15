package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StopItemViewModel(private val stopId: String, private val tripsRepository: TripsRepository, private val tripId: String) : ViewModel() {

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted

    fun deleteStop() {
        viewModelScope.launch {
            tripsRepository.removeStopFromTrip(tripId, stopId)
            _isDeleted.value = true
        }
    }

    /** Factory for creating StopViewModel instances. */
    class StopItemViewModelFactory(
        private val tripsRepository: TripsRepository,
        private val tripId: String,
        private val stopId: String
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StopItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StopItemViewModel(stopId, tripsRepository, tripId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}