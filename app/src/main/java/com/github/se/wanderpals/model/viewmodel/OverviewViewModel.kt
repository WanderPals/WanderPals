package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class OverviewViewModel(val tripsRepository : TripsRepository) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Trip>())
    val state : StateFlow<List<Trip>> = _state
    init{
        getAllTrips()
    }
    fun getAllTrips(){
        viewModelScope.launch {
            _state.value = tripsRepository.getAllTrips() }
    }
    fun filterTripsByTitle(title: String): List<Trip> {
        if (title.isEmpty()) {
            state.value
        }

        val titleLowerCase = title.lowercase()
        return state.value.filter { trip -> trip.title.lowercase().contains(titleLowerCase) }
    }

}