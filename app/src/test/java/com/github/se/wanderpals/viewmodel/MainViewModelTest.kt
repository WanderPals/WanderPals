package com.github.se.wanderpals.viewmodel

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.model.viewmodel.MainViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    // Set the main dispatcher to a test dispatcher to control coroutine execution
    Dispatchers.setMain(testDispatcher)
    application = ApplicationProvider.getApplicationContext()

    // Create the ViewModel using a factory with the mocked repository
    val factory = MainViewModel.MainViewModelFactory(application)
    viewModel = factory.create(MainViewModel::class.java)
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
}
