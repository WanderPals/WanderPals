package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**Finance View model, not doing anything with database for the moment*/
class FinanceViewModel(val tripsRepository: TripsRepository, val tripId: String): ViewModel() {
    val expense1 = Expense(
        expenseId = "exp001",
        title = "Groceries",
        amount = 50.0,
        category = Category.FOOD,
        userId = "user001",
        userName = "Alice",
        participantsIds = listOf("user001", "user002", "user003"),
        names = listOf("Alice", "Bob", "Charlie"),
        localDate = LocalDate.of(2024, 4, 30)
    )

    val expense2 = Expense(
        expenseId = "exp002",
        title = "Movie Night",
        amount = 25.0,
        category = Category.ENTERTAINMENT,
        userId = "user002",
        userName = "Bob",
        participantsIds = listOf("user001", "user002", "user003"),
        names = listOf("Alice", "Bob", "Charlie"),
        localDate = LocalDate.of(2024, 4, 29)
    )

    val expense3 = Expense(
        expenseId = "exp003",
        title = "Dinner",
        amount = 100.0,
        category = Category.FOOD,
        userId = "user003",
        userName = "Charlie",
        participantsIds = listOf("user001", "user002", "user003"),
        names = listOf("Alice", "Bob", "Charlie"),
        localDate = LocalDate.of(2024, 4, 28)
    )

    private val _expenseStateList = MutableStateFlow( listOf(expense1,expense2,expense3))
    open val expenseStateList: StateFlow<List<Expense>> = _expenseStateList

    private val _isLoading = MutableStateFlow(true)
    open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    open fun updateStateLists() {
        viewModelScope.launch {

            _isLoading.value = true
            // Modifiy this
            _expenseStateList.value = listOf(expense1,expense2,expense3)

            _isLoading.value = false
        }
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