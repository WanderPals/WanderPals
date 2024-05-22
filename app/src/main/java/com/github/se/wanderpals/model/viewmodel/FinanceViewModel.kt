package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.NotificationsManager
import java.util.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel for managing the financial data of a trip.
 *
 * This ViewModel handles the state and logic for expenses, users, and currency settings related to
 * a trip. It interacts with the TripsRepository to fetch and update trip data.
 *
 * @param tripsRepository The repository to access trip data.
 * @param tripId The ID of the trip this ViewModel is associated with.
 */
open class FinanceViewModel(val tripsRepository: TripsRepository, val tripId: String) :
    ViewModel() {

  private val _users = MutableStateFlow(emptyList<User>())
  open val users: StateFlow<List<User>> = _users.asStateFlow()

  private val _expenseStateList = MutableStateFlow(emptyList<Expense>())
  open val expenseStateList: StateFlow<List<Expense>> = _expenseStateList

  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _showDeleteDialog = MutableStateFlow(false)
  open val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

  private val _selectedExpense = MutableStateFlow<Expense?>(null)
  open val selectedExpense: StateFlow<Expense?> = _selectedExpense.asStateFlow()

  private val _showCurrencyDialog = MutableStateFlow(false)
  val showCurrencyDialog = _showCurrencyDialog.asStateFlow()

  private val _tripCurrency = MutableStateFlow<Currency>(Currency.getInstance("CHF"))
  open val tripCurrency = _tripCurrency.asStateFlow()

  /** Fetches all expenses from the trip and updates the state flow accordingly. */
  open fun updateStateLists() {
    viewModelScope.launch {
      _isLoading.value = true
      val currencyCode = tripsRepository.getTrip(tripId)!!.currencyCode
      _tripCurrency.value = Currency.getInstance(currencyCode)
      _expenseStateList.value = tripsRepository.getAllExpensesFromTrip(tripId)

      _isLoading.value = false
    }
  }

  /** Fetches all members from the trip and updates the state flow accordingly. */
  open fun loadMembers(tripId: String) {
    viewModelScope.launch { _users.value = tripsRepository.getAllUsersFromTrip(tripId) }
  }

  /**
   * Adds an expense to a specified trip.
   *
   * @param tripId The ID of the trip to which to add the expense.
   * @param expense The Expense object to add.
   */
  open fun addExpense(tripId: String, expense: Expense) {
    runBlocking { tripsRepository.addExpenseToTrip(tripId, expense) }
    viewModelScope.launch {
      val newExpense = tripsRepository.getAllExpensesFromTrip(tripId).last()
      NotificationsManager.addExpenseNotification(tripId, newExpense)
      updateStateLists()
    }
  }

  /**
   * Deletes a specific expense from a trip.
   *
   * @param expense The Expense object to delete.
   */
  open fun deleteExpense(expense: Expense) {
    runBlocking { tripsRepository.removeExpenseFromTrip(tripId, expense.expenseId) }
    viewModelScope.launch {
      NotificationsManager.removeExpensePath(tripId, expense.expenseId)
      updateStateLists()
    }
    setShowDeleteDialogState(false)
  }

  /**
   * Updates the currency code of the current trip.
   *
   * This method retrieves the current trip from the repository, updates it with the new currency
   * code, and refreshes the related state lists.
   *
   * @param currencyCode The new currency code to be used for the trip.
   */
  open fun updateCurrency(currencyCode: String) {
    viewModelScope.launch {
      val currentTrip = tripsRepository.getTrip(tripId)!!
      tripsRepository.updateTrip(currentTrip.copy(currencyCode = currencyCode))
      updateStateLists()
    }
  }

  /** Setter functions */
  open fun setShowDeleteDialogState(value: Boolean) {
    _showDeleteDialog.value = value
  }

  open fun setSelectedExpense(expense: Expense) {
    _selectedExpense.value = expense
  }

  open fun setShowCurrencyDialogState(value: Boolean) {
    _showCurrencyDialog.value = value
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
