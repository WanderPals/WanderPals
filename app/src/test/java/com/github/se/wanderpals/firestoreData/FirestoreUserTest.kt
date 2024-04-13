package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FirestoreUserTest {

  @Test
  fun testFromUserToFirestoreUser() {
    val user =
        User(
            userId = "123",
            name = "John Doe",
            email = "john@example.com",
            nickname = "Johnny",
            role = Role.ADMIN,
            lastPosition = GeoCords(40.7128, -74.0060),
            profilePictureURL = "http://example.com/image.jpg")

    val firestoreUser = FirestoreUser.fromUser(user)

    assertEquals(user.userId, firestoreUser.userId)
    assertEquals(user.name, firestoreUser.name)
    assertEquals(user.email, firestoreUser.email)
    assertEquals(user.nickname, firestoreUser.nickname)
    assertEquals(user.role.name, firestoreUser.role)
    assertEquals(
        "${user.lastPosition.latitude},${user.lastPosition.longitude}",
        "${firestoreUser.lastPosition.latitude},${firestoreUser.lastPosition.longitude}")
    assertEquals(user.profilePictureURL, firestoreUser.profilePictureURL)
  }

  @Test
  fun testFromFirestoreUserToUser() {
    val firestoreUser =
        FirestoreUser(
            userId = "123",
            name = "John Doe",
            email = "john@example.com",
            nickname = "Johnny",
            role = "ADMIN",
            lastPosition = GeoCords(40.7128, -74.0060),
            profilePictureURL = "http://example.com/image.jpg")

    val user = firestoreUser.toUser()

    assertEquals(firestoreUser.userId, user.userId)
    assertEquals(firestoreUser.name, user.name)
    assertEquals(firestoreUser.email, user.email)
    assertEquals(firestoreUser.nickname, user.nickname)
    assertEquals(Role.valueOf(firestoreUser.role), user.role)
    assertEquals(firestoreUser.lastPosition, user.lastPosition)
    assertEquals(firestoreUser.profilePictureURL, user.profilePictureURL)
  }
}
