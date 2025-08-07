package com.cosmicmeta.news.network

import com.cosmicmeta.news.data.NewsItem
import com.cosmicmeta.news.data.RssFeed
import com.cosmicmeta.news.data.toNewsItem
import com.cosmicmeta.news.utils.Logger.logd
import com.cosmicmeta.news.utils.Logger.loge
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.xml.xml


interface NewsApi {
    suspend fun fetchNews(): List<NewsItem>
    suspend fun fetchNews(page: Int, pageSize: Int): List<NewsItem>
}

class KtorNewsApi(
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            xml()
        }
    }
) : NewsApi {
    
    companion object {
        private const val RSS_URL = "https://api.cosmicmeta.ai/rss"
    }
    
    // Cache for all parsed news items
    private var cachedNewsItems: List<NewsItem>? = null
    private var lastFetchTime: Long = 0
    private val cacheValidityMs = 5 * 60 * 1000L // 5 minutes
    
    override suspend fun fetchNews(): List<NewsItem> {
        return fetchAllNewsItems()
    }
    
    override suspend fun fetchNews(page: Int, pageSize: Int): List<NewsItem> {
        val allItems = fetchAllNewsItems()
        val startIndex = page * pageSize
        val endIndex = minOf(startIndex + pageSize, allItems.size)
        
        return if (startIndex < allItems.size) {
            allItems.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }
    
    private suspend fun fetchAllNewsItems(): List<NewsItem> {
        val currentTime = System.currentTimeMillis()
        
        // Return cached items if still valid
        if (cachedNewsItems != null && (currentTime - lastFetchTime) < cacheValidityMs) {
            return cachedNewsItems!!
        }
        
        return try {
            val response = httpClient.get(RSS_URL) {
                header(HttpHeaders.Accept, "application/rss+xml, application/xml, text/xml, */*")
                header(HttpHeaders.UserAgent, "CosmicMetaNews/1.0")
            }
            
            logd("Response status: ${response.status}")
            logd("Response content type: ${response.headers["Content-Type"]}")
            
            val xmlContent = response.bodyAsText()
            logd("Raw XML content length: ${xmlContent.length}")
            
            // Try to parse the XML manually since auto-deserialization isn't working
            val newsItems = parseRssXml(xmlContent)
            logd("Parsed ${newsItems.size} news items")

            // Cache the results
            cachedNewsItems = newsItems
            lastFetchTime = currentTime
            
            newsItems
        } catch (e: Exception) {
            loge("Error fetching news: ${e.message}", e)
            cachedNewsItems ?: emptyList()
        }
    }
    
    private fun parseRssXml(xmlContent: String): List<NewsItem> {
        val items = mutableListOf<NewsItem>()
        
        try {
            // Get channel title for source information
            val channelTitlePattern = Regex("<channel>.*?<title[^>]*>(.*?)</title>", RegexOption.DOT_MATCHES_ALL)
            val channelTitle = channelTitlePattern.find(xmlContent)?.groupValues?.get(1)?.trim() ?: "Unknown Source"
            
            // Simple regex-based parsing since kotlinx.serialization XML isn't working
            val itemPattern = Regex("<item[^>]*>(.*?)</item>", RegexOption.DOT_MATCHES_ALL)
            val sourcePattern = Regex("<source[^>]*>(.*?)</source>", RegexOption.DOT_MATCHES_ALL)
            val titlePattern = Regex("<title[^>]*>(.*?)</title>", RegexOption.DOT_MATCHES_ALL)
            val linkPattern = Regex("<link[^>]*>(.*?)</link>", RegexOption.DOT_MATCHES_ALL)
            val descriptionPattern = Regex("<description[^>]*>(.*?)</description>", RegexOption.DOT_MATCHES_ALL)
            val pubDatePattern = Regex("<pubDate[^>]*>(.*?)</pubDate>", RegexOption.DOT_MATCHES_ALL)
            val guidPattern = Regex("<guid[^>]*>(.*?)</guid>", RegexOption.DOT_MATCHES_ALL)
            val categoryPattern = Regex("<category[^>]*>(.*?)</category>", RegexOption.DOT_MATCHES_ALL)
            val enclosurePattern = Regex("<enclosure[^>]*url=\"([^\"]+)\"[^>]*type=\"image/[^\"]*\"[^>]*/>", RegexOption.DOT_MATCHES_ALL)
            val contentEncodedPattern = Regex("<content:encoded[^>]*>(.*?)</content:encoded>", RegexOption.DOT_MATCHES_ALL)
            
            val itemMatches = itemPattern.findAll(xmlContent)
            
            for (itemMatch in itemMatches) {
                val itemContent = itemMatch.groupValues[1]
                
                val rawTitle = titlePattern.find(itemContent)?.groupValues?.get(1)?.trim() ?: "No Title"
                val link = linkPattern.find(itemContent)?.groupValues?.get(1)?.trim() ?: ""
                val rawDescription = descriptionPattern.find(itemContent)?.groupValues?.get(1)?.trim() ?: ""
                val pubDate = pubDatePattern.find(itemContent)?.groupValues?.get(1)?.trim() ?: ""
                val source = sourcePattern.find(itemContent)?.groupValues?.get(1)?.trim() ?: ""
                val guid = guidPattern.find(itemContent)?.groupValues?.get(1)?.trim() ?: link
                
                // Extract all categories from this item
                val rawCategories = categoryPattern.findAll(itemContent)
                    .map { it.groupValues[1].trim() }
                    .filter { it.isNotEmpty() }
                    .toList()
                
                // Helper function to clean HTML entities and tags
                fun cleanHtmlContent(content: String): String {
                    return content
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&amp;", "&")
                        .replace("&quot;", "\"")
                        .replace("&apos;", "'")
                        .replace("&#39;", "'")
                        .replace("&#x27;", "'")
                        .replace("&nbsp;", " ")
                        .replace(Regex("<[^>]+>"), "") // Remove HTML tags
                        .trim()
                }
                
                // Clean up HTML entities and tags from title
                val cleanTitle = cleanHtmlContent(rawTitle)
                
                // Clean up HTML entities and tags from description
                val cleanDescription = cleanHtmlContent(rawDescription)
                    .take(200) // Limit description length
                
                // Clean up HTML entities and tags from categories
                val cleanCategories = rawCategories.map { cleanHtmlContent(it) }
                    .filter { it.isNotEmpty() }
                
                // Extract image URL - try enclosure first, then content:encoded
                val imageUrl = extractImageUrl(itemContent, enclosurePattern, contentEncodedPattern)
                    
                if (imageUrl != null) {
                    logd("Found image URL: $imageUrl")
                } else {
                    logd("No image found for article: $cleanTitle")
                }
                
                val newsItem = NewsItem(
                    title = cleanTitle,
                    description = cleanDescription,
                    link = link,
                    guid = guid,
                    pubDate = pubDate,
                    categories = cleanCategories,
                    imageUrl = imageUrl,
                    source = if (source.isNotEmpty()) cleanHtmlContent(source) else channelTitle
                )
                
                items.add(newsItem)
            }
            
        } catch (e: Exception) {
            loge("Error parsing RSS XML: ${e.message}", e)
        }
        
        return items
    }
    
    private fun extractImageUrl(
        itemContent: String,
        enclosurePattern: Regex,
        contentEncodedPattern: Regex
    ): String? {
        logd("Extracting image from item content (length: ${itemContent.length})")
        
        // First, try to find image from enclosure tag
        val enclosureMatch = enclosurePattern.find(itemContent)
        if (enclosureMatch != null) {
            val imageUrl = enclosureMatch.groupValues[1].trim()
            logd("Found enclosure image: $imageUrl")
            return imageUrl
        } else {
            logd("No enclosure image found")
        }
        
        // If no enclosure image, try to find first image in content:encoded
        val contentEncodedMatch = contentEncodedPattern.find(itemContent)
        if (contentEncodedMatch != null) {
            val contentEncoded = contentEncodedMatch.groupValues[1]
            logd("Found content:encoded field (length: ${contentEncoded.length})")
            
            // Look for img tags in content:encoded
            val imgPattern = Regex("<img[^>]+src\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>", RegexOption.IGNORE_CASE)
            val imgMatch = imgPattern.find(contentEncoded)
            
            if (imgMatch != null) {
                val imageUrl = imgMatch.groupValues[1].trim()
                logd("Found img tag with src: $imageUrl")
                
                // Decode HTML entities in the URL
                val cleanImageUrl = imageUrl
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&quot;", "\"")
                    .replace("&apos;", "'")
                
                // Basic validation - check if it looks like an image URL
                if (cleanImageUrl.matches(Regex(".*\\.(jpg|jpeg|png|gif|webp|bmp)(\\?.*)?$", RegexOption.IGNORE_CASE))) {
                    logd("Valid image URL: $cleanImageUrl")
                    return cleanImageUrl
                } else {
                    logd("Invalid image URL format: $cleanImageUrl")
                }
            } else {
                logd("No img tags found in content:encoded")
            }
        } else {
            logd("No content:encoded field found")
        }
        
        return null
    }
}
