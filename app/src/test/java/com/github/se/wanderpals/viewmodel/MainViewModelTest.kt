package com.github.se.wanderpals.viewmodel

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.viewmodel.MainViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.NavigationActionsVariables
import com.github.se.wanderpals.ui.navigation.Route
import com.google.firebase.FirebaseApp
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

  private val context = ApplicationProvider.getApplicationContext<Context>()
  private lateinit var application: Application
  private lateinit var viewModel: MainViewModel
  private val testDispatcher = StandardTestDispatcher()

  private val mockShortcutManager = mockk<ShortcutManager>()

  private val navigationActions =
      NavigationActions(
          variables = NavigationActionsVariables(),
          mainNavigation = mockk(relaxed = true),
          tripNavigation = mockk(relaxed = true))

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    // Set the main dispatcher to a test dispatcher to control coroutine execution
    Dispatchers.setMain(testDispatcher)
    application = ApplicationProvider.getApplicationContext()

    // Create the ViewModel using a factory with the mocked repository
    val factory = MainViewModel.MainViewModelFactory(application)
    viewModel = factory.create(MainViewModel::class.java)

    viewModel.initShortcutManager(mockShortcutManager)
  }

  @Test
  fun `test getOverviewJoinTripDialogIsOpen`() {
    assertEquals(false, viewModel.getOverviewJoinTripDialogIsOpen())
  }

  @Test
  @ExperimentalCoroutinesApi
  fun `initRepository initializes the repository with the given userId`() = runBlockingTest {
    FirebaseApp.initializeApp(context)
    viewModel.initRepository("userId123")
    advanceUntilIdle()
    assertEquals("userId123", viewModel.getTripsRepository().uid)

    // change the UID
    viewModel.initRepository("userIddiff")
    assertEquals("userIddiff", viewModel.getTripsRepository().uid)
  }

  @Test
  fun `getUserName returns formatted username for gmail emails`() {
    val userName = viewModel.getUserName("test@gmail.com")
    assertEquals("test@gml", userName)
  }

  @Test
  fun `getUserName returns formatted username for non-gmail emails`() {
    val userName = viewModel.getUserName("test@example.com")
    assertEquals("test@mle${"example.com".hashCode().mod(1000)}", userName)
  }

  @Test
  fun `test addDynamicShortcutCreateTrip`() {
    // Mock dependencies
    mockkStatic(ShortcutManagerCompat::class)
    mockkConstructor(ShortcutInfoCompat.Builder::class)

    // Mock ShortcutInfoCompat.Builder behavior
    val builder = mockk<ShortcutInfoCompat.Builder>()
    val shortcutInfo = mockk<ShortcutInfoCompat>()

    every { builder.setShortLabel("Create trip") } returns builder
    every { builder.setLongLabel("Clicking this will create a new trip.") } returns builder
    every { builder.setIcon(any<IconCompat>()) } returns builder
    every { builder.setIntent(any()) } returns builder
    every { builder.build() } returns shortcutInfo
    every { shortcutInfo.toShortcutInfo() } returns mockk(relaxed = true)
    every { shortcutInfo.id } returns "dynamic_" + Route.CREATE_TRIP

    every { anyConstructed<ShortcutInfoCompat.Builder>().setShortLabel("Create trip") } returns
        builder

    // Execute the function
    viewModel.addDynamicShortcutCreateTrip()

    // Verify that ShortcutManagerCompat.pushDynamicShortcut was called with the expected shortcut
    verify { ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo) }

    // Clear mocks
    unmockkAll()
  }

  @Test
  fun `test addDynamicShortcutJoinTrip`() {
    // Mock dependencies
    mockkStatic(ShortcutManagerCompat::class)
    mockkConstructor(ShortcutInfoCompat.Builder::class)

    // Mock ShortcutInfoCompat.Builder behavior
    val builder = mockk<ShortcutInfoCompat.Builder>()
    val shortcutInfo = mockk<ShortcutInfoCompat>()

    every { builder.setShortLabel("Join trip") } returns builder
    every { builder.setLongLabel("Clicking this will join a new trip.") } returns builder
    every { builder.setIcon(any<IconCompat>()) } returns builder
    every { builder.setIntent(any()) } returns builder
    every { builder.build() } returns shortcutInfo
    every { shortcutInfo.toShortcutInfo() } returns mockk(relaxed = true)
    every { shortcutInfo.id } returns "dynamic_" + Route.OVERVIEW

    every { anyConstructed<ShortcutInfoCompat.Builder>().setShortLabel("Join trip") } returns
        builder

    // Execute the function
    viewModel.addDynamicShortcutJoinTrip()

    // Verify that ShortcutManagerCompat.pushDynamicShortcut was called with the expected shortcut
    verify { ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo) }

    // Clear mocks
    unmockkAll()
  }

  @Test
  fun `test removeAllDynamicShortcuts`() {
    // Mock dependencies
    mockkStatic(ShortcutManagerCompat::class)
    mockkConstructor(ShortcutInfoCompat.Builder::class)

    // Execute the function
    viewModel.removeAllDynamicShortcuts()

    // Verify that ShortcutManagerCompat.pushDynamicShortcut was called with the expected shortcut
    verify { ShortcutManagerCompat.removeAllDynamicShortcuts(context) }

    // Clear mocks
    unmockkAll()
  }

  @Test
  fun `test handleIntent with CREATE_TRIP route`() {
    val intent = mockk<Intent>(relaxed = true)
    every { intent.getStringExtra("shortcut_id") } returns Route.CREATE_TRIP

    viewModel.handleIntent(intent, navigationActions)

    assertFalse(viewModel.getOverviewJoinTripDialogIsOpen())
    verify { navigationActions.mainNavigation.setStartDestination(Route.CREATE_TRIP) }
  }

  @Test
  fun `test handleIntent with OVERVIEW route`() {
    val intent = mockk<Intent>(relaxed = true)
    every { intent.getStringExtra("shortcut_id") } returns Route.OVERVIEW

    viewModel.handleIntent(intent, navigationActions)

    assertTrue(viewModel.getOverviewJoinTripDialogIsOpen())
    verify { navigationActions.mainNavigation.setStartDestination(Route.OVERVIEW) }
  }

  @Test
  fun `test handleIntent with TRIP route`() {
    val intent = mockk<Intent>(relaxed = true)
    every { intent.getStringExtra("shortcut_id") } returns Route.TRIP
    every { intent.getStringExtra("trip_id") } returns "test_trip_id"

    viewModel.handleIntent(intent, navigationActions)

    assertFalse(viewModel.getOverviewJoinTripDialogIsOpen())
    verify { navigationActions.mainNavigation.setStartDestination(Route.TRIP) }
  }

  @Test
  fun `test handleIntent with unknown route`() {
    val intent = mockk<Intent>(relaxed = true)
    every { intent.getStringExtra("shortcut_id") } returns Route.MAP

    viewModel.handleIntent(intent, navigationActions)

    // No changes to overviewJoinTripDialogIsOpen expected
    verify(exactly = 0) { navigationActions.mainNavigation.setStartDestination(any()) }
  }

  @Test
  fun `test addPinnedShortcutTrip`() {
    mockkStatic(Build.VERSION.SDK_INT::class)
    mockkStatic(PendingIntent::class)
    mockkConstructor(ShortcutInfo.Builder::class)
    mockkStatic(Icon::class)

    // Mock the shortcut manager
    every { mockShortcutManager.isRequestPinShortcutSupported } returns true

    // Prepare the Trip object
    val trip = Trip("test_trip_id", "title", LocalDate.now(), LocalDate.now(), 0.0, "description")

    // Mock ShortcutInfo.Builder behavior
    val builder = mockk<ShortcutInfo.Builder>()
    val shortcutInfo = mockk<ShortcutInfo>()
    val icon = mockk<Icon>()

    every { Icon.createWithResource(context, R.drawable.logo_projet) } returns icon

    every { builder.setShortLabel(trip.title) } returns builder
    every { builder.setLongLabel("Open ${trip.title}") } returns builder
    every { builder.setIcon(icon) } returns builder
    every { builder.setIntent(any()) } returns builder
    every { builder.build() } returns shortcutInfo

    every { anyConstructed<ShortcutInfo.Builder>().setShortLabel(trip.title) } returns builder

    // Mock PendingIntent
    val pendingIntent = mockk<PendingIntent>()
    every { PendingIntent.getBroadcast(any(), any(), any(), any()) } returns pendingIntent
    every { pendingIntent.intentSender } returns mockk(relaxed = true)
    every {
      mockShortcutManager.requestPinShortcut(shortcutInfo, pendingIntent.intentSender)
    } returns true

    every { mockShortcutManager.createShortcutResultIntent(any()) } returns mockk(relaxed = true)

    // Execute the function
    viewModel.addPinnedShortcutTrip(trip)

    // Verify ShortcutManager interactions
    verify { mockShortcutManager.requestPinShortcut(shortcutInfo, pendingIntent.intentSender) }

    // Clear mocks
    unmockkAll()
  }
}
