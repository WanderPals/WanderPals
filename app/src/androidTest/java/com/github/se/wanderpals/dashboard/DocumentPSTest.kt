package com.github.se.wanderpals.dashboard

import android.content.Context
import android.net.Uri
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Documents
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DocumentPSViewModel
import com.github.se.wanderpals.screens.DocumentsPSScreen
import com.github.se.wanderpals.ui.screens.docs.DocumentsPS
import com.google.firebase.storage.StorageReference
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// fake view model
class FakeDocumentPSViewModel :
    DocumentPSViewModel(tripsRepository = TripsRepository("", Dispatchers.IO), "tripId") {
  override var documentslistURL: MutableStateFlow<List<Documents>> =
      MutableStateFlow(
          listOf(
              Documents("url1", "name1"), Documents("url2", "name2"), Documents("url3", "name3")))
  override var documentslistUserURL: MutableStateFlow<List<Documents>> =
      MutableStateFlow(listOf(Documents("url1", "name1"), Documents("url2", "name2")))

  override fun getAllDocumentsFromTrip() {
    documentslistURL.value
  }

  override fun getAllDocumentsFromCurrentUser() {
    documentslistUserURL.value
  }

  override fun addDocument(
      documentsName: String,
      documentsURL: Uri,
      path: String,
      context: Context,
      storageReference: StorageReference?,
      state: Int
  ) {
    if (state == 0) {
      documentslistURL.value += Documents(documentsURL.toString(), documentsName)
    } else {
      documentslistURL.value += Documents(documentsURL.toString(), documentsName)
    }
  }
}

@RunWith(AndroidJUnit4::class)
class DocumentPSTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalCoroutinesApi::class) private val testScope = TestCoroutineScope()

  @get:Rule val mockkRule = MockKRule(this)

  @Test
  fun testDocumentPS() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent { DocumentsPS(viewModel = viewModel, storageReference = null) }
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

  @Test
  fun testClickedOnPrivateAndShared() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent { DocumentsPS(viewModel = viewModel, storageReference = null) }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabShared {
        performClick()
        assertTextContains("Shared")
      }
      document0 { assertIsNotDisplayed() }
      document1 { assertIsNotDisplayed() }
      document2 { assertIsNotDisplayed() }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDocumentIsDisplayed() =
      testScope.runBlockingTest {
        val viewModel = FakeDocumentPSViewModel()
        composeTestRule.setContent { DocumentsPS(viewModel = viewModel, storageReference = null) }
        val screen = DocumentsPSScreen(composeTestRule)
        screen.tabShared.performClick()

        screen.shimmerdocument0.assertIsDisplayed()
        screen.shimmerdocument1.assertIsDisplayed()
        screen.shimmerdocument2.assertIsDisplayed()

        screen.document0.assertIsNotDisplayed()

        screen.documentImageBox { assertIsNotDisplayed() }
      }

  @Test
  fun testDocumentIsClicked() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent { DocumentsPS(viewModel = viewModel, storageReference = null) }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabShared { performClick() }
      sharedDocumentslist { assertIsDisplayed() }
      composeTestRule.onNodeWithTag("sharedDocuments").onChildren().assertCountEquals(3)
    }
  }

  @Test
  fun testDocumentUserIsDisplayed() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent { DocumentsPS(viewModel = viewModel, storageReference = null) }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      tabShared { performClick() }
      tabPrivate { performClick() }

      privateDocumentslist { assertIsDisplayed() }
      sharedDocumentslist { assertIsNotDisplayed() }

      composeTestRule.onNodeWithTag("privateDocuments").onChildren().assertCountEquals(2)

      documentUser0 { assertIsNotDisplayed() }
      documentUser1 { assertIsNotDisplayed() }
    }
  }

  @Test
  fun testAddDocument() = run {
    val viewModel = FakeDocumentPSViewModel()
    composeTestRule.setContent { DocumentsPS(viewModel = viewModel, storageReference = null) }
    ComposeScreen.onComposeScreen<DocumentsPSScreen>(composeTestRule) {
      floatingActionButton { performClick() }
      documentBox { assertIsDisplayed() }
      documentNameBox {
        assertIsDisplayed()
        assertHasClickAction()
      }
      addDocumentButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      acceptButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      cancelButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}
