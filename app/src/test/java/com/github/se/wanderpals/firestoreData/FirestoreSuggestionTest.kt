package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreSuggestionTest {

  @Test
  fun testFromSuggestionConversion() {
    val stop =
        Stop(
            stopId = "stop1",
            title = "Location Name",
            address = "123 Main St",
            time = LocalDate.now(), // Using current date for simplicity
            budget = 100.0,
            description = "Stop Description",
            geoCords = GeoCords(0.0, 0.0),
            website = "http://example.com",
            imageUrl = "http://example.com/image.png")
    val comments =
        listOf(
            Comment(
                commentId = "comment1",
                userId = "user1",
                userName = "User One",
                text = "Great idea!",
                createdAt = LocalDate.now()))
    val suggestion =
        Suggestion(
            suggestionId = "suggestion1",
            userId = "user1",
            userName = "User One",
            text = "Suggestion text",
            createdAt = LocalDate.now(),
            stop = stop,
            comments = comments)

    val firestoreSuggestion = FirestoreSuggestion.fromSuggestion(suggestion)
    assertEquals("suggestion1", firestoreSuggestion.suggestionId)
    assertEquals("Suggestion text", firestoreSuggestion.text)
    assertEquals(firestoreSuggestion.stop.stopId, "stop1")
    assertEquals(firestoreSuggestion.comments.size, 1)
    assertEquals(firestoreSuggestion.comments.first().commentId, "comment1")
  }

  @Test
  fun testToSuggestionConversion() {
    val firestoreStop =
        FirestoreStop(
            stopId = "stop1",
            title = "Location Name",
            address = "123 Main St",
            time = LocalDate.now().toString(),
            budget = 100.0,
            description = "Stop Description",
            geoCords = GeoCords(0.0, 0.0),
            website = "http://example.com",
            imageUrl = "http://example.com/image.png")
    val firestoreComments =
        listOf(
            FirestoreComment(
                commentId = "comment1",
                userId = "user1",
                userName = "User One",
                text = "Great idea!",
                createdAt = LocalDate.now().toString()))
    val firestoreSuggestion =
        FirestoreSuggestion(
            suggestionId = "suggestion1",
            userId = "user1",
            userName = "User One",
            text = "Suggestion text",
            createdAt = LocalDate.now().toString(),
            stop = firestoreStop,
            comments = firestoreComments)

    val suggestion = firestoreSuggestion.toSuggestion()
    assertEquals("suggestion1", suggestion.suggestionId)
    assertEquals("Suggestion text", suggestion.text)
    assertEquals(suggestion.stop.stopId, "stop1")
    assertEquals(suggestion.comments.size, 1)
    assertEquals(suggestion.comments.first().commentId, "comment1")
  }
}
