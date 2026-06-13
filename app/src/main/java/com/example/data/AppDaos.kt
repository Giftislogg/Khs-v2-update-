package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAll(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(announcement: Announcement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Announcement>)

    @Query("UPDATE announcements SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Delete
    suspend fun delete(announcement: Announcement)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY eventDate ASC")
    fun getAll(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Event>)

    @Delete
    suspend fun delete(event: Event)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAll(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Post>)

    @Delete
    suspend fun delete(post: Post)
}

@Dao
interface PostCommentDao {
    @Query("SELECT * FROM post_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<PostComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: PostComment)

    @Delete
    suspend fun delete(comment: PostComment)
}

@Dao
interface PostLikeDao {
    @Query("SELECT * FROM post_likes WHERE postId = :postId")
    fun getLikesForPost(postId: Long): Flow<List<PostLike>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(like: PostLike)

    @Query("DELETE FROM post_likes WHERE postId = :postId AND clientId = :clientId AND reactionType = :reactionType")
    suspend fun removeLike(postId: Long, clientId: String, reactionType: String)
}

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetables ORDER BY id DESC")
    fun getAll(): Flow<List<Timetable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timetable: Timetable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Timetable>)

    @Delete
    suspend fun delete(timetable: Timetable)
}
