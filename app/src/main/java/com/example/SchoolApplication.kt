package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.SchoolRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SchoolApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy {
        SchoolRepository(
            database.announcementDao(),
            database.eventDao(),
            database.postDao(),
            database.postCommentDao(),
            database.postLikeDao(),
            database.timetableDao()
        )
    }
}
