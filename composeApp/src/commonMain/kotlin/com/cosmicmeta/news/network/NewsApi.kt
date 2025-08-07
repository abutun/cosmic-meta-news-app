package com.cosmicmeta.news.network

import com.cosmicmeta.news.data.NewsItem
import com.cosmicmeta.news.data.RssFeed
import com.cosmicmeta.news.data.toNewsItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.serialization.modules.SerializersModule

interface NewsApi {
    suspend fun fetchNews(): List<NewsItem>
}

class KtorNewsApi(
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            xml(
                serializersModule = SerializersModule {
                    // Add custom serializers if needed
                }
            )
        }
    }
) : NewsApi {
    
    companion object {
        private const val RSS_URL = "https://api.cosmicmeta.ai/rss"
    }
    
    override suspend fun fetchNews(): List<NewsItem> {
        return try {
            val response = httpClient.get(RSS_URL)
            val rssFeed: RssFeed = response.body()
            rssFeed.rss.channel.items.map { it.toNewsItem() }
        } catch (e: Exception) {
            println("Error fetching news: ${e.message}")
            emptyList()
        }
    }
}
