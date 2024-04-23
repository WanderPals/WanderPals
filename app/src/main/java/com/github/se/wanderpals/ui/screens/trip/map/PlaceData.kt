package com.github.se.wanderpals.ui.screens.trip.map

import com.google.android.libraries.places.api.model.Place

data class PlaceData(
    var placeName: String = "",
    var placeAddress: String = "",
    var placeRating: String = "",
    var placeUserRatingsTotal: String = "",
    var placePhoneNumber: String = "",
    var placeWebsite: String = "",
    var placeOpeningHours: String = "",
    var placeIconUrl: String = "",
    var placeBusinessStatus: String = ""
) {
  fun setPlaceData(place: Place) {
    place.name?.let { it1 -> placeName = it1 }
    place.iconUrl?.let { it1 -> placeIconUrl = it1 }
    place.businessStatus?.let { it1 -> placeBusinessStatus = it1.toString() }
    place.phoneNumber?.let { it1 -> placePhoneNumber = it1 }
    place.address?.let { it1 -> placeAddress = it1 }
    place.currentOpeningHours?.weekdayText.let { it1 -> placeOpeningHours = it1.toString() }
    place.rating?.let { it1 -> placeRating = it1.toString() }
    place.userRatingsTotal?.let { it1 -> placeUserRatingsTotal = it1.toString() }
    place.websiteUri?.let { it1 -> placeWebsite = it1.toString() }
  }
}
