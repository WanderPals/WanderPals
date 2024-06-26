package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FinanceViewModelTest {

  private lateinit var viewModel: FinanceViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()
  private val tripId = "tripId"

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)

    mockTripsRepository = mockk(relaxed = true)
    NotificationsManager.initNotificationsManager(mockTripsRepository)
    SessionManager.setUserSession()
    navigationActions = mockk(relaxed = true)

    every { navigationActions.goBack() } just Runs
    setupMockResponses()

    viewModel = FinanceViewModel(mockTripsRepository, tripId)
  }

  private fun setupMockResponses() {
    coEvery { mockTripsRepository.getAllExpensesFromTrip(tripId) } returns
        listOf(
            Expense(
                "",
                "Lunch",
                10.0,
                Category.FOOD,
                "",
                "",
                emptyList(),
                emptyList(),
                LocalDate.now()))
    coEvery { mockTripsRepository.getAllUsersFromTrip(tripId) } returns
        listOf(User(userId = "1", name = "Alice"))
    coEvery { mockTripsRepository.addExpenseToTrip(tripId, any()) } returns "true"
    coEvery { mockTripsRepository.getTrip(any()) } returns
        Trip("-1", "", LocalDate.now(), LocalDate.now(), 0.0, "")
    coEvery { mockTripsRepository.getAllExpensesFromTrip(any()) } returns
        listOf(
            Expense(
                "",
                "Lunch",
                10.0,
                Category.FOOD,
                "",
                "",
                emptyList(),
                emptyList(),
                LocalDate.now()))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateStateLists fetches and updates expenses correctly`() =
      runBlockingTest(testDispatcher) {
        viewModel.updateStateLists()

        advanceUntilIdle()

        assert(viewModel.expenseStateList.value.isNotEmpty())
        assertEquals("Lunch", viewModel.expenseStateList.value.first().title)
        coVerify { mockTripsRepository.getAllExpensesFromTrip(tripId) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `loadMembers fetches and updates users correctly`() =
      runBlockingTest(testDispatcher) {
        viewModel.loadMembers(tripId)

        advanceUntilIdle()

        assert(viewModel.users.value.isNotEmpty())
        assertEquals("Alice", viewModel.users.value.first().name)
        coVerify { mockTripsRepository.getAllUsersFromTrip(tripId) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `addExpense adds an expense and updates state list`() =
      runBlockingTest(testDispatcher) {
        val expense =
            Expense(
                "", "Lunch", 10.0, Category.FOOD, "", "", emptyList(), emptyList(), LocalDate.now())
        viewModel.addExpense(tripId, expense)

        advanceUntilIdle()

        coVerify { mockTripsRepository.addExpenseToTrip(tripId, expense) }
        assert(viewModel.expenseStateList.value.contains(expense))
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateCurrency updates trip currency and expenses correctly`() =
      runBlockingTest(testDispatcher) {
        // Arrange
        val newCurrency = "EUR"
        val exchangeRate = 0.85

        // Mocking the _exchangeRate LiveData
        val exchangeRateLiveData = MutableStateFlow<Double?>(exchangeRate)
        viewModel.exchangeRate.value = exchangeRateLiveData.value

        val viewModelSpy = spyk(viewModel, recordPrivateCalls = true)
        coEvery { viewModelSpy["setExchangeRate"]("CHF", newCurrency) } coAnswers {}

        // Act
        viewModelSpy.updateCurrency(newCurrency)

        advanceUntilIdle()

        coVerify { mockTripsRepository.getTrip(tripId) }
        coVerify { mockTripsRepository.getAllExpensesFromTrip(tripId) }

        coVerify {
          mockTripsRepository.updateExpenseInTrip(
              tripId, match { it.amount == 10.0 * exchangeRate })
        }
        coVerify { (mockTripsRepository.updateTrip(any())) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `addExpense is not called twice when invoked multiple times rapidly`() =
      runBlockingTest(testDispatcher) {
        // Arrange
        val expense =
            Expense(
                "", "Lunch", 10.0, Category.FOOD, "", "", emptyList(), emptyList(), LocalDate.now())

        // Mock the addExpenseToTrip method to simulate a delay
        coEvery { mockTripsRepository.addExpenseToTrip(tripId, any()) } coAnswers
            {
              delay(500)
              "true"
            }

        // Act
        launch { viewModel.addExpense(tripId, expense) }
        launch { viewModel.addExpense(tripId, expense) }

        // Advance the dispatcher to let coroutines finish
        advanceUntilIdle()

        // Assert
        // Verify that addExpenseToTrip is called only once
        coVerify(exactly = 1) { mockTripsRepository.addExpenseToTrip(tripId, expense) }
      }
}
