package com.github.se.wanderpals.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(val route: String, val icon: ImageVector, val text: String)

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = Route.OVERVIEW,
            icon = Icons.Default.AccountBox,
            text = "Overview"
        ),
        TopLevelDestination(
            route = Route.TRIP,
            icon = Icons.Default.AccountBox,
            text = "Trip"
        ),
        TopLevelDestination(
            route = Route.MAP_ROUTE, icon = Icons.Default.Place, text = "Map"
        )
    )

val TRIP_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = Route.CREATE_TRIP, icon = Icons.Default.Add, text = "Create Trip")
    )
