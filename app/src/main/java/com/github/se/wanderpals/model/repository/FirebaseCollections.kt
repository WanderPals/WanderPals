package com.github.se.wanderpals.model.repository

enum class FirebaseCollections(val path: String) {
  TRIPS("Trips"),
  USERS_TO_TRIPS_IDS("UsersToTripIds"),
  STOPS_SUBCOLLECTION("Stops"),
  USERS_SUBCOLLECTION("Users"),
  SUGGESTIONS_SUBCOLLECTION("Suggestions"),
  ANNOUNCEMENTS_SUBCOLLECTION("Announcements"),
  TRIP_NOTIFICATIONS_SUBCOLLECTION("Notifications"),
  TRIP_BALANCES_SUBCOLLECTION("Balances"),
  TRIP_EXPENSES_SUBCOLLECTION("Expenses"),
  USERNAME_TO_EMAIL_COLLECTION("UsernameToEmail")
}
