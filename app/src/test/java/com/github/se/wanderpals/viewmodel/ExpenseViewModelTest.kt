package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.ExpenseViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExpenseViewModelTest {

  private lateinit var viewModel: ExpenseViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()
  private val tripId = "tripId"

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)

    mockTripsRepository = mockk(relaxed = true)
    setupMockResponses()

    val factory = ExpenseViewModel.ExpenseViewModelFactory(mockTripsRepository, "tripId")
    viewModel = factory.create(ExpenseViewModel::class.java)
  }

  private fun setupMockResponses() {
    coEvery { mockTripsRepository.getAllExpensesFromTrip(tripId) } returns
        listOf(
            Expense(
                "", "Lunch", 0.0, Category.FOOD, "", "", emptyList(), emptyList(), LocalDate.now()))
    coEvery { mockTripsRepository.getAllUsersFromTrip(tripId) } returns
        listOf(User(userId = "1", name = "Alice"))
    coEvery { mockTripsRepository.addExpenseToTrip(tripId, any()) } returns "true"
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `loadMembers fetches and updates users correctly`() =
      runBlockingTest(testDispatcher) {
        viewModel.loadMembers(tripId)

        advanceUntilIdle()

        assertEquals(1, viewModel.users.value.size)
        assertEquals("Alice", viewModel.users.value.first().name)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `addExpense adds an expense and updates the repository correctly`() =
      runBlockingTest(testDispatcher) {
        val newExpense =
            Expense(
                "",
                "Dinner",
                30.0,
                Category.FOOD,
                "",
                "",
                emptyList(),
                emptyList(),
                LocalDate.now())
        viewModel.addExpense(tripId, newExpense)

        advanceUntilIdle()

        coVerify { mockTripsRepository.addExpenseToTrip(tripId, newExpense) }
        // Verify if the state list has been updated, additional checks might be necessary if state
        // management is more complex.
      }
}
