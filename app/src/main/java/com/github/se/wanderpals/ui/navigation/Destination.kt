package com.github.se.wanderpals.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a destination in the app.
 *
 * @param route The route of the destination.
 * @param filledIcon The icon of the destination.
 * @param text The text of the destination.
 */
data class Destination(val route: String, val filledIcon: ImageVector, val outlinedIcon: ImageVector,  val text: String)

val MAIN_ROUTES =
    listOf(
        Route.SIGN_IN,
        Route.OVERVIEW,
        Route.TRIP,
        Route.CREATE_TRIP,
        Route.CREATE_SUGGESTION,
        Route.ADMIN_PAGE)

val TRIP_BOTTOM_BAR by lazy { TRIP_DESTINATIONS.subList(0, 5) }

/** List of destinations in the trip screen. */
val TRIP_DESTINATIONS =
    listOf(
        Destination(
            route = Route.SUGGESTION, filledIcon = Icons.AutoMirrored.Filled.List, outlinedIcon = Icons.AutoMirrored.Outlined.List,  text = "Suggestion"),
        Destination(route = Route.AGENDA, filledIcon = Icons.Default.DateRange, outlinedIcon = Icons.Outlined.DateRange, text = "Agenda"),
        Destination(route = Route.DASHBOARD, filledIcon = Icons.Default.Home, Icons.Outlined.Home, text = "Dashboard"),
        Destination(route = Route.MAP, filledIcon = Icons.Default.Place, Icons.Outlined.Place, text = "Map"),
        Destination(
            route = Route.NOTIFICATION, filledIcon = Icons.Default.Notifications, Icons.Outlined.Notifications, text = "Notification"),
        Destination(
            route = Route.SUGGESTION_DETAIL,
            filledIcon = Icons.AutoMirrored.Filled.List,
            outlinedIcon = Icons.AutoMirrored.Outlined.List,
            text = "Detail Suggestion"),
        Destination(
            route = Route.CREATE_ANNOUNCEMENT,
            filledIcon = Icons.Default.Create,
            outlinedIcon = Icons.Outlined.Create,
            text = "CreateAnnouncement"),
        Destination(route = Route.FINANCE, filledIcon = Icons.Default.Menu, Icons.Outlined.Menu, text = "finance"),
        Destination(
            route = Route.CREATE_EXPENSE, filledIcon = Icons.Default.Create, Icons.Outlined.Create, text = "Create Expense"),
        Destination(route = Route.STOPS_LIST, filledIcon = Icons.Default.Menu, Icons.Outlined.Menu, text = "Stops List"),
        Destination(route = Route.EXPENSE_INFO, filledIcon = Icons.Default.Create, Icons.Outlined.Create, text = "Expense info"),
        Destination(
            route = Route.SUGGESTION_HISTORY,
            filledIcon = Icons.Default.Menu,
            Icons.Outlined.Menu,
            text = "gSuggestion History"),
        Destination(route = Route.DOCUMENT, filledIcon = Icons.Default.Menu, Icons.Outlined.Menu, text = "Document"))
