package com.cosmicmeta.news.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class NewsItem(
    val title: String,
    val description: String,
    val link: String,
    val guid: String,
    val pubDate: String,
    val categories: List<String> = emptyList(),
    val imageUrl: String? = null,
    val source: String = "Unknown Source"
) {
    // Get the first category for display
    val category: String?
        get() = categories.firstOrNull()
    
    // Computed property for formatted date
    val formattedDate: String
        get() = try {
            // RSS feeds typically use RFC 822 format: "Thu, 07 Aug 2025 18:57:40 +0300"
            // We'll extract a readable date from this format
            parseRssDate(pubDate)
        } catch (e: Exception) {
            // Fallback: just return the original date string, cleaned up
            pubDate.substringBefore(" +").substringBefore(" -").trim()
        }
        
    private fun parseRssDate(rssDate: String): String {
        try {
            // Extract date parts from RFC 822 format
            // Example: "Thu, 07 Aug 2025 18:57:40 +0300"
            val parts = rssDate.split(" ")
            if (parts.size >= 4) {
                val day = parts[1]
                val month = parts[2]
                val year = parts[3]
                val time = parts.getOrNull(4)?.substringBefore("+")?.substringBefore("-")
                
                // Convert month name to number for comparison
                val monthNumber = when (month.lowercase()) {
                    "jan" -> 1; "feb" -> 2; "mar" -> 3; "apr" -> 4
                    "may" -> 5; "jun" -> 6; "jul" -> 7; "aug" -> 8
                    "sep" -> 9; "oct" -> 10; "nov" -> 11; "dec" -> 12
                    else -> 0
                }
                
                // Simple relative time - just check if it's today or recent
                val now = kotlinx.datetime.Clock.System.now()
                val currentYear = now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).year
                val currentMonth = now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).monthNumber
                val currentDay = now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).dayOfMonth
                
                return when {
                    year.toIntOrNull() == currentYear && monthNumber == currentMonth && day.toIntOrNull() == currentDay -> {
                        time?.let { "Today ${it.substringBeforeLast(":")}" } ?: "Today"
                    }
                    year.toIntOrNull() == currentYear && monthNumber == currentMonth && (currentDay - (day.toIntOrNull() ?: 0)) == 1 -> {
                        "Yesterday"
                    }
                    year.toIntOrNull() == currentYear -> {
                        "$day $month"
                    }
                    else -> {
                        "$day $month $year"
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback to original processing
        }
        
        // Final fallback: return cleaned original
        return rssDate.substringBefore(" +").substringBefore(" -").trim()
    }
}
