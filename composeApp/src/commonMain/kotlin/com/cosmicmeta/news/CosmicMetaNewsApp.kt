package com.cosmicmeta.news

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cosmicmeta.news.data.NewsItem
import com.cosmicmeta.news.ui.screens.NewsDetailScreen
import com.cosmicmeta.news.ui.screens.NewsListScreen
import com.cosmicmeta.news.ui.screens.SettingsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun CosmicMetaNewsApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Store selected news item in composable state
    val selectedNewsItem = remember { mutableStateOf<NewsItem?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = "news_list"
    ) {
        composable("news_list") {
            NewsListScreen(
                onNewsItemClick = { newsItem ->
                    // Store the news item in state instead of URL
                    selectedNewsItem.value = newsItem
                    navController.navigate("news_detail")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("news_detail") {
            val newsItem = selectedNewsItem.value
            if (newsItem != null) {
                NewsDetailScreen(
                    newsItem = newsItem,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable("settings") {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
