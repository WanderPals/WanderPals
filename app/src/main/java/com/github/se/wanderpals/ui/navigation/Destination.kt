package com.github.se.wanderpals.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
        Destination(route = Route.MEMBERS, icon = Icons.AutoMirrored.Filled.List, text = "Member List"),
        Destination(route = Route.CREATE_ANNOUNCEMENT, icon = Icons.Default.Create, text = "CreateAnnouncement"))
