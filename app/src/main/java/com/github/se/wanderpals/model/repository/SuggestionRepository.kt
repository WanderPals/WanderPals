package com.github.se.wanderpals.model.repository

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SuggestionRepository(tripsRepository: TripsRepository, tripId: String, private val dispatcher: CoroutineDispatcher){
    private val _state = MutableStateFlow(emptyList<Suggestion>())
    open val state: StateFlow<List<Suggestion>> = _state

    private val _tripsRepository = tripsRepository

    private val _suggestions = MutableLiveData<List<Suggestion>>()
    val suggestions: LiveData<List<Suggestion>> = _suggestions

    // To be used with observeAsState
    private val _suggestionState = mutableStateOf<List<Suggestion>>(emptyList())
    val suggestionState: State<List<Suggestion>> = _suggestionState

    // Public method to load suggestions of a trip
    // Function to get local data
    fun getLocalSuggestions(): LiveData<List<Suggestion>> {
        return _suggestions
    }

    // Function to update local data from the database
    suspend fun updateSuggestions(tripId: String) {
        try {
            val suggestionList = _tripsRepository.getAllSuggestionsFromTrip(tripId)

            _suggestions.value = suggestionList
            _suggestionState.value = suggestionList
        } catch (e: Exception) {
            Log.e("SuggestionsViewModel", "Error loading suggestions", e)
        }
    }

    fun addSuggestion(suggestion: Suggestion) {
        _suggestions.value = _suggestions.value?.plus(suggestion)
    }
}