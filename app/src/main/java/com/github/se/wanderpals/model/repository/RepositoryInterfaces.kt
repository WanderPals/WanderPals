package com.github.se.wanderpals.model.repository

import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.data.User
import com.google.firebase.firestore.Source

interface IUserRepository {
  suspend fun getUserEmail(username: String, source: Source): String?

  suspend fun addEmailToUsername(username: String, email: String, source: Source): Boolean

  suspend fun deleteEmailByUsername(username: String, source: Source): Boolean

  suspend fun getUserFromTrip(tripId: String, userId: String, source: Source): User?

  suspend fun getAllUsersFromTrip(tripId: String, source: Source): List<User>

  suspend fun addUserToTrip(tripId: String, user: User): Boolean

  suspend fun updateUserInTrip(tripId: String, user: User): Boolean

  suspend fun removeUserFromTrip(tripId: String, userId: String): Boolean
}

interface IExpenseRepository {
  suspend fun getBalances(tripId: String, source: Source): Map<String, Double>

  suspend fun setBalances(tripId: String, balancesMap: Map<String, Double>): Boolean

  suspend fun getExpenseFromTrip(tripId: String, expenseId: String, source: Source): Expense?

  suspend fun getAllExpensesFromTrip(tripId: String, source: Source): List<Expense>

  suspend fun addExpenseToTrip(tripId: String, expense: Expense): String

  suspend fun removeExpenseFromTrip(tripId: String, expenseId: String): Boolean

  suspend fun updateExpenseInTrip(tripId: String, expense: Expense): Boolean
}

interface INotificationRepository {
  suspend fun getNotificationList(tripId: String, source: Source): List<TripNotification>

  suspend fun setNotificationList(
      tripId: String,
      notifications: List<TripNotification>,
      source: Source
  ): Boolean
}

interface IStopRepository {
  suspend fun getStopFromTrip(tripId: String, stopId: String, source: Source): Stop?

  suspend fun getAllStopsFromTrip(tripId: String, source: Source): List<Stop>

  suspend fun addStopToTrip(tripId: String, stop: Stop): Boolean

  suspend fun removeStopFromTrip(tripId: String, stopId: String): Boolean

  suspend fun updateStopInTrip(tripId: String, stop: Stop): Boolean
}

interface ISuggestionRepository {
  suspend fun getSuggestionFromTrip(
      tripId: String,
      suggestionId: String,
      source: Source
  ): Suggestion?

  suspend fun getAllSuggestionsFromTrip(tripId: String, source: Source): List<Suggestion>

  suspend fun addSuggestionToTrip(tripId: String, suggestion: Suggestion): Boolean

  suspend fun removeSuggestionFromTrip(tripId: String, suggestionId: String): Boolean

  suspend fun updateSuggestionInTrip(tripId: String, suggestion: Suggestion): Boolean
}

interface ITripIDsRepository {
  suspend fun getTripsIds(source: Source): List<String>

  suspend fun addTripId(tripId: String, isOwner: Boolean, source: Source): Boolean

  suspend fun removeTripId(tripId: String, userId: String): Boolean
}

interface ITripRepository {
  suspend fun getTrip(tripId: String, source: Source): Trip?

  suspend fun getAllTrips(source: Source): List<Trip>

  suspend fun addTrip(trip: Trip): Boolean

  suspend fun updateTrip(trip: Trip): Boolean

  suspend fun deleteTrip(tripId: String): Boolean
}

interface IAnnouncementRepository {
  suspend fun getAnnouncementFromTrip(
      tripId: String,
      announcementId: String,
      source: Source
  ): Announcement?

  suspend fun getAllAnnouncementsFromTrip(tripId: String, source: Source): List<Announcement>

  suspend fun addAnnouncementToTrip(tripId: String, announcement: Announcement): Boolean

  suspend fun removeAnnouncementFromTrip(tripId: String, announcementId: String): Boolean

  suspend fun updateAnnouncementInTrip(tripId: String, announcement: Announcement): Boolean
}
