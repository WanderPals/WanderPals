package com.github.se.wanderpals.data

import android.net.Uri
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.setPlaceData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GeoCordsTest {

  @Test
  fun testGetPlaceCoordinates() {
    val latitude = 40.6892
    val longitude = 74.0445
    val geoCords = GeoCords(latitude, longitude)
    val latLng = geoCords.getPlaceCoordinates()
    assertEquals(latitude, latLng.latitude, 0.0001)
    assertEquals(longitude, latLng.longitude, 0.0001)
  }

  @Test
  fun testSetPlaceData() {
    // Create a mock Place object
    val mockPlace = mockk<Place>()

    // Define the behavior of the mock Place object
    every { mockPlace.name } returns "Mock Place"
    every { mockPlace.address } returns "123 Mock Street"
    every { mockPlace.rating } returns 4.5
    every { mockPlace.userRatingsTotal } returns 100
    every { mockPlace.phoneNumber } returns "123-456-7890"
    every { mockPlace.websiteUri } returns android.net.Uri.parse("https://www.mockplace.com")
    every { mockPlace.currentOpeningHours?.weekdayText } returns listOf("Monday: 9:00 AM – 5:00 PM")
    every { mockPlace.iconUrl } returns "https://www.mockplace.com/icon.png"
    every { mockPlace.businessStatus } returns Place.BusinessStatus.OPERATIONAL

    // Define the place ID and coordinates
    val placeId = "mockPlaceId"
    val placeCoordinates = LatLng(40.7128, -74.0060)

    // Set the place data using the function
    val geoCords = setPlaceData(mockPlace, placeId, placeCoordinates)

    // Assert that the GeoCords object is correct
    assertEquals(40.7128, geoCords.latitude, 0.0001)
    assertEquals(-74.0060, geoCords.longitude, 0.0001)
    assertEquals("mockPlaceId", geoCords.placeId)
    assertEquals("Mock Place", geoCords.placeName)
    assertEquals("123 Mock Street", geoCords.placeAddress)
    assertEquals("4.5", geoCords.placeRating)
    assertEquals("100", geoCords.placeUserRatingsTotal)
    assertEquals("123-456-7890", geoCords.placePhoneNumber)
    assertEquals("https://www.mockplace.com", geoCords.placeWebsite)
    assertEquals("[Monday: 9:00 AM – 5:00 PM]", geoCords.placeOpeningHours)
    assertEquals("https://www.mockplace.com/icon.png", geoCords.placeIconUrl)
    assertEquals("OPERATIONAL", geoCords.placeBusinessStatus)
  }
}
