package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.SuggestionRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val suggestionRepository: SuggestionRepository, tripId : String) :
    ViewModel() {

    val suggestions: LiveData<List<Suggestion>> = suggestionRepository.getLocalSuggestions()

    init {
        loadSuggestions(tripId)
    }

    fun loadSuggestions(tripId: String) {
        viewModelScope.launch {
            try {
                suggestionRepository.updateSuggestions(tripId)
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error loading suggestions", e)
            }
        }
    }

    fun addSuggestion(suggestion: Suggestion) {
        suggestionRepository.addSuggestion(suggestion)
    }
}