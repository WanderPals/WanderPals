package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/** Finance View model, not doing anything with database for the moment */
open class FinanceViewModel(val tripsRepository: TripsRepository, val tripId: String) :
    ViewModel() {

  private val _users = MutableStateFlow(emptyList<User>())
  open val users: StateFlow<List<User>> = _users.asStateFlow()

  private val _expenseStateList = MutableStateFlow(emptyList<Expense>())
  open val expenseStateList: StateFlow<List<Expense>> = _expenseStateList

  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  /** Fetches all expenses from the trip and updates the state flow accordingly. */
  open fun updateStateLists() {
    viewModelScope.launch {
      _isLoading.value = true

      _expenseStateList.value = tripsRepository.getAllExpensesFromTrip(tripId)

      _isLoading.value = false
    }
  }

  /** Fetches all members from the trip and updates the state flow accordingly. */
  open fun loadMembers(tripId: String) {
    viewModelScope.launch { _users.value = tripsRepository.getAllUsersFromTrip(tripId) }
  }

  /** Adds an expense to the trip. */
  open fun addExpense(tripId: String, expense: Expense) {
    runBlocking {
      tripsRepository.addExpenseToTrip(tripId, expense)
    }
    viewModelScope.launch { updateStateLists() }
  }

  class FinanceViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return FinanceViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
