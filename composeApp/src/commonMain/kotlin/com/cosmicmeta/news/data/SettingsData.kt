package com.cosmicmeta.news.data

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val enableNotifications: Boolean = true,
    val enabledCategories: Set<String> = setOf(
        "Technology",
        "Science", 
        "Business",
        "Health",
        "Entertainment"
    ),
    val notificationFrequency: NotificationFrequency = NotificationFrequency.HOURLY
)

enum class NotificationFrequency(val displayName: String, val intervalMinutes: Int) {
    REALTIME("Real-time", 0),
    EVERY_15_MIN("Every 15 minutes", 15),
    HOURLY("Hourly", 60),
    TWICE_DAILY("Twice daily", 720),
    DAILY("Daily", 1440)
}
