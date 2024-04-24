package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.wanderpals.ui.navigation.Route
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class NotificationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<NotificationScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("notificationScreen") }) {

    val notificationButton: KNode = onNode { hasTestTag("notificationButton") }

    val announcementButton: KNode = onNode { hasTestTag("announcementButton") }

    val createAnnouncementButton: KNode = onNode { hasTestTag("createAnnouncementButton") }

    val noItemsText: KNode = onNode { hasTestTag("noItemsText") }


    val announcementDialog: KNode = onNode { hasTestTag("announcementDialog") }
    val deleteAnnouncementDialog: KNode = onNode { hasTestTag("deleteAnnouncementDialog") }
    val confirmDeleteAnnouncementButton: KNode = onNode { hasTestTag("confirmDeleteAnnouncementButton") }
    val deleteAnnouncementButton: KNode = onNode { hasTestTag("deleteAnnouncementButton") }

    val notifItemButtonWithPath: KNode =
        onNode { hasTestTag("notifItemButton" + Route.MEMBERS) }

    val notifItemButtonWithoutPath: KNode = onNode { hasTestTag("notifItemButton") }

    val announcementItemButton1: KNode =
        onNode { hasTestTag("announcementItemButton1") }



}
