package com.cosmicmeta.news.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RssFeed(
    val rss: Rss
)

@Serializable
data class Rss(
    val channel: Channel
)

@Serializable
data class Channel(
    val title: String,
    val description: String,
    val link: String,
    @SerialName("item")
    val items: List<RssItem>
)

@Serializable
data class RssItem(
    val title: String,
    val description: String? = null,
    val link: String,
    val guid: String? = null,
    val pubDate: String,
    val category: String? = null,
    val enclosure: Enclosure? = null
)

@Serializable
data class Enclosure(
    val url: String,
    val type: String,
    val length: String? = null
)

// Extension function to convert RSS items to NewsItems
fun RssItem.toNewsItem(): NewsItem {
    return NewsItem(
        title = title,
        description = description ?: "",
        link = link,
        guid = guid ?: link,
        pubDate = pubDate,
        category = category,
        imageUrl = enclosure?.takeIf { it.type.startsWith("image/") }?.url
    )
}
