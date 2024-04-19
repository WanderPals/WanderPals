package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Comment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreCommentTest {

    @Test
    fun testFromCommentConversion() {
        val comment = Comment(
            commentId = "comment123",
            userId = "user456",
            userName = "Jane Doe",
            text = "This is a comment.",
            createdAt = LocalDate.of(2024, 5, 2),
            createdAtTime = LocalTime.of(14, 30))  // Example time for testing

        val firestoreComment = FirestoreComment.fromComment(comment)
        assertEquals("comment123", firestoreComment.commentId)
        assertEquals("user456", firestoreComment.userId)
        assertEquals("Jane Doe", firestoreComment.userName)
        assertEquals("This is a comment.", firestoreComment.text)
        assertEquals("2024-05-02", firestoreComment.createdAt)
        assertEquals("14:30:00", firestoreComment.createdAtTime)  // Verifying the correct string format of LocalTime
    }

    @Test
    fun testToCommentConversion() {
        val firestoreComment = FirestoreComment(
            commentId = "comment123",
            userId = "user456",
            userName = "Jane Doe",
            text = "This is a comment.",
            createdAt = "2024-05-02",
            createdAtTime = "14:30:00")  // ISO formatted time as string

        val comment = firestoreComment.toComment()
        assertEquals("comment123", comment.commentId)
        assertEquals("user456", comment.userId)
        assertEquals("Jane Doe", comment.userName)
        assertEquals("This is a comment.", comment.text)
        assertEquals(LocalDate.of(2024, 5, 2), comment.createdAt)
        assertEquals(LocalTime.of(14, 30), comment.createdAtTime)  // Ensure LocalTime is correctly parsed
    }
}
