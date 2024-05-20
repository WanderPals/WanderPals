package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.firestoreData.FirestoreExpense
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExpenseRepository(
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) {

  private lateinit var firestore: FirebaseFirestore

  private val balancesId = "Balances"

  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  fun init(firestore: FirebaseFirestore) {
    this.firestore = firestore
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
  }

  /**
   * Retrieves the balance map for a specific trip from Firestore. This method queries the Firestore
   * subcollection for balances associated with a given trip document identified by its unique trip
   * ID. The balances are stored under a specific document, which is predefined by `balancesId`.
   *
   * @param tripId The unique identifier of the trip for which balances are to be fetched.
   * @return A map of String to Double representing the balances for the trip. Returns an empty map
   *   if the balances document is not found, if there are no balances in the document, or in case
   *   of an error during data retrieval. Errors during the operation are logged and an empty map is
   *   returned.
   */
  open suspend fun getBalances(tripId: String, source: Source): Map<String, Double> =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_BALANCES_SUBCOLLECTION.path)
                  .document(balancesId)
                  .get(source)
                  .await()
          @Suppress("UNCHECKED_CAST")
          (documentSnapshot.data as? Map<String, Double>) ?: emptyMap()
        } catch (e: Exception) {
          Log.e("TripsRepository", "getBalances: Error getting balance map for trip $tripId", e)
          emptyMap()
        }
      }

  /**
   * Sets or updates the balance map for a specific trip in Firestore. This method updates the
   * Firestore subcollection for balances by setting a map of String to Double associated with a
   * given trip document, identified by its unique trip ID. The balances are stored under a specific
   * document predefined by `balancesId`.
   *
   * @param tripId The unique identifier of the trip for which balances are to be set.
   * @param balancesMap The map of String to Double to be stored as balances for the trip.
   * @return A Boolean value indicating the success or failure of the operation. Returns `true` if
   *   the operation is successful, and `false` if there is an error during the setting operation.
   *   Errors are logged.
   */
  open suspend fun setBalances(tripId: String, balancesMap: Map<String, Double>): Boolean =
      withContext(dispatcher) {
        try {
          val balanceDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_BALANCES_SUBCOLLECTION.path)
                  .document(balancesId)

          if (balancesMap.isEmpty()) {
            balanceDocument.delete().await()
          } else {
            balanceDocument.set(balancesMap).await()
          }
          true
        } catch (e: Exception) {
          Log.e("TripsRepository", "setBalances: Error setting balance map for trip $tripId", e)
          false
        }
      }

  /**
   * Retrieves a specific expense from a trip using the expense's unique identifier. This method
   * queries the Firestore subcollection for trip expenses within a specific trip document.
   *
   * @param tripId The unique identifier of the trip.
   * @param expenseId The unique identifier of the expense to be retrieved.
   * @return An `Expense` object if found, or `null` if the expense is not found or if an error
   *   occurs. The method logs an error and returns `null` in case of failure.
   */
  open suspend fun getExpenseFromTrip(tripId: String, expenseId: String, source: Source): Expense? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
                  .document(expenseId)
                  .get(source)
                  .await()
          val firestoreExpense = documentSnapshot.toObject<FirestoreExpense>()
          if (firestoreExpense != null) {
            firestoreExpense.toExpense()
          } else {
            Log.e(
                "TripsRepository",
                "getExpenseFromTrip: Not found Expense $expenseId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getExpenseFromTrip: Error getting Expense $expenseId from trip $tripId.",
              e)
          null // error
        }
      }

  /**
   * Fetches all expenses associated with a specific trip. This method retrieves an array of
   * expenses by leveraging the `getExpenseFromTrip` method for each expense ID found in the trip
   * document.
   *
   * @param tripId The unique identifier of the trip for which expenses are being retrieved.
   * @return A list of `Expense` objects. Returns an empty list if the trip is not found or if an
   *   error occurs during fetching. Errors are logged and an empty list is returned in these cases.
   */
  open suspend fun getAllExpensesFromTrip(tripId: String, source: Source): List<Expense> =
      withContext(dispatcher) {
        try {
          val trip = tripsRepository.getTrip(tripId)

          if (trip != null) {
            val expenseIds = trip.expenses
            expenseIds.mapNotNull { expenseId -> getExpenseFromTrip(tripId, expenseId, source) }
          } else {
            Log.e("TripsRepository", "getAllExpensesFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e(
              "TripsRepository",
              "getAllExpensesFromTrip: Error fetching Expenses to trip $tripId.",
              e)
          emptyList()
        }
      }

  /**
   * Adds a new expense to a specified trip. This method generates a unique identifier for the new
   * expense, stores it in the Firestore, and updates the trip document to include this new expense
   * ID.
   *
   * @param tripId The unique identifier of the trip to which the expense is being added.
   * @param expense The `Expense` object to be added.
   * @return The unique identifier of the newly added expense if the operation is successful, or an
   *   empty string if it fails. The method logs the outcome of the operation.
   */
  open suspend fun addExpenseToTrip(tripId: String, expense: Expense): String =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString()
          val firebaseExpense =
              FirestoreExpense.fromExpense(
                  expense.copy(expenseId = uniqueID)) // we already know who creates the Expense
          val expenseDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
                  .document(uniqueID)
          expenseDocument.set(firebaseExpense).await()
          Log.d("TripsRepository", "addExpenseToTrip: Expense added successfully to trip $tripId.")

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            // Add the new ExpenseId to the trip's Expense list and update the
            // trip
            val updatedExpensesList = trip.expenses + uniqueID
            val updatedTrip = trip.copy(expenses = updatedExpensesList)
            tripsRepository.updateTrip(updatedTrip)
            Log.d("TripsRepository", "addExpenseToTrip: Expense ID added to trip successfully.")

            uniqueID // this function will return the new object ID if its successful
          } else {

            Log.e("TripsRepository", "addExpenseToTrip: Trip not found with ID $tripId.")

            "" // And will return an empty string in case of failure
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "addExpenseToTrip: Error adding Expense to trip $tripId.", e)
          ""
        }
      }

  /**
   * Removes a specific expense from a trip. This method deletes the expense document from Firestore
   * and updates the trip's expense list to exclude the removed expense ID.
   *
   * @param tripId The unique identifier of the trip.
   * @param expenseId The unique identifier of the expense to be removed.
   * @return `true` if the expense is successfully removed and the trip is updated; `false` if the
   *   trip is not found or if any error occurs during the operation. Errors are logged.
   */
  open suspend fun removeExpenseFromTrip(tripId: String, expenseId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "removeExpenseFromTrip: removing Expense $expenseId from trip $tripId")
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
              .document(expenseId)
              .delete()
              .await()

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            val updatedExpensesList = trip.expenses.filterNot { it == expenseId }
            val updatedTrip = trip.copy(expenses = updatedExpensesList)
            tripsRepository.updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "removeExpenseFromTrip: Expense $expenseId remove and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "removeExpenseFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeExpenseFromTrip: Error removing Expense $expenseId from trip $tripId.",
              e)
          false
        }
      }

  /**
   * Updates an existing expense within a trip. This method replaces the current expense document in
   * Firestore with the updated expense data provided.
   *
   * @param tripId The unique identifier of the trip containing the expense.
   * @param expense The updated `Expense` object.
   * @return `true` if the expense is successfully updated; `false` if the trip is not found or an
   *   error occurs. Errors are logged and the method returns `false` in these cases.
   */
  open suspend fun updateExpenseInTrip(tripId: String, expense: Expense): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "updateExpenseInTrip: Updating a Expense in trip $tripId")
          val firestoreExpense = FirestoreExpense.fromExpense(expense)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
              .document(firestoreExpense.expenseId)
              .set(firestoreExpense)
              .await()
          Log.d(
              "TripsRepository",
              "updateExpenseInTrip: Trip's Expense updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateExpenseInTrip: Error updating Expense with ID ${expense.expenseId} in trip with ID $tripId",
              e)
          false
        }
      }
}
