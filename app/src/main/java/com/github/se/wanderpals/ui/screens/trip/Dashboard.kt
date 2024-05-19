package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.dashboard.DashboardDocumentWidget
import com.github.se.wanderpals.ui.screens.dashboard.DashboardFinanceWidget
import com.github.se.wanderpals.ui.screens.dashboard.DashboardStopWidget
import com.github.se.wanderpals.ui.screens.dashboard.DashboardSuggestionWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The Dashboard screen.
 *
 * @param tripId the trip ID
 * @param dashboardViewModel the dashboard view model
 * @param navActions the navigation actions
 *
 * The tripId is used to identify the trip currently being displayed and interacted with. The
 * oldNavActions is used to navigate back to the overview screen.
 */
@Composable
fun Dashboard(
    tripId: String,
    dashboardViewModel: DashboardViewModel,
    navActions: NavigationActions
) {
  LaunchedEffect(Unit) {
    // Fetch the suggestions for the trip every time the screen is displayed
    dashboardViewModel.loadSuggestion(tripId)
    dashboardViewModel.loadExpenses(tripId)
    dashboardViewModel.loadTripTitle(tripId)
    dashboardViewModel.loadStops(tripId)
    dashboardViewModel.loadLastAddedSharedDocument(tripId)
    dashboardViewModel.loadLastAddedPrivateDocument(
        tripId, SessionManager.getCurrentUser()!!.userId)
  }

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val isLoading by dashboardViewModel.isLoading.collectAsState()
  val tripTitle by dashboardViewModel.tripTitle.collectAsState()
  val showDeleteDialog by dashboardViewModel.showDeleteDialog.collectAsState()

  ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = { Menu(scope, drawerState, navActions, dashboardViewModel) },
  ) {
    Text(modifier = Modifier.testTag("dashboardScreen"), text = " ")
    if (isLoading) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp).align(Alignment.Center).testTag("loading"))
      }
    } else {
      Scaffold(
          topBar = { TopDashboardBar(scope, drawerState, tripTitle) },
      ) { contentPadding ->
        Surface(
            modifier =
                Modifier.background(Color.White)
                    .padding(contentPadding)
                    .testTag("dashboardSuggestions")) {
              LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                item {
                  Spacer(modifier = Modifier.padding(8.dp))
                  DashboardSuggestionWidget(
                      viewModel = dashboardViewModel,
                      onClick = { navActions.navigateTo(Route.SUGGESTION) })

                  Spacer(modifier = Modifier.padding(8.dp))
                }

                item {
                  DashboardFinanceWidget(
                      viewModel = dashboardViewModel,
                      onClick = { navActions.navigateTo(Route.FINANCE) })

                  Spacer(modifier = Modifier.padding(8.dp))
                }

                item {
                  DashboardStopWidget(
                      viewModel = dashboardViewModel,
                      onClick = { navActions.navigateTo(Route.AGENDA) })

                  Spacer(modifier = Modifier.padding(8.dp))
                }
                item {
                  DashboardDocumentWidget(
                      onClick = { navActions.navigateTo(Route.DOCUMENT) },
                      viewModel = dashboardViewModel)
                  Spacer(modifier = Modifier.padding(8.dp))
                }
              }
            }

        if (showDeleteDialog) {
          AlertDialog(
              onDismissRequest = { dashboardViewModel.hideDeleteDialog() },
              title = { Text("Confirm Deletion") },
              text = {
                Text(
                    when (SessionManager.getIsNetworkAvailable()) {
                      true -> "Are you sure you want to delete this Trip?"
                      false -> "No internet connection. Please try again later."
                    })
              },
              confirmButton = {
                TextButton(
                    onClick = {
                      if (SessionManager.getIsNetworkAvailable()) {
                        dashboardViewModel.confirmDeleteTrip()
                        navActions.navigateTo(Route.OVERVIEW)
                      } else {
                        dashboardViewModel.hideDeleteDialog()
                      }
                    },
                    modifier = Modifier.testTag("confirmDeleteTripButton")) {
                      Text("Confirm", color = Color.Red)
                    }
              },
              dismissButton = {
                TextButton(
                    onClick = { dashboardViewModel.hideDeleteDialog() },
                    modifier = Modifier.testTag("cancelDeleteTripButton")) {
                      Text("Cancel")
                    }
              },
              modifier = Modifier.testTag("deleteTripDialog"))
        }
      }
    }
  }
}

