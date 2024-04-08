package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The Dashboard screen.
 *
 * @param tripId the trip ID
 * @param oldNavActions the old navigation actions
 *
 * The tripId is used to identify the trip currently being displayed and interacted with. The
 * oldNavActions is used to navigate back to the overview screen.
 */
@Composable
fun Dashboard(tripId: String, oldNavActions: NavigationActions) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = { Menu(scope, drawerState, oldNavActions) },
  ) {
    Scaffold(
        topBar = { TopDashboardBar(scope, drawerState) },
    ) { contentPadding ->
      Text(
          modifier = Modifier.padding(contentPadding).testTag("dashboardScreen"),
          text = "Dashboard for trip $tripId")
    }
  }
}

/**
 * The menu for the dashboard screen.
 *
 * @param scope the coroutine scope
 * @param drawerState the drawer state
 * @param oldNavActions the old navigation actions
 *
 * The scope is used to launch coroutines. The drawerState is used to control the drawer. The
 * oldNavActions is used to navigate back to the overview screen.
 */
@Composable
fun Menu(scope: CoroutineScope, drawerState: DrawerState, oldNavActions: NavigationActions) {
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
            oldNavActions.navigateTo(Route.OVERVIEW)
          }
        })
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
fun TopDashboardBar(scope: CoroutineScope, drawerState: DrawerState) {
  Column(modifier = Modifier.padding(8.dp)) {
    Row(modifier = Modifier.fillMaxWidth()) {
      ElevatedButton(
          modifier = Modifier.testTag("menuButton"),
          content = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
          onClick = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } })
    }
    HorizontalDivider(modifier = Modifier.padding(8.dp))
  }
}
