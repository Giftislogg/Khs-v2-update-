package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Announcement::class,
        Event::class,
        Post::class,
        PostComment::class,
        PostLike::class,
        Timetable::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun announcementDao(): AnnouncementDao
    abstract fun eventDao(): EventDao
    abstract fun postDao(): PostDao
    abstract fun postCommentDao(): PostCommentDao
    abstract fun postLikeDao(): PostLikeDao
    abstract fun timetableDao(): TimetableDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "khanyisa_school_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: AppDatabase) {
            val announcementDao = db.announcementDao()
            val eventDao = db.eventDao()
            val postDao = db.postDao()
            val timetableDao = db.timetableDao()

            // 1. Announcements
            announcementDao.insertAll(
                listOf(
                    Announcement(
                        title = "Third Term Midterm Examinations Schedule",
                        content = "Midterm tests will commence on Wednesday, 10th June 2026. Learners are urged to clear all library balances and check their respective class notice boards for seat assignments.",
                        category = "Academics",
                        createdAt = "2026-05-31",
                        isRead = false
                    ),
                    Announcement(
                        title = "Khanyisa Winter Welfare Program - Food Drive",
                        content = "Please send any spare non-perishable tinned food, old jackets, scarves, or blankets with pupils tomorrow morning. They can drop off all donations at the Student Union Portal office.",
                        category = "Community",
                        createdAt = "2026-05-30",
                        isRead = false
                    ),
                    Announcement(
                        title = "Winter Sports Dress Code Policy Update",
                        content = "Due to frosty early mornings, learners representing the school in track activities are mandated to dress in their registered tracksuit bottoms and school windbreaker over regular sportswear.",
                        category = "Sports",
                        createdAt = "2026-05-28",
                        isRead = false
                    )
                )
            )

            // 2. Events
            eventDao.insertAll(
                listOf(
                    Event(
                        title = "Khanyisa Athletic District Trials",
                        description = "Support our athletic teams competing in the annual district trials. Track, high jump, shotput and relay teams represent Khanyisa with pride.",
                        eventDate = "2026-06-12 09:00",
                        location = "Khanyisa Sports Complex",
                        imageUrls = "file:///android_asset/supabase/6.jpg,file:///android_asset/supabase/images_3.jpeg"
                    ),
                    Event(
                        title = "Science and Tech Exhibition 2026",
                        description = "Pupils display their innovative science and electronics projects in the physics laboratories. Interactive demonstration stalls open to families.",
                        eventDate = "2026-06-18 10:00",
                        location = "School Science Labs",
                        imageUrls = "file:///android_asset/supabase/unnamed.jpg"
                    ),
                    Event(
                        title = "Khanyisa High Music Festival Night",
                        description = "A celebratory musical evening featuring classical symphonies, local youth choir acts, acoustic bands, and instrumental ensembles under golden twilight lights.",
                        eventDate = "2026-06-25 18:30",
                        location = "School Drama Hall",
                        imageUrls = "file:///android_asset/supabase/unnamed_1.jpg"
                    )
                )
            )

            // 3. Posts (News Feed)
            postDao.insertAll(
                listOf(
                    Post(
                        title = "School Winter Food Drive starts tomorrow!",
                        content = "We are soliciting canned meals, warm winter beanies, and blankets to aid the local shelter centers. Please turn in all donations to your homeroom learner representatives by Friday afternoon. Let's make an impact!",
                        category = "Community",
                        imageUrls = "file:///android_asset/supabase/images.jpeg"
                    ),
                    Post(
                        title = "Matric Dance 2026 theme announcement coming soon",
                        content = "The student governance assembly has narrowed down the dance decor to three elegant candidates: Midnight Masquerade, Golden Twilight, and Celestial Radiance. Look out for votes next week!",
                        category = "Social",
                        imageUrls = "file:///android_asset/supabase/2.jpg"
                    )
                )
            )

            // 4. Timetables
            timetableDao.insertAll(
                listOf(
                    Timetable(
                        title = "Grade 12A Official Examination Timetable",
                        type = "exam",
                        grade = "12A",
                        imageUrl = "file:///android_asset/supabase/images_3.jpeg"
                    ),
                    Timetable(
                        title = "Grade 11B Term 2 Weekly Academic Schedule",
                        type = "weekly",
                        grade = "11B",
                        imageUrl = "file:///android_asset/supabase/2.jpg"
                    ),
                    Timetable(
                        title = "Grade 8A Weekly Academic Class Schedule",
                        type = "weekly",
                        grade = "8A",
                        imageUrl = "file:///android_asset/supabase/unnamed.jpg"
                    )
                )
            )
        }
    }
}
