package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class DashboardViewModel(private val tripsRepository: TripsRepository, tripId: String) :
    ViewModel() {
    // State flow to hold the list of suggestions
    private val _state = MutableStateFlow(emptyList<Suggestion>())
    open val state: StateFlow<List<Suggestion>> = _state

    private val _isLoading = MutableStateFlow(true)
    open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Fetch all trips when the ViewModel is initialized
        loadSuggestion(tripId)
    }

    /** Fetches all trips from the repository and updates the state flow accordingly. */
    open fun loadSuggestion(tripId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Fetch all trips from the repository
            _state.value = tripsRepository.getAllSuggestionsFromTrip(tripId)
            _isLoading.value = false
        }
    }


}