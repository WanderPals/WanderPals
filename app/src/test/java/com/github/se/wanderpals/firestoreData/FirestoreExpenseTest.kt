package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import java.time.LocalDate
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FirestoreExpenseTest {

  @Test
  fun testFromExpenseToFirestoreExpense() {
    val expense =
        Expense(
            expenseId = "exp123",
            title = "Lunch Meeting",
            amount = 45.50,
            category = Category.FOOD,
            userId = "user123",
            userName = "Alice Smith",
            participantsIds = listOf("user124", "user125"),
            names = listOf("Bob Johnson", "Carol White"),
            localDate = LocalDate.of(2024, 1, 15))

    val firestoreExpense = FirestoreExpense.fromExpense(expense)

    assertEquals(expense.expenseId, firestoreExpense.expenseId)
    assertEquals(expense.title, firestoreExpense.title)
    assertEquals(expense.amount, firestoreExpense.amount)
    assertEquals(expense.category.name, firestoreExpense.category)
    assertEquals(expense.userId, firestoreExpense.userId)
    assertEquals(expense.userName, firestoreExpense.userName)
    assertEquals(expense.participantsIds, firestoreExpense.participantsIds)
    assertEquals(expense.names, firestoreExpense.names)
    assertEquals(expense.localDate.toString(), firestoreExpense.localDate)
  }

  @Test
  fun testFromFirestoreExpenseToExpense() {
    val firestoreExpense =
        FirestoreExpense(
            expenseId = "exp123",
            title = "Lunch Meeting",
            amount = 45.50,
            category = "FOOD",
            userId = "user123",
            userName = "Alice Smith",
            participantsIds = listOf("user124", "user125"),
            names = listOf("Bob Johnson", "Carol White"),
            localDate = "2024-01-15")

    val expense = firestoreExpense.toExpense()

    assertEquals(firestoreExpense.expenseId, expense.expenseId)
    assertEquals(firestoreExpense.title, expense.title)
    assertEquals(firestoreExpense.amount, expense.amount)
    assertEquals(Category.valueOf(firestoreExpense.category), expense.category)
    assertEquals(firestoreExpense.userId, expense.userId)
    assertEquals(firestoreExpense.userName, expense.userName)
    assertEquals(firestoreExpense.participantsIds, expense.participantsIds)
    assertEquals(firestoreExpense.names, expense.names)
    assertEquals(LocalDate.parse(firestoreExpense.localDate), expense.localDate)
  }
}
