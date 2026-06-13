package com.example.data

import kotlinx.coroutines.flow.Flow

class SchoolRepository(
    private val announcementDao: AnnouncementDao,
    private val eventDao: EventDao,
    private val postDao: PostDao,
    private val postCommentDao: PostCommentDao,
    private val postLikeDao: PostLikeDao,
    private val timetableDao: TimetableDao
) {
    val announcements: Flow<List<Announcement>> = announcementDao.getAll()
    val events: Flow<List<Event>> = eventDao.getAll()
    val posts: Flow<List<Post>> = postDao.getAll()
    val timetables: Flow<List<Timetable>> = timetableDao.getAll()

    // Announcements
    suspend fun insertAnnouncement(announcement: Announcement) = announcementDao.insert(announcement)
    suspend fun insertAnnouncements(list: List<Announcement>) = announcementDao.insertAll(list)
    suspend fun markAnnouncementAsRead(id: Long) = announcementDao.markAsRead(id)
    suspend fun deleteAnnouncement(announcement: Announcement) = announcementDao.delete(announcement)

    // Events
    suspend fun insertEvent(event: Event) = eventDao.insert(event)
    suspend fun insertEvents(list: List<Event>) = eventDao.insertAll(list)
    suspend fun deleteEvent(event: Event) = eventDao.delete(event)

    // Posts
    suspend fun insertPost(post: Post) = postDao.insert(post)
    suspend fun insertPosts(list: List<Post>) = postDao.insertAll(list)
    suspend fun deletePost(post: Post) = postDao.delete(post)

    // Comments
    fun getCommentsForPost(postId: Long): Flow<List<PostComment>> = postCommentDao.getCommentsForPost(postId)
    suspend fun insertComment(comment: PostComment) = postCommentDao.insert(comment)
    suspend fun deleteComment(comment: PostComment) = postCommentDao.delete(comment)

    // Likes
    fun getLikesForPost(postId: Long): Flow<List<PostLike>> = postLikeDao.getLikesForPost(postId)
    suspend fun insertLike(like: PostLike) = postLikeDao.insert(like)
    suspend fun removeLike(postId: Long, clientId: String, reactionType: String) = postLikeDao.removeLike(postId, clientId, reactionType)

    // Timetables
    suspend fun insertTimetable(timetable: Timetable) = timetableDao.insert(timetable)
    suspend fun insertTimetables(list: List<Timetable>) = timetableDao.insertAll(list)
    suspend fun deleteTimetable(timetable: Timetable) = timetableDao.delete(timetable)
}
