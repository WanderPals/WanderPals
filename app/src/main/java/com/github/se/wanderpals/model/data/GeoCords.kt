package com.github.se.wanderpals.model.data

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

/**
 * Represents geographical coordinates with a latitude and a longitude. Utilized to pinpoint
 * locations for trips, stops, or suggestions within the application.
 *
 * @param latitude The north-south position, with values ranging from -90.0 to 90.0.
 * @param longitude The east-west position, with values ranging from -180.0 to 180.0.
 * @param placeId Unique identifier for the place.
 * @param placeName Name of the place.
 * @param placeAddress Address of the place.
 * @param placeRating Rating of the place.
 * @param placeUserRatingsTotal Total number of user ratings for the place.
 * @param placePhoneNumber Phone number of the place.
 * @param placeWebsite Website of the place.
 * @param placeOpeningHours Opening hours of the place.
 * @param placeIconUrl URL for the icon of the place.
 * @param placeBusinessStatus Business status of the place.
 */
data class GeoCords(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val placeId: String = "",
    val placeName: String = "",
    val placeAddress: String = "",
    val placeRating: String = "",
    val placeUserRatingsTotal: String = "",
    val placePhoneNumber: String = "",
    val placeWebsite: String = "",
    val placeOpeningHours: String = "",
    val placeIconUrl: String = "",
    val placeBusinessStatus: String = ""
) {

  /**
   * Function to get the place coordinates.
   *
   * @return The place coordinates.
   */
  fun getPlaceCoordinates(): LatLng {
    return LatLng(latitude, longitude)
  }
}

/**
 * Function to set the place data.
 *
 * @param place The place object.
 * @param placeId The place ID.
 * @param placeCoordinates The place coordinates.
 */
fun setPlaceData(place: Place, placeId: String, placeCoordinates: LatLng): GeoCords {
  return GeoCords(
      placeCoordinates.latitude,
      placeCoordinates.longitude,
      placeId,
      place.name?.toString() ?: "",
      place.address?.toString() ?: "",
      place.rating?.toString() ?: "",
      place.userRatingsTotal?.toString() ?: "",
      place.phoneNumber?.toString() ?: "",
      place.websiteUri?.toString() ?: "",
      place.currentOpeningHours?.weekdayText?.toString() ?: "",
      place.iconUrl?.toString() ?: "",
      place.businessStatus?.toString() ?: "")
}
