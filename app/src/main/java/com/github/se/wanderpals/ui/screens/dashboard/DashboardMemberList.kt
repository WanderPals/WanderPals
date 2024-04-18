package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionDetailPopup
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        Column(modifier = Modifier.padding(paddingValues))
        {
            for (member in members.value) {
                DashboardMemberItem(member = member, onClick = {selectedMember = member})
            }
        }
    }
}

@Preview
@Composable
fun PreviewDashboardMemberList() {
    DashboardMemberList(DashboardViewModel(TripsRepository("", Dispatchers.IO), ""),
        NavigationActions(rememberNavController()))
}
