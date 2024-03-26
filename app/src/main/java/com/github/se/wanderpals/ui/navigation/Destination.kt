package com.github.se.wanderpals.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

data class Destination(val route: String, val icon: ImageVector, val text: String)

val TOP_LEVEL_DESTINATIONS =
    listOf(
        Destination(route = Route.OVERVIEW, icon = Icons.Default.AccountBox, text = "Overview"),
        Destination(route = Route.TRIP, icon = Icons.Default.AccountBox, text = "Trip"),
    )

// Finance, Agenda , Dashboard, Map, Notifications
val TRIP_DESTINATIONS =
    listOf(
        Destination(route = Route.FINANCE, icon = Icons.Default.ShoppingCart, text = "Finance"),
        Destination(route = Route.AGENDA, icon = Icons.Default.DateRange, text = "Agenda"),
        Destination(route = Route.DASHBOARD, icon = Icons.Default.Home, text = "Dashboard"),
        Destination(route = Route.MAP, icon = Icons.Default.Place, text = "Map"),
        Destination(route = Route.NOTIFICATION, icon = Icons.Default.AccountBox, text = "Trip"),
    )
