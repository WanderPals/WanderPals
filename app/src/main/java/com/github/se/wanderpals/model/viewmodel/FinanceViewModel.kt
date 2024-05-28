package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.NotificationsManager
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException
import java.time.LocalDate
import java.util.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import org.json.JSONObject

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

  private val _settleDebtData = MutableStateFlow(Pair(User(), 0.0))
  open val settleDebtData = _settleDebtData.asStateFlow()

  private val _showSettleDebtDialog = MutableStateFlow(false)
  open val showSettleDebtDialog = _showSettleDebtDialog.asStateFlow()

  // signal that the Expense was added successfully
  private val _createExpenseFinished = MutableStateFlow(false)
  open val createExpenseFinished: StateFlow<Boolean> = _createExpenseFinished.asStateFlow()

  // don't add an Expense twice
  private val _isAddingExpense = MutableStateFlow(false)
  open val isAddingExpense: StateFlow<Boolean> = _isAddingExpense.asStateFlow()

  val exchangeRate = MutableStateFlow<Double?>(null)

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

    viewModelScope.launch {
      if (!isAddingExpense.value && !createExpenseFinished.value) {
        _isAddingExpense.value = true
        tripsRepository.addExpenseToTrip(tripId, expense)
        val expenseList = tripsRepository.getAllExpensesFromTrip(tripId)
        if (expenseList.isEmpty()) {
          // error
          Log.e("FinanceViewModel", "Error reading expense after addition (should not happen)")
        } else {
          val newExpense = expenseList.last()
          NotificationsManager.addExpenseNotification(tripId, newExpense)
        }
        updateStateLists()
        _isAddingExpense.value = false
        _createExpenseFinished.value = true
      }
    }
  }

  /** Resets the CreateExpenseFinished flag */
  fun setCreateExpenseFinished(value: Boolean) {
    _createExpenseFinished.value = value
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
   * This method retrieves the current trip from the repository, updates the exchange rate, converts
   * all expenses to the new currency, updates the trip with the new currency code, and refreshes
   * the related state lists.
   *
   * @param newCurrencyCode The new currency code to be used for the trip.
   */
  open fun updateCurrency(newCurrencyCode: String) {
    viewModelScope.launch {
      val currentTrip = tripsRepository.getTrip(tripId)!!
      setExchangeRate(currentTrip.currencyCode, newCurrencyCode)
      if (exchangeRate.value != null) {
        val expenses = tripsRepository.getAllExpensesFromTrip(tripId)
        expenses.forEach {
          tripsRepository.updateExpenseInTrip(
              tripId, it.copy(amount = it.amount * exchangeRate.value!!))
        }
        tripsRepository.updateTrip(currentTrip.copy(currencyCode = newCurrencyCode))
        updateStateLists()
      }
    }
  }

  /**
   * Set the exchange rate between two currencies in the finance view model.
   *
   * This method fetches the exchange rate from a remote API by performing an http request and
   * updates the _exchangeRate state variable with the retrieved value.
   *
   * @param fromCurrency The original currency code.
   * @param toCurrency The target currency code.
   */
  private suspend fun setExchangeRate(fromCurrency: String, toCurrency: String) {
    withContext(Dispatchers.IO) {
      try {
        val client = OkHttpClient()

        val url = buildExchangeURL(fromCurrency, toCurrency)

        val request = Request.Builder().url(url).build()

        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
          val responseBody = response.body()?.string()
          val exchangeRateResponse =
              responseBody?.let {
                JSONObject(it)
                    .getJSONArray("response")
                    .getJSONObject(0)
                    .getString("average_bid")
                    .toDouble()
              }
          exchangeRate.value = exchangeRateResponse
          Log.d("UpdateExchangeRate", "Exchange rate updated successfully: $exchangeRate")
        } else {
          exchangeRate.value = null
          Log.e("UpdateExchangeRate", "Unsuccessful response. HTTP code: ${response.code()}")
        }
      } catch (e: IOException) {
        exchangeRate.value = null
        Log.e("UpdateExchangeRate", "Failed to update exchange rate. Exception: ${e.message}")
      }
    }
  }

  /**
   * Builds the URL for fetching the exchange rate between two currencies.
   *
   * This method constructs the URL for the exchange rate API based on the provided currency codes
   * and the current date.
   *
   * @param fromCurrency The original currency code.
   * @param toCurrency The target currency code.
   * @return The URL for the exchange rate API.
   */
  private fun buildExchangeURL(fromCurrency: String, toCurrency: String): String {

    val currentDate = LocalDate.now()
    val startDate = currentDate.minusDays(1).toString() // Yesterday's date
    val endDate = currentDate.toString() // Today's date

    return HttpUrl.Builder()
        .scheme("https")
        .host("fxds-public-exchange-rates-api.oanda.com")
        .addPathSegment("cc-api")
        .addPathSegment("currencies")
        .addQueryParameter("base", fromCurrency)
        .addQueryParameter("quote", toCurrency)
        .addQueryParameter("data_type", "general_currency_pair")
        .addQueryParameter("start_date", startDate)
        .addQueryParameter("end_date", endDate)
        .build()
        .toString()
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

  /** Sets the state of the dialog for settling a debt. */
  open fun setShowSettleDebtDialogState(value: Boolean) {
    _showSettleDebtDialog.value = value
  }

  /** Sets the data for settling a debt. */
  open fun setSettleDebt(receiver: User, amount: Double) {
    _settleDebtData.value = Pair(receiver, amount)
  }

  /**
   * Settles a debt between two users.
   *
   * This method creates a new expense to settle a debt between two users. The expense is added to
   * the trip and the dialog for settling a debt is closed.
   *
   * @param userId The ID of the user to whom the debt is settled.
   * @param userName The name of the user to whom the debt is settled.
   */
  open fun settleDebt(userId: String, userName: String) {
    _isLoading.value = true
    val (sender, amount) = _settleDebtData.value
    val expense =
        Expense(
            expenseId = "",
            title = "Debt settlement : ${sender.name} -> " + userName,
            amount = amount,
            category = Category.OTHER,
            userId = sender.userId,
            userName = sender.name,
            participantsIds = listOf(userId),
            names = listOf(userName),
            localDate = LocalDate.now())
    addExpense(tripId, expense)
    setShowSettleDebtDialogState(false)
    _isLoading.value = false
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
