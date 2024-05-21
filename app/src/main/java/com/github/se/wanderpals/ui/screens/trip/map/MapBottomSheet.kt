package com.github.se.wanderpals.ui.screens.trip.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.GeoCords

/**
 * Composable function to display the bottom sheet with the place details.
 *
 * @param placeData The data of the place to be displayed.
 * @param bottomSheetScaffoldState The state of the bottom sheet.
 * @param uriHandler The handler to open the website.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomSheet(
    placeData: GeoCords,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    uriHandler: UriHandler
) {
  BottomSheetScaffold(
      modifier =
          Modifier.shadow(15.dp, shape = RoundedCornerShape(40.dp)).testTag("mapBottomSheet"),
      sheetContent = {
        Column {
          if (placeData.placeName.isNotEmpty()) {
            Text(
                modifier =
                    Modifier.align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                        .testTag("placeName"),
                text = placeData.placeName)
          }
          if (placeData.placeBusinessStatus.isNotEmpty()) {
            Row(modifier = Modifier.padding(bottom = 8.dp).testTag("placeBusinessStatus")) {
              Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = "Business Status Icon",
                  modifier = Modifier.size(24.dp))
              Text(text = placeData.placeBusinessStatus)
            }
          }
          if (placeData.placeAddress.isNotEmpty()) {
            Row(modifier = Modifier.padding(bottom = 8.dp).testTag("placeAddress")) {
              Icon(
                  imageVector = Icons.Default.Place,
                  contentDescription = "Place Icon",
                  modifier = Modifier.size(24.dp))
              Text(text = placeData.placeAddress)
            }
          }
          if (placeData.placeRating.isNotEmpty()) {
            Row(modifier = Modifier.padding(bottom = 8.dp).testTag("placeRating")) {
              Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = "Rating Icon",
                  modifier = Modifier.size(24.dp))
              Text(text = "${placeData.placeRating}/5.0")
            }
          }
          if (placeData.placeUserRatingsTotal.isNotEmpty()) {
            Row(modifier = Modifier.padding(bottom = 8.dp).testTag("placeUserRatingsTotal")) {
              Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "Total Icon",
                  modifier = Modifier.size(24.dp))
              Text(text = placeData.placeUserRatingsTotal)
            }
          }
          if (placeData.placePhoneNumber.isNotEmpty()) {
            Row(modifier = Modifier.padding(bottom = 8.dp).testTag("placePhoneNumber")) {
              Icon(
                  imageVector = Icons.Default.Phone,
                  contentDescription = "Phone Icon",
                  modifier = Modifier.size(24.dp))
              Text(text = placeData.placePhoneNumber)
            }
          }
          if (placeData.placeWebsite.isNotEmpty()) {
            Row(modifier = Modifier.padding(bottom = 8.dp).testTag("placeWebsite")) {
              Text(text = "Visit Website: ")
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "Phone Icon",
                  modifier =
                      Modifier.size(24.dp).clickable { uriHandler.openUri(placeData.placeWebsite) })
            }
          }

          if (placeData.placeOpeningHours.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            val listOfDays =
                placeData.placeOpeningHours.removePrefix("[").removeSuffix("]").split(", ")
            listOfDays.forEach {
              if (it != "null") {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                    text = it)
              }
            }
          }
        }
      },
      scaffoldState = bottomSheetScaffoldState) {}
}
