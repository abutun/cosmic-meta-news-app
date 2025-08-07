package com.cosmicmeta.news.repository

import com.cosmicmeta.news.data.NewsItem
import com.cosmicmeta.news.network.NewsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NewsRepository {
    fun getNews(): Flow<List<NewsItem>>
    suspend fun refreshNews(): List<NewsItem>
}

class NewsRepositoryImpl(
    private val api: NewsApi
) : NewsRepository {
    
    private var cachedNews: List<NewsItem> = emptyList()
    
    override fun getNews(): Flow<List<NewsItem>> = flow {
        if (cachedNews.isEmpty()) {
            cachedNews = api.fetchNews()
        }
        emit(cachedNews)
    }
    
    override suspend fun refreshNews(): List<NewsItem> {
        cachedNews = api.fetchNews()
        return cachedNews
    }
}
