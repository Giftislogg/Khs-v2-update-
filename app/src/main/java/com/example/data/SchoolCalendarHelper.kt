package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SchoolCalendarHelper {

    data class TermInfo(
        val termNumber: Int,
        val startDate: String, // "yyyy-MM-dd"
        val endDate: String,   // "yyyy-MM-dd"
        val displayName: String
    )

    val SA_TERMS_2026 = listOf(
        TermInfo(1, "2026-01-14", "2026-03-27", "School Term 1"),
        TermInfo(2, "2026-04-08", "2026-06-26", "School Term 2"),
        TermInfo(3, "2026-07-21", "2026-09-25", "School Term 3"),
        TermInfo(4, "2026-10-13", "2026-12-11", "School Term 4")
    )

    data class TermProgressInfo(
        val daysElapsed: Int,
        val totalDays: Int,
        val progressPercentage: Float,
        val label: String,
        val termNumber: Int,
        val endDateFormatted: String,
        val displayDate: String
    )

    // Maintaining legacy signature for simple fallbacks
    fun getTerm2Progress(currentDateString: String = "2026-05-31"): TermProgressInfo {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = try {
            sdf.parse(currentDateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        val term2 = SA_TERMS_2026[1] // Default Term 2
        return calculateProgressForDate(term2, date)
    }

    fun getActiveTermOrUpcoming(date: Date): TermInfo {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        // 1. Is it inside a term?
        for (term in SA_TERMS_2026) {
            val start = sdf.parse(term.startDate) ?: continue
            val end = sdf.parse(term.endDate) ?: continue
            if (date.time >= start.time && date.time <= end.time) {
                return term
            }
        }
        
        // 2. Is it before a term (in holiday before term)?
        for (term in SA_TERMS_2026) {
            val start = sdf.parse(term.startDate) ?: continue
            if (date.time < start.time) {
                return term
            }
        }
        
        // 3. Fallback to last term of the year
        return SA_TERMS_2026.last()
    }

    fun calculateProgressForDate(term: TermInfo, date: Date): TermProgressInfo {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val start = sdf.parse(term.startDate) ?: Date()
            val end = sdf.parse(term.endDate) ?: Date()

            val totalDiff = end.time - start.time
            val totalDays = (totalDiff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)

            val elapsedDiff = date.time - start.time
            var elapsedDays = (elapsedDiff / (1000 * 60 * 60 * 24)).toInt()

            if (elapsedDays < 0) elapsedDays = 0
            if (elapsedDays > totalDays) elapsedDays = totalDays

            val progress = (elapsedDays.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)

            // Weeks or days remaining
            val remainingDiff = end.time - date.time
            val remainingDays = (remainingDiff / (1000 * 60 * 60 * 24)).toInt()
            val weeksLeft = (remainingDays / 7).coerceAtLeast(0)

            val label = if (weeksLeft > 0) {
                "Term ${term.termNumber} Progress • $weeksLeft weeks left"
            } else if (remainingDays > 0) {
                "Term ${term.termNumber} Progress • $remainingDays days left"
            } else {
                "Term ${term.termNumber} Progress Completed 🎉"
            }

            val displayFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US)
            val formattedEnd = displayFormat.format(end)

            val displayDate = SimpleDateFormat("d MMMM yyyy", Locale.US).format(date)

            return TermProgressInfo(
                daysElapsed = elapsedDays,
                totalDays = totalDays,
                progressPercentage = progress,
                label = label,
                termNumber = term.termNumber,
                endDateFormatted = formattedEnd,
                displayDate = displayDate
            )
        } catch (e: Exception) {
            return TermProgressInfo(
                daysElapsed = 50,
                totalDays = 79,
                progressPercentage = 0.63f,
                label = "Term 2 Progress • 4 weeks left",
                termNumber = 2,
                endDateFormatted = "Friday, 26 June 2026",
                displayDate = SimpleDateFormat("d MMMM yyyy", Locale.US).format(date)
            )
        }
    }
}
