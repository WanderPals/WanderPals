package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.viewmodel.MembersViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions

/**
 * Composable function that represents the list of members in the dashboard.
 *
 * @param membersViewModel The ViewModel for managing members.
 * @param navActions The navigation actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardMemberList(
    membersViewModel: MembersViewModel,
    navActions: NavigationActions,
) {
  val members = membersViewModel.members.collectAsState()
  var selectedMember by remember { mutableStateOf<User?>(null) }

  selectedMember?.let { user ->
    DashboardMemberDetail(
        member = user,
        onDismiss = { selectedMember = null }, // When the popup is dismissed
    )
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { // Display the title of the screen
              Text(
                  text = "Member List",
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.testTag("MemberListTitle"))
            },
            navigationIcon = { // Add a back button to the top app bar
              IconButton(
                  onClick = { navActions.goBack() }, modifier = Modifier.testTag("BackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                    )
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        )
      }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
          for (member in members.value) { // Display each member in the list
            DashboardMemberItem(member = member, onClick = { selectedMember = member })
            if (member != members.value.last()) { // Add a divider between the members
              Spacer(modifier = Modifier.height(4.dp))
              HorizontalDivider(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 32.dp)
                          .testTag("divider" + member.userId))
              Spacer(modifier = Modifier.height(4.dp))
            }
          }
        }
      }
}
