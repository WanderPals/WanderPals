package com.github.se.wanderpals.service

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.wanderpals.model.data.GeoCords
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesManagerTest {

  private lateinit var saveOfMarkerState: MutableList<GeoCords>

  @Before
  fun saveMarkerState() {
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    SharedPreferencesManager.init(context)
    saveOfMarkerState = SharedPreferencesManager.getAllPlaceData()
  }

  @Test
  fun testSaveMarkerState() {
    val placeData = GeoCords(placeName = "Las Vegas")
    assert(SharedPreferencesManager.getAllPlaceData().isEmpty())
    val savedMarkers = SharedPreferencesManager.savePlaceData(placeData)
    assert(savedMarkers.isNotEmpty())
    assert(savedMarkers.contains(placeData))
  }

  @Test
  fun testGetAllTempMarkers() {
    val placeData = GeoCords(placeName = "Las Vegas")
    assert(SharedPreferencesManager.savePlaceData(placeData).isNotEmpty())
    assert(SharedPreferencesManager.getAllPlaceData().isNotEmpty())
    SharedPreferencesManager.clearAll()
    assert(SharedPreferencesManager.getAllPlaceData().isEmpty())
    assert(SharedPreferencesManager.savePlaceData(placeData).isNotEmpty())
  }

  @After
  fun restoreMarkerState() {
    SharedPreferencesManager.clearAll()
    saveOfMarkerState.forEach { SharedPreferencesManager.savePlaceData(it) }
  }
}
