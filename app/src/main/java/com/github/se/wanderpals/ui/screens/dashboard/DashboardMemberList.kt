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
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardMemberList(
    dashboardViewModel: DashboardViewModel,
    navActions: NavigationActions,
) {
  val members = dashboardViewModel.members.collectAsState()
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
            title = {
              Text(
                  text = "Member List",
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.testTag("MemberListTitle"))
            },
            navigationIcon = {
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
          for (member in members.value) {
            DashboardMemberItem(member = member, onClick = { selectedMember = member })
            if (member != members.value.last()) {
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
