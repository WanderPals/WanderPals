package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SessionManagerTest {

  @Before
  fun setUp() {
    // Ensure SessionManager is reset before each test
    SessionManager.logout()
  }

  @Test
  fun testSetUserSession() {
    SessionManager.setUserSession(
        userId = "1",
        name = "John Doe",
        email = "john@example.com",
        role = Role.ADMIN,
        geoCoords = GeoCords(37.7749, -122.4194))

    assertNotNull(SessionManager.getCurrentUser())
    SessionManager.getCurrentUser()?.apply {
      assertEquals("1", userId)
      assertEquals("John Doe", name)
      assertEquals("john@example.com", email)
      assertEquals(Role.ADMIN, role)
      assertEquals(37.7749, geoCords.latitude, 0.001)
      assertEquals(-122.4194, geoCords.longitude, 0.001)
    }
  }

  @Test
  fun testGetCurrentUser() {
    assertNull(SessionManager.getCurrentUser())
    SessionManager.setUserSession(userId = "2", name = "Jane Doe")
    assertNotNull(SessionManager.getCurrentUser())
  }

  @Test
  fun testCanRemove() {
    SessionManager.setUserSession(userId = "123", role = Role.ADMIN)
    assertTrue(SessionManager.canRemove("456")) // Admin can remove any user

    SessionManager.setUserSession(userId = "123", role = Role.MEMBER)
    assertFalse(SessionManager.canRemove("456")) // Normal user can't remove another user

    assertTrue(SessionManager.canRemove("123")) // User can remove themselves
  }

  @Test
  fun testIsAdmin() {
    SessionManager.setUserSession(role = Role.ADMIN)
    assertTrue(SessionManager.isAdmin())

    SessionManager.setUserSession(role = Role.MEMBER)
    assertFalse(SessionManager.isAdmin())
  }

  @Test
  fun testSetGeoCords() {
    val newGeoCords = GeoCords(51.5074, -0.1278)
    SessionManager.setUserSession(geoCoords = GeoCords(0.0, 0.0))
    SessionManager.setGeoCords(newGeoCords)
    assertEquals(newGeoCords, SessionManager.getCurrentUser()?.geoCords)
  }

  @Test
  fun testSetRole() {
    SessionManager.setUserSession(role = Role.MEMBER)
    SessionManager.setRole(Role.ADMIN)
    assertEquals(Role.ADMIN, SessionManager.getCurrentUser()?.role)
  }

  @Test
  fun testLogout() {
    SessionManager.setUserSession(userId = "test")
    assertNotNull(SessionManager.getCurrentUser())
    SessionManager.logout()
    assertNull(SessionManager.getCurrentUser())
  }
}
