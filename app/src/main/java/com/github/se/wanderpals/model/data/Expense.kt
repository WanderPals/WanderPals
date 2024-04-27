package com.github.se.wanderpals.model.data

import java.time.LocalDate

/**
 * Represents an expense record, detailing the financial expenditure made by a user.
 *
 * @property expenseId Unique identifier for the expense.
 * @property title The title or description of the expense.
 * @property amount The monetary value of the expense.
 * @property category The category of the expense (e.g., food, transport).
 * @property userId The unique identifier of the user who payed the expense.
 * @property userName The name of the user who payed the expense.
 * @property participantsIds A list of user IDs who are involved in the expense.
 * @property names A list of names corresponding to the participants' IDs.
 * @property localDate The date when the expense was incurred.
 */
data class Expense(
    val expenseId: String,
    val title: String,
    val amount: Double,
    val category: Category,
    val userId: String,
    val userName: String,
    val participantsIds: List<String>,
    val names: List<String>,
    val localDate: LocalDate
)