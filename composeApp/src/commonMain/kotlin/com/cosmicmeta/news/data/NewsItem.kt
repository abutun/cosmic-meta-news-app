package com.cosmicmeta.news.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class NewsItem(
    val title: String,
    val description: String,
    val link: String,
    val guid: String,
    val pubDate: String,
    val category: String? = null,
    val imageUrl: String? = null,
    val source: String = "Cosmic Meta"
) {
    // Computed property for formatted date
    val formattedDate: String
        get() = try {
            val instant = Instant.parse(pubDate)
            val now = kotlinx.datetime.Clock.System.now()
            val diffInSeconds = (now - instant).inWholeSeconds
            
            when {
                diffInSeconds < 60 -> "Just now"
                diffInSeconds < 3600 -> "${diffInSeconds / 60}m ago"
                diffInSeconds < 86400 -> "${diffInSeconds / 3600}h ago"
                diffInSeconds < 604800 -> "${diffInSeconds / 86400}d ago"
                else -> pubDate.substringBefore("T")
            }
        } catch (e: Exception) {
            pubDate.substringBefore("T")
        }
}
