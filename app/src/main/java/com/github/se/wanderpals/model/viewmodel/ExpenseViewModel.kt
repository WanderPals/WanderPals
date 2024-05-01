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

/**
 * ViewModel for the Expense
 *
 * @param tripsRepository Repository to fetch data from
 * @param tripId Id of the trip
 */
open class ExpenseViewModel(private val tripsRepository: TripsRepository, tripId: String) :
    ViewModel() {

  private val _users = MutableStateFlow(emptyList<User>())
  open val users: StateFlow<List<User>> = _users.asStateFlow()

  init {
    loadMembers(tripId)
    // _users.value =
    //    listOf(User("1", "John Doe"), User("2", "Jane Doe"), User("3", "A1"), User("4", "A2"))
  }

  /** Fetches all members from the trip and updates the state flow accordingly. */
  open fun loadMembers(tripId: String) {
    viewModelScope.launch { _users.value = tripsRepository.getAllUsersFromTrip(tripId) }
  }

  open fun addExpense(tripId: String, expense: Expense) {
    viewModelScope.launch { tripsRepository.addExpenseToTrip(tripId, expense) }
  }

  class ExpenseViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return ExpenseViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
