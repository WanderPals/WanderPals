package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.ui.screens.trip.CalendarDataSource
import com.github.se.wanderpals.ui.screens.trip.CalendarUiState
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AgendaViewModel : ViewModel() {

  private val dataSource by lazy { CalendarDataSource() }

  private val _uiState = MutableStateFlow(CalendarUiState.Init)
  var uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(dates = dataSource.getDates(currentState.yearMonth, LocalDate.now()))
      }
    }
  }

  fun onDateSelected(selectedDate: CalendarUiState.Date) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        // Convert CalendarUiState.Date to LocalDate
        val selectedLocalDate =
            LocalDate.of(
                selectedDate.year.value,
                selectedDate.yearMonth.month,
                selectedDate.dayOfMonth.toInt())

        val newSelectedDate =
            if (currentState.selectedDate == selectedLocalDate) null
            else selectedLocalDate // Toggle selection

        // Mark the selected date and update the list
        val updatedDates =
            currentState.dates.map { date ->
              if (date.dayOfMonth == selectedDate.dayOfMonth) {
                // Toggle the selection status of the date
                selectedDate.copy(isSelected = !selectedDate.isSelected)
              } else {
                // If it's not the selected date, ensure it's not marked as selected
                if (date.isSelected) date.copy(isSelected = false) else date
              }
            }
        currentState.copy(dates = updatedDates, selectedDate = newSelectedDate)
      }
    }
  }

  fun toNextMonth(nextMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(
            yearMonth = nextMonth,
            dates = dataSource.getDates(nextMonth, currentState.selectedDate))
      }
    }
  }

  fun toPreviousMonth(prevMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(
            yearMonth = prevMonth,
            dates = dataSource.getDates(prevMonth, currentState.selectedDate))
      }
    }
  }
}
