package com.github.se.wanderpals.stops

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.viewmodel.StopItemViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeStopItemViewModel() : StopItemViewModel(mockk(relaxed = true), tripId = "1") {
  private val _isDeleted = MutableStateFlow(false)
  override val isDeleted: StateFlow<Boolean> = _isDeleted

  override fun deleteStop(stopId: String) {
    _isDeleted.value = true
  }
}

@RunWith(AndroidJUnit4::class)
class StopListTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    // Set up the test

  }

  @Test fun testDeleteStop() {}
}
