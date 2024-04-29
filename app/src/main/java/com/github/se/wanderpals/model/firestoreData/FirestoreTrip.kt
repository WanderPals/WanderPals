import com.github.se.wanderpals.model.data.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A data transfer object (DTO) for storing and retrieving trip data from Firestore. This class
 * adapts the Trip data model to a format compatible with Firestore, particularly converting
 * LocalDate fields to String for serialization. It supports conversion between the Trip domain
 * model and the FirestoreTrip DTO to facilitate easy data handling with Firestore.
 *
 * @property tripId Unique identifier of the trip, usually generated by the database.
 * @property title Name or title providing a brief overview of the trip.
 * @property startDate Start date of the trip in the format (yyyy-MM-dd).
 * @property endDate End date of the trip in the format (yyyy-MM-dd).
 * @property totalBudget Estimated budget for all trip-related expenses.
 * @property description Detailed information about the trip.
 * @property imageUrl Optional URL of an image representing the trip.
 * @property stops List of IDs for the trip's stops.
 * @property users List of participant user IDs.
 * @param tokenIds Tokens for push notifications.
 * @property suggestions List of IDs for suggested activities or stops.
 * @property announcements List of IDs for trip announcements.
 */
data class FirestoreTrip(
    val tripId: String = "",
    val title: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val totalBudget: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val stops: List<String> = emptyList(),
    val users: List<String> = emptyList(),
    val tokenIds: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val announcements: List<String> = emptyList(),
    val expenses: List<String> = emptyList()
) {
  companion object {
    /**
     * Converts a Trip data model to a FirestoreTrip DTO.
     *
     * @param trip The Trip object to convert.
     * @return A FirestoreTrip DTO with dates converted to String format.
     */
    fun fromTrip(trip: Trip): FirestoreTrip {
      val formatter = DateTimeFormatter.ISO_LOCAL_DATE
      return FirestoreTrip(
          tripId = trip.tripId,
          title = trip.title,
          startDate = trip.startDate.format(formatter),
          endDate = trip.endDate.format(formatter),
          totalBudget = trip.totalBudget,
          description = trip.description,
          imageUrl = trip.imageUrl,
          stops = trip.stops,
          users = trip.users,
          tokenIds = trip.tokenIds,
          suggestions = trip.suggestions,
          announcements = trip.announcements,
          expenses = trip.expenses)
    }
  }

  /**
   * Converts this FirestoreTrip DTO back into a Trip data model.
   *
   * @return A Trip object with LocalDate fields parsed from String.
   */
  fun toTrip(): Trip {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return Trip(
        tripId = tripId,
        title = title,
        startDate = LocalDate.parse(startDate, formatter),
        endDate = LocalDate.parse(endDate, formatter),
        totalBudget = totalBudget,
        description = description,
        imageUrl = imageUrl,
        stops = stops,
        users = users,
        tokenIds = tokenIds,
        suggestions = suggestions,
        announcements = announcements,
        expenses = expenses)
  }
}
