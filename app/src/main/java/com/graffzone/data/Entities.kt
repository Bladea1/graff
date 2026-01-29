package com.graffzone.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val bio: String = "",
    val avatarUri: String? = null
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: Long,
    val caption: String,
    val tags: String, // comma-separated
    val mediaUri: String? = null,
    val mediaType: String = "NONE", // IMAGE/VIDEO/NONE
    val lat: Double? = null,
    val lon: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val isPublic: Boolean = true
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorId: Long,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorId: Long,
    val title: String,
    val description: String,
    val startTime: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val isPublic: Boolean = true
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val lastMessageAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val sender: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
