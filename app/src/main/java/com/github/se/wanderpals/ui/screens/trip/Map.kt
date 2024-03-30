package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/** The Map screen. */
@Composable
fun Map() {
  var uiSettings by remember { mutableStateOf(MapUiSettings()) }
  val properties by remember { mutableStateOf(MapProperties(mapType = MapType.SATELLITE)) }

  Box(Modifier.fillMaxSize()) {
    GoogleMap(
      modifier = Modifier.matchParentSize(),
      properties = properties,
      uiSettings = uiSettings
    )
    Switch(
      checked = uiSettings.zoomControlsEnabled,
      onCheckedChange = {
        uiSettings = uiSettings.copy(zoomControlsEnabled = it)
      }
    )
  }
}