/**
 * The menu for the dashboard screen.
 *
 * @param scope the coroutine scope
 * @param drawerState the drawer state
 * @param navActions the old navigation actions
 *
 * The scope is used to launch coroutines. The drawerState is used to control the drawer. The
 * oldNavActions is used to navigate back to the overview screen.
 */
@Composable
fun Menu(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navActions: NavigationActions,
    dashboardViewModel: DashboardViewModel
) {
  ModalDrawerSheet(
      drawerShape = MaterialTheme.shapes.large,
      modifier =
          Modifier.testTag("menuNav").requiredWidth(200.dp).requiredHeight(250.dp).padding(8.dp),
  ) {
    ElevatedButton(
        modifier = Modifier.testTag("backToOverview"),
        content = {
          Row {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            Text("Back to overview")
          }
        },
        onClick = {
          scope.launch {
            drawerState.close()
            navActions.navigateTo(Route.OVERVIEW)
          }
        })
    Spacer(modifier = Modifier.padding(2.dp))
    ElevatedButton(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp).testTag("AdminButtonTest"),
        content = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(id = R.drawable.logo_nsa),
                contentDescription = "NSA",
                modifier = Modifier.clip(CircleShape).size(30.dp))
            Text(text = "Admin", modifier = Modifier.padding(horizontal = 20.dp))
          }
        },
        onClick = {
          scope.launch {
            drawerState.close()
            navActions.navigateTo(Route.ADMIN_PAGE)
          }
        })
    Spacer(modifier = Modifier.padding(2.dp))
    ElevatedButton(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp).testTag("FinanceButtonTest"),
        content = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(id = R.drawable.finance_logo),
                contentDescription = "financeLogo",
                modifier = Modifier.clip(CircleShape).size(25.dp))
            Text(text = "Finance", modifier = Modifier.padding(horizontal = 20.dp))
          }
        },
        onClick = {
          scope.launch {
            drawerState.close()
            navActions.navigateTo(Route.FINANCE)
          }
        })

    if (SessionManager.getCurrentUser()!!.role == Role.OWNER &&
        SessionManager.getIsNetworkAvailable()) {
      ElevatedButton(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp).testTag("deleteTripButton"),
          content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  Icons.Default.Delete,
                  contentDescription = "Delete",
                  modifier = Modifier.clip(CircleShape).size(25.dp))
              Text(text = "Delete Trip", modifier = Modifier.padding(start = 20.dp))
            }
          },
          onClick = {
            scope.launch {
              drawerState.close()
              dashboardViewModel.deleteTrip()
            }
          })
    }
  }
}

/**
 * The top bar for the dashboard screen.
 *
 * @param scope the coroutine scope
 * @param drawerState the drawer state
 *
 * The scope is used to launch coroutines, and the drawerState is used to control the drawer.
 * Contains a menu button to open the drawer.
 */
@Composable
fun TopDashboardBar(scope: CoroutineScope, drawerState: DrawerState, tripTitle: String) {
  Column(
      modifier =
          Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).testTag("dashboardTopBar")) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment
                    .CenterVertically // This aligns all children vertically centered in the Row
            ) {
              ElevatedButton(
                  modifier = Modifier.testTag("menuButton"),
                  content = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                  onClick = {
                    scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                  })
              Text(
                  text = tripTitle,
                  modifier =
                      Modifier.padding(8.dp)
                          .testTag("tripTitle")
                          .weight(1f), // This makes the text expand and fill the space
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold, // This makes the text bold
                  fontSize = 24.sp, // This sets the font size to 24sp
                  maxLines = 1, // This makes the text to be displayed in a single line
                  overflow =
                      TextOverflow.Ellipsis // This makes the text to be ellipsized if it overflows
                  )
            }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
      }
}
