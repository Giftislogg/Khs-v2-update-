package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "general",
    val createdAt: String = "2026-05-31",
    val isRead: Boolean = false
)

@Serializable
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val eventDate: String,
    val location: String,
    val imageUrls: String = "" // comma-separated of images or asset file paths
)

@Serializable
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,
    val imageUrls: String = "" // comma-separated of images or asset file paths
)

@Serializable
@Entity(tableName = "post_comments")
data class PostComment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val author: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "post_likes")
data class PostLike(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val clientId: String,
    val reactionType: String = "like" // "like" or "heart"
)

@Serializable
@Entity(tableName = "timetables")
data class Timetable(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // "weekly" or "exam"
    val grade: String, // "8A", "8B", etc.
    val imageUrl: String
)
