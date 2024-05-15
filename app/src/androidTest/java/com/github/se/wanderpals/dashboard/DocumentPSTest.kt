package com.github.se.wanderpals.dashboard

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DocumentPSViewModel
import com.github.se.wanderpals.screens.DocumentsPSScreen
import com.github.se.wanderpals.ui.screens.DocumentsPS
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// fake view model
class FakeDocumentPSViewModel :
    DocumentPSViewModel(tripsRepository = TripsRepository("", Dispatchers.IO), "tripId") {
  override var documentslistURL =
      MutableStateFlow(
          listOf(
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Ffr.vecteezy.com%2Fvecteur-libre%2Fbillet-avion&psig=AOvVaw1z46CJaCsQt_21f3_98pTc&ust=1715808551692000&source=images&cd=vfe&opi=89978449&ved=0CBAQjRxqFwoTCNjls4uLjoYDFQAAAAAdAAAAABAE",
              "url2",
              "url3"))
  override var documentslistUserURL = MutableStateFlow(listOf("url1", "url2"))

  override fun getAllDocumentsFromTrip() {
    documentslistURL.value = listOf("url1", "url2", "url3")
  }

  override fun getAllDocumentsFromCurrentUser() {
    documentslistUserURL.value = listOf("url1", "url2")
  }

  override fun addDocumentToTrip(documentURL: String, tripID: String) {
    documentslistURL.value += documentURL
  }

  override fun updateDocumentsOfCurrentUser(documentURL: String) {
    documentslistUserURL.value += documentURL
  }
}

@RunWith(AndroidJUnit4::class)
class DocumentPSTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  // test1: assert all displayed documents
  @Test
  fun testDocumentPS() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent {
      DocumentsPS(tripId = "tripId", viewModel = viewModel, storageReference = null)
    }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabPrivate {
        assertIsDisplayed()
        assertTextContains("Private")
      }
      tabShared {
        assertIsDisplayed()
        assertTextContains("Shared")
      }
      floatingActionButton { assertIsDisplayed() }
    }
  }

  // test 2:Click on the private Tab
  @Test
  fun testClickedOnPrivateAndShared() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent {
      DocumentsPS(tripId = "tripID", viewModel = viewModel, storageReference = null)
    }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabShared {
        performClick()
        assertTextContains("Shared")
      }
      document0 {
        assertIsDisplayed()
        assertHasClickAction()
      }
      document1 {
        assertIsDisplayed()
        assertHasClickAction()
      }
      document2 {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  // test3: assert Images are displayed
  @Test
  fun testDocumentIsDisplayed() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent {
      DocumentsPS(tripId = "tripID", viewModel = viewModel, storageReference = null)
    }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabShared { performClick() }
      document0 { performClick() }
      documentImageBox {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  // test4: assert the document is clicked
  @Test
  fun testDocumentIsClicked() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent {
      DocumentsPS(tripId = "tripID", viewModel = viewModel, storageReference = null)
    }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabShared { performClick() }
      document0 { performClick() }
      documentImageBox {
        performClick()
        assertIsNotDisplayed()
      }
    }
  }

  // test5: assert the document user are displayed
  @Test
  fun testDocumentUserIsDisplayed() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent {
      DocumentsPS(tripId = "tripID", viewModel = viewModel, storageReference = null)
    }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabShared { performClick() }
      tabPrivate { performClick() }
      documentUser0 {
        assertIsDisplayed()
        assertHasClickAction()
      }
      documentUser1 {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}
