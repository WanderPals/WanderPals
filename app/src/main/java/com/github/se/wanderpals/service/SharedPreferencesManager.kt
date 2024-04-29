package com.github.se.wanderpals.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.MarkerState
import java.lang.reflect.Type

/** Class to manage the shared preferences. */
object SharedPreferencesManager {
  private const val PREF_NAME_MAP = "PREFERENCE_NAME_MAP"
  private const val LIST_OF_TEMP_MARKERS = "LIST_OF_TEMP_MARKERS"
  private lateinit var sharedPreferencesMap: SharedPreferences

  /** Function to initialize the shared preferences. */
  fun init(context: Context) {
    sharedPreferencesMap = context.getSharedPreferences(PREF_NAME_MAP, Context.MODE_PRIVATE)
  }

  /** Function to clear all the shared preferences. */
  fun clearAll() {
    sharedPreferencesMap.edit().clear().apply()
  }

  /**
   * Save the marker state.
   *
   * @param markerState The marker state to save.
   * @return The list of marker states.
   */
  fun saveMarkerState(markerState: MarkerState): MutableList<MarkerState> {
    val tempMarkers = getAllTempMarkers().toMutableList()
    tempMarkers.add(markerState)
    val json = Gson().toJson(tempMarkers.map { it.position })
    sharedPreferencesMap.edit().putString(LIST_OF_TEMP_MARKERS, json).apply()
    return tempMarkers
  }

  /**
   * Get all the temporary markers.
   *
   * @return The list of marker states.
   */
  fun getAllTempMarkers(): MutableList<MarkerState> {
    var arrayItems: List<LatLng> = emptyList()
    val serializedObject: String? = sharedPreferencesMap.getString(LIST_OF_TEMP_MARKERS, null)
    Log.d("SharedPreferencesManager", "serializedObject: $serializedObject")
    if (serializedObject != null) {
      val type: Type = object : TypeToken<List<LatLng>>() {}.type
      arrayItems = Gson().fromJson(serializedObject, type)
    }
    return arrayItems.map { MarkerState(it) }.toMutableList()
  }
}
