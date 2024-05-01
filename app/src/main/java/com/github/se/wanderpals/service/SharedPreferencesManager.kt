package com.github.se.wanderpals.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.github.se.wanderpals.ui.screens.trip.map.PlaceData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
   * Save the place data as a json string in the shared preferences.
   *
   * @param placeData The place data to save.
   * @return The list of place data with the new place data added
   */
  fun savePlaceData(placeData: PlaceData): MutableList<PlaceData> {
    val listPlaceData = getAllPlaceData().toMutableList()
    listPlaceData.add(placeData)
    val json = Gson().toJson(listPlaceData)
    sharedPreferencesMap.edit().putString(LIST_OF_TEMP_MARKERS, json).apply()
    return listPlaceData
  }

  /**
   * Delete the place data from the shared preferences.
   *
   * @param placeData The place data to delete.
   * @return The list of place data
   */
  fun deletePlaceData(placeData: PlaceData): MutableList<PlaceData> {
    val listPlaceData = getAllPlaceData().toMutableList()
    listPlaceData.remove(placeData)
    val json = Gson().toJson(listPlaceData)
    sharedPreferencesMap.edit().putString(LIST_OF_TEMP_MARKERS, json).apply()
    return listPlaceData
  }

  /**
   * Get all the place data from the shared preferences.
   *
   * @return The list of place data
   */
  fun getAllPlaceData(): MutableList<PlaceData> {
    var arrayItems: List<PlaceData> = emptyList()
    val serializedObject: String? = sharedPreferencesMap.getString(LIST_OF_TEMP_MARKERS, null)
    Log.d("SharedPreferencesManager", "serializedObject: $serializedObject")
    if (serializedObject != null) {
      val type: Type = object : TypeToken<List<PlaceData>>() {}.type
      arrayItems = Gson().fromJson(serializedObject, type)
    }
    return arrayItems.toMutableList()
  }
}
