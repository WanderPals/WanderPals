package com.github.se.wanderpals.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.github.se.wanderpals.model.repository.TripsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NetworkHelperTest {

  private lateinit var repository: TripsRepository
  private lateinit var networkHelper: NetworkHelper
  private lateinit var connectivityManager: ConnectivityManager
  private lateinit var mockNetwork: Network
  private lateinit var context: Context // Mock the context

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    repository = mockk(relaxed = true)
    connectivityManager = mockk(relaxed = true)
    context = mockk() // Initialize the mocked context

    // Mock System Service retrieval for the connectivity manager
    every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager

    networkHelper = NetworkHelper(context, repository)
    mockNetwork = mockk()
  }

  @Test
  fun `test network becomes available`() {
    val slot = slot<ConnectivityManager.NetworkCallback>()
    verify { connectivityManager.registerDefaultNetworkCallback(capture(slot)) }
    slot.captured.onAvailable(mockNetwork)

    verify { repository.isNetworkEnabled = true }
    assertTrue(SessionManager.isNetworkAvailable)
  }

  @Test
  fun `test network is lost`() {
    val slot = slot<ConnectivityManager.NetworkCallback>()
    verify { connectivityManager.registerDefaultNetworkCallback(capture(slot)) }
    slot.captured.onLost(mockNetwork)

    verify { repository.isNetworkEnabled = false }
    assertFalse(SessionManager.isNetworkAvailable)
  }
}
