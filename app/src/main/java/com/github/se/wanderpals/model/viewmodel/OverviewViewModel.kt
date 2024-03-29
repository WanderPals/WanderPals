package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class OverviewViewModel(val tripsRepository : TripsRepository) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Trip>())
    val state : StateFlow<List<Trip>> = _state

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    init{
        getAllTrips()
    }
    fun getAllTrips(){
        viewModelScope.launch {
            _isLoading.value = true
            _state.value = tripsRepository.getAllTrips()
            _isLoading.value = false
        }
    }


}