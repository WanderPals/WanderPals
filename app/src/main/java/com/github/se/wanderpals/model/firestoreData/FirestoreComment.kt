package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Comment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A Firestore-compatible representation of a user-generated comment. This class adapts the
 * Comment data model for Firestore by converting LocalDate fields to String.
 * It facilitates storing and retrieving comment data from Firestore with methods for conversion
 * between the FirestoreComment DTO and the Comment domain model.
 *
 * @param commentId Unique identifier of the comment.
 * @param userId ID of the user who posted the comment.
 * @param userName Name of the user, displayed alongside the comment.
 * @param text Content of the comment.
 * @param createdAt Date the comment was made, in ISO-8601 format (yyyy-MM-dd).
 */
data class FirestoreComment(
    val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: String = ""
){
    companion object {
        /**
         * Converts a domain model Comment to a FirestoreComment DTO.
         *
         * @param comment The Comment object to convert.
         * @return A FirestoreComment DTO with the date converted to String format.
         */
        fun fromComment(comment: Comment):FirestoreComment{
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            return FirestoreComment(
                commentId = comment.commentId,
                userId = comment.userId,
                userName = comment.userName,
                text = comment.text,
                createdAt = comment.createdAt.format(formatter)
            )
        }
    }
    /**
     * Converts this FirestoreComment DTO back into a domain model Comment.
     *
     * @return A Comment object with LocalDate fields parsed from String.
     */
    fun toComment(): Comment {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return Comment(
            commentId = commentId,
            userId = userId,
            userName = userName,
            text = text,
            createdAt = LocalDate.parse(createdAt, formatter)
        )
    }

}
