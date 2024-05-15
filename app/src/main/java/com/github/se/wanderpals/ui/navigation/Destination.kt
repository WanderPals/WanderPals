package com.github.se.wanderpals.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a destination in the app.
 *
 * @param route The route of the destination.
 * @param icon The icon of the destination.
 * @param text The text of the destination.
 */
data class Destination(val route: String, val icon: ImageVector, val text: String)

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
            route = Route.SUGGESTION, icon = Icons.AutoMirrored.Filled.List, text = "Suggestion"),
        Destination(route = Route.AGENDA, icon = Icons.Default.DateRange, text = "Agenda"),
        Destination(route = Route.DASHBOARD, icon = Icons.Default.Home, text = "Dashboard"),
        Destination(route = Route.MAP, icon = Icons.Default.Place, text = "Map"),
        Destination(
            route = Route.NOTIFICATION, icon = Icons.Default.Notifications, text = "Notification"),
        Destination(
            route = Route.SUGGESTION_DETAIL,
            icon = Icons.AutoMirrored.Filled.List,
            text = "Detail Suggestion"),
        Destination(
            route = Route.CREATE_ANNOUNCEMENT,
            icon = Icons.Default.Create,
            text = "CreateAnnouncement"),
        Destination(route = Route.FINANCE, icon = Icons.Default.Menu, text = "finance"),
        Destination(
            route = Route.CREATE_EXPENSE, icon = Icons.Default.Create, text = "Create Expense"),
        Destination(route = Route.STOPS_LIST, icon = Icons.Default.Menu, text = "Stops List"),
        Destination(route = Route.EXPENSE_INFO, icon = Icons.Default.Create, text = "Expense info"),
        Destination(route = Route.DOCUMENT, icon = Icons.Default.Menu, text = "document"))
