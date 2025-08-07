package com.cosmicmeta.news.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cosmicmeta.news.data.NewsItem
import com.cosmicmeta.news.repository.NewsRepository
import com.cosmicmeta.news.utils.Logger.logd
import com.cosmicmeta.news.utils.Logger.loge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsListViewModel(
    private val repository: NewsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NewsListUiState())
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()
    
    private var currentPage = 0
    private val pageSize = 10
    private var hasMorePages = true
    
    init {
        loadInitialNews()
    }
    
    fun loadInitialNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                currentPage = 0
                hasMorePages = true
                val newsItems = repository.getNewsPage(currentPage, pageSize)
                _uiState.value = _uiState.value.copy(
                    news = newsItems,
                    isLoading = false,
                    error = null,
                    hasMorePages = newsItems.size == pageSize
                )
                logd("Loaded initial page: ${newsItems.size} items")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun loadMoreNews() {
        if (!hasMorePages || _uiState.value.isLoadingMore) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            try {
                currentPage++
                val newItems = repository.getNewsPage(currentPage, pageSize)
                val currentNews = _uiState.value.news
                
                _uiState.value = _uiState.value.copy(
                    news = currentNews + newItems,
                    isLoadingMore = false,
                    hasMorePages = newItems.size == pageSize
                )
                
                if (newItems.size < pageSize) {
                    hasMorePages = false
                }
                
                logd("Loaded page $currentPage: ${newItems.size} new items, total: ${_uiState.value.news.size}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    error = e.message ?: "Failed to load more news"
                )
                currentPage-- // Revert page increment on error
            }
        }
    }
    
    fun refreshNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                // Clear cache and reset pagination
                currentPage = 0
                hasMorePages = true
                val freshNews = repository.getNewsPage(currentPage, pageSize)
                _uiState.value = _uiState.value.copy(
                    news = freshNews,
                    isRefreshing = false,
                    error = null,
                    hasMorePages = freshNews.size == pageSize
                )
                logd("Refreshed news: ${freshNews.size} items")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "Failed to refresh news"
                )
            }
        }
    }
}

data class NewsListUiState(
    val news: List<NewsItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val error: String? = null
)
