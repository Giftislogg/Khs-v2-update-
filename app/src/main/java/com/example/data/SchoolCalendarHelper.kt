package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SchoolCalendarHelper {
    // Standard SA School Term 2, 2026 (approx April 8, 2026 to June 26, 2026)
    private const val TERM_START_DATE = "2026-04-08"
    private const val TERM_END_DATE = "2026-06-26"

    data class TermProgressInfo(
        val daysElapsed: Int,
        val totalDays: Int,
        val progressPercentage: Float,
        val label: String
    )

    fun getTerm2Progress(currentDateString: String = "2026-05-31"): TermProgressInfo {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val start = sdf.parse(TERM_START_DATE) ?: Date()
            val end = sdf.parse(TERM_END_DATE) ?: Date()
            val current = sdf.parse(currentDateString) ?: Date()

            val totalDiff = end.time - start.time
            val totalDays = (totalDiff / (1000 * 60 * 60 * 24)).toInt()

            val elapsedDiff = current.time - start.time
            var elapsedDays = (elapsedDiff / (1000 * 60 * 60 * 24)).toInt()

            if (elapsedDays < 0) elapsedDays = 0
            if (elapsedDays > totalDays) elapsedDays = totalDays

            val progress = (elapsedDays.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)

            // Calculate weeks left
            val remainingDiff = end.time - current.time
            val remainingDays = (remainingDiff / (1000 * 60 * 60 * 24)).toInt()
            val weeksLeft = (remainingDays / 7).coerceAtLeast(0)

            val label = if (weeksLeft > 0) {
                "Term 2 Progress • $weeksLeft weeks left"
            } else {
                "Term 2 Progress • Under 1 week remaining"
            }

            return TermProgressInfo(
                daysElapsed = elapsedDays,
                totalDays = totalDays,
                progressPercentage = progress,
                label = label
            )
        } catch (e: Exception) {
            return TermProgressInfo(53, 79, 0.67f, "Term 2 Progress • 3 weeks left")
        }
    }
}
