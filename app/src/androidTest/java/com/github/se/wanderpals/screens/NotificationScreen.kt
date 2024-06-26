package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.wanderpals.ui.navigation.Route
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class NotificationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<NotificationScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("notificationScreen") }) {

  val notificationButton: KNode = onNode { hasTestTag("notificationTab") }

  val announcementButton: KNode = onNode { hasTestTag("announcementsTab") }

  val createAnnouncementButton: KNode = onNode { hasTestTag("createAnnouncementButton") }

  val noItemsText: KNode = onNode { hasTestTag("noItemsText") }

  val announcementDialog: KNode = onNode { hasTestTag("announcementDialog") }
  val deleteAnnouncementDialog: KNode = onNode { hasTestTag("deleteAnnouncementDialog") }
  val confirmDeleteAnnouncementButton: KNode = onNode {
    hasTestTag("confirmDeleteAnnouncementButton")
  }
  val deleteAnnouncementButton: KNode = onNode { hasTestTag("deleteAnnouncementButton") }

  val notifJoinTripItemButton: KNode = onNode { hasTestTag("notifItemButton" + Route.ADMIN_PAGE) }

  val notifStopItemButton: KNode = onNode { hasTestTag("notifItemButton" + Route.STOPS_LIST) }

  val notifMeetItemButton: KNode = onNode { hasTestTag("notifItemButton" + Route.MAP) }

  val notifSuggestionItemButton: KNode = onNode {
    hasTestTag("notifItemButton" + Route.SUGGESTION_DETAIL)
  }

  val notifExpenseItemButton: KNode = onNode { hasTestTag("notifItemButton" + Route.EXPENSE_INFO) }
  val notifItemButtonWithoutPath: KNode = onNode { hasTestTag("notifItemButton") }

  val announcementItemButton1: KNode = onNode { hasTestTag("announcementItemButton1") }
}
