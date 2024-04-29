package com.github.se.wanderpals.service

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesManagerTest {

  private lateinit var saveOfMarkerState: MutableList<MarkerState>

  @Before
  fun saveMarkerState() {
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    SharedPreferencesManager.init(context)
    saveOfMarkerState = SharedPreferencesManager.getAllTempMarkers()
  }

  @Test
  fun testSaveMarkerState() {
    val markerState = MarkerState(LatLng(37.7749, -122.4194))
    assert(SharedPreferencesManager.getAllTempMarkers().isEmpty())
    val savedMarkers = SharedPreferencesManager.saveMarkerState(markerState)
    assert(savedMarkers.isNotEmpty())
    assert(savedMarkers.contains(markerState))
  }

  @Test
  fun testGetAllTempMarkers() {
    val markerState = MarkerState(LatLng(37.7749, -122.4194))
    assert(SharedPreferencesManager.saveMarkerState(markerState).isNotEmpty())
    assert(SharedPreferencesManager.getAllTempMarkers().isNotEmpty())
    SharedPreferencesManager.clearAll()
    assert(SharedPreferencesManager.getAllTempMarkers().isEmpty())
    assert(SharedPreferencesManager.saveMarkerState(markerState).isNotEmpty())
  }

  @After
  fun restoreMarkerState() {
    SharedPreferencesManager.clearAll()
    saveOfMarkerState.forEach { SharedPreferencesManager.saveMarkerState(it) }
  }
}
