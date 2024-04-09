package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.SuggestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class SuggestionsViewModel(private val suggestionRepository: SuggestionRepository, tripId : String) :
    ViewModel() {
    // State flow to hold the list of suggestions
    private val _state = MutableStateFlow(emptyList<Suggestion>())
    open val state: StateFlow<List<Suggestion>> = _state

    private val _suggestionRepository = suggestionRepository

    // To be used with observeAsState
    private val _suggestionState = mutableStateOf<List<Suggestion>>(emptyList())
    val suggestionState: State<List<Suggestion>> = _suggestionState

    val suggestions: LiveData<List<Suggestion>> = _suggestionRepository.getLocalSuggestions()

    init {
        loadSuggestions(tripId)
    }

    // Public method to load suggestions of a trip
    fun loadSuggestions(tripId: String) {
        viewModelScope.launch {
            try {
                _suggestionRepository.updateSuggestions(tripId)
            } catch (e: Exception) {
                Log.e("SuggestionsViewModel", "Error loading suggestions", e)
            }
        }
    }
}