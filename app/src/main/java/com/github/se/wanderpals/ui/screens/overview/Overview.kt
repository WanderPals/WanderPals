package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.isMapManagerInitialized
import com.github.se.wanderpals.mapManager
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route

const val EMPTY_CODE = ""
/**
 * Composable function that represents the Overview screen, displaying the list of trips of a user.
 * Provides functionalities such as searching trips by their title, creating a new trip, and joining
 * a trip.
 *
 * @param overviewViewModel The view model containing the data and logic for the overview screen.
 * @param navigationActions The navigation actions used for navigating to different screens.
 */
@Composable
fun Overview(overviewViewModel: OverviewViewModel, navigationActions: NavigationActions) {
  LaunchedEffect(
      Unit) { // This ensures getAllTrips is called once per composition, not on every recomposition
        overviewViewModel.getAllTrips()
        if (isMapManagerInitialized()) {
          mapManager.executeLocationIntentStop()
        }
      }
  // Collecting trips list and loading state from view model
  val tripsList by overviewViewModel.state.collectAsState()
  val isLoading by overviewViewModel.isLoading.collectAsState()

  // State for managing search text
  var searchText by remember { mutableStateOf("") }

  var dialogIsOpen by remember { mutableStateOf(false) }

  // Display loading indicator waiting for database to fetch the trips of the user
  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier.size(50.dp).align(Alignment.Center))
    }
  } else {

    if (dialogIsOpen) {

      // Display the Dialog for joining a trip using its code if if the dialog is
      DialogHandler(
          closeDialogueAction = { dialogIsOpen = false },
          addTripCodeAction = { tripId -> overviewViewModel.joinTrip(tripId) })
    }

    // Display scaffold with top bar, bottom bar, and content when data is loaded
    Scaffold(
        modifier = Modifier.testTag("overviewScreen"),
        topBar = {
          // Top bar with search functionality based on the title of the trips
          OverviewTopBar(
              overviewViewModel = overviewViewModel,
              searchText = searchText,
              onSearchTextChanged = { newSearchText -> searchText = newSearchText })
        },
        bottomBar = {
          // Bottom bar containing buttons to create a new trip and join a trip
          OverviewBottomBar(
              onCreateTripClick = { navigationActions.navigateTo(Route.CREATE_TRIP) },
              onLinkClick = { dialogIsOpen = true })
        }) {
            // Content of the overview screen
            innerPadding ->
          OverviewContent(
              innerPadding = innerPadding,
              navigationActions = navigationActions,
              tripsList = tripsList,
              searchText = searchText,
              overviewViewModel = overviewViewModel)
        }
  }
}

/**
 * Composable function to handle a dialog for inserting a trip code.
 *
 * @param closeDialogueAction A lambda function to be called when the dialog is dismissed.
 * @param addTripCodeAction A lambda function that takes a trip code String as input and returns a
 *   Boolean indicating whether the trip code was successfully added.
 */
@Composable
fun DialogHandler(closeDialogueAction: () -> Unit, addTripCodeAction: (String) -> Boolean) {

  // Mutable state to hold the trip code input and error state
  var tripCode by remember { mutableStateOf(EMPTY_CODE) }
  var isError by remember { mutableStateOf(false) }

  // Dialog composable
  Dialog(
      onDismissRequest = {
        closeDialogueAction()
        isError = false
        tripCode = EMPTY_CODE
      }) {
        Surface(
            modifier = Modifier.height(200.dp).testTag("dialog"),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)) {
              Column(
                  modifier = Modifier.padding(16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    // Input field for trip code
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = tripCode,
                        onValueChange = { tripCode = it },
                        label = {
                          Text(
                              text = "Insert trip code here",
                              style =
                                  TextStyle(
                                      fontSize = 18.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        },
                        isError = isError,
                        // Text to display if an error occurs while inputing the trip code
                        supportingText = {
                          if (isError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Invalid trip code",
                                color = MaterialTheme.colorScheme.error)
                          }
                        },
                        singleLine = true)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Button to join with trip code
                    Button(
                        enabled = SessionManager.getIsNetworkAvailable(),
                        onClick = {
                          val success = addTripCodeAction(tripCode)

                          if (success) {

                            isError = false
                            tripCode = EMPTY_CODE

                            closeDialogueAction()
                          } else {
                            isError = true
                          }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        shape = RoundedCornerShape(12.dp)) {
                          Text(
                              text = "Join",
                              style =
                                  TextStyle(
                                      fontSize = 16.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        }
                  }
            }
      }
}
