package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Announcement
import com.example.data.Event
import com.example.data.Post
import com.example.data.PostComment
import com.example.data.PostLike
import com.example.data.SchoolRepository
import com.example.data.Timetable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SchoolViewModel(private val repository: SchoolRepository) : ViewModel() {

    val announcements: StateFlow<List<Announcement>> = repository.announcements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<Event>> = repository.events
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<Post>> = repository.posts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timetables: StateFlow<List<Timetable>> = repository.timetables
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientId: String = UUID.randomUUID().toString()

    // Comments
    fun getComments(postId: Long): Flow<List<PostComment>> {
        return repository.getCommentsForPost(postId)
    }

    fun addComment(postId: Long, author: String, content: String) {
        viewModelScope.launch {
            repository.insertComment(
                PostComment(
                    postId = postId,
                    author = author,
                    content = content
                )
            )
        }
    }

    fun deleteComment(comment: PostComment) {
        viewModelScope.launch {
            repository.deleteComment(comment)
        }
    }

    // Likes
    fun getLikes(postId: Long): Flow<List<PostLike>> {
        return repository.getLikesForPost(postId)
    }

    fun toggleReaction(postId: Long, reactionType: String) {
        viewModelScope.launch {
            val currentLikes = repository.getLikesForPost(postId).first()
            val existingReaction = currentLikes.find { it.clientId == clientId && it.reactionType == reactionType }
            if (existingReaction != null) {
                repository.removeLike(postId, clientId, reactionType)
            } else {
                repository.insertLike(
                    PostLike(
                        postId = postId,
                        clientId = clientId,
                        reactionType = reactionType
                    )
                )
            }
        }
    }

    fun toggleLike(postId: Long) {
        toggleReaction(postId, "like")
    }

    // Admin/Manage Functions
    fun addAnnouncement(title: String, content: String, category: String = "general", id: Long = 0) {
        viewModelScope.launch {
            repository.insertAnnouncement(
                Announcement(
                    id = id,
                    title = title,
                    content = content,
                    category = category
                )
            )
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }

    fun addEvent(title: String, description: String, date: String, location: String, imageUrls: String = "", id: Long = 0) {
        viewModelScope.launch {
            repository.insertEvent(
                Event(
                    id = id,
                    title = title,
                    description = description,
                    eventDate = date,
                    location = location,
                    imageUrls = imageUrls
                )
            )
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun addPost(title: String, content: String, category: String, imageUrls: String = "", id: Long = 0) {
        viewModelScope.launch {
            repository.insertPost(
                Post(
                    id = id,
                    title = title,
                    content = content,
                    category = category,
                    imageUrls = imageUrls
                )
            )
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }

    fun addTimetable(title: String, type: String, grade: String, imageUrl: String, id: Long = 0) {
        viewModelScope.launch {
            repository.insertTimetable(
                Timetable(
                    id = id,
                    title = title,
                    type = type,
                    grade = grade,
                    imageUrl = imageUrl
                )
            )
        }
    }

    fun deleteTimetable(timetable: Timetable) {
        viewModelScope.launch {
            repository.deleteTimetable(timetable)
        }
    }
}

class SchoolViewModelFactory(private val repository: SchoolRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchoolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SchoolViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
