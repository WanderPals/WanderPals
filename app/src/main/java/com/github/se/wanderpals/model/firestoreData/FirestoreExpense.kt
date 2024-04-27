package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import java.time.LocalDate

/**
 * Firestore-compatible DTO for an Expense, ensuring all expense details are correctly handled for
 * interactions with Firestore. Includes conversions from and to the Expense model.
 *
 * @property expenseId Unique identifier for the expense.
 * @property title The title or description of the expense.
 * @property amount The monetary value of the expense.
 * @property category The category of the expense (e.g., food, transport).
 * @property userId The unique identifier of the user who paid the expense.
 * @property userName The name of the user who paid the expense.
 * @property participantsIds A list of user IDs who are involved in the expense.
 * @property names A list of names corresponding to the participants' IDs.
 * @property localDate The date when the expense was incurred, stored as a String.
 */
data class FirestoreExpense(
    val expenseId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val userId: String = "",
    val userName: String = "",
    val participantsIds: List<String> = emptyList(),
    val names: List<String> = emptyList(),
    val localDate: String = "" // LocalDate converted to String for Firestore compatibility
) {
  companion object {
    /**
     * Converts an Expense model to a FirestoreExpense DTO.
     *
     * @param expense The Expense object to convert.
     * @return A Firestore-compatible FirestoreExpense DTO.
     */
    fun fromExpense(expense: Expense): FirestoreExpense {
      return FirestoreExpense(
          expenseId = expense.expenseId,
          title = expense.title,
          amount = expense.amount,
          category = expense.category.name, // Convert enum to String
          userId = expense.userId,
          userName = expense.userName,
          participantsIds = expense.participantsIds,
          names = expense.names,
          localDate = expense.localDate.toString() // Convert LocalDate to String
          )
    }
  }

  /**
   * Converts this FirestoreExpense DTO back to an Expense model.
   *
   * @return An Expense object with the date and category converted back from String.
   */
  fun toExpense(): Expense {
    return Expense(
        expenseId = this.expenseId,
        title = this.title,
        amount = this.amount,
        category = Category.valueOf(this.category), // Convert String back to enum
        userId = this.userId,
        userName = this.userName,
        participantsIds = this.participantsIds,
        names = this.names,
        localDate = LocalDate.parse(this.localDate) // Convert String back to LocalDate
        )
  }
}
