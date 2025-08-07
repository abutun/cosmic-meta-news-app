package com.cosmicmeta.news

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cosmicmeta.news.data.NewsItem
import com.cosmicmeta.news.ui.screens.NewsDetailScreen
import com.cosmicmeta.news.ui.screens.NewsListScreen
import com.cosmicmeta.news.ui.screens.SettingsScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    NavHost(
        navController = navController,
        startDestination = "news_list"
    ) {
        composable("news_list") {
            NewsListScreen(
                onNewsItemClick = { newsItem ->
                    // Serialize the news item to pass it via navigation
                    val newsItemJson = Json.encodeToString(newsItem)
                    navController.navigate("news_detail/$newsItemJson")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("news_detail/{newsItemJson}") { backStackEntry ->
            val newsItemJson = backStackEntry.arguments?.getString("newsItemJson")
            if (newsItemJson != null) {
                val newsItem = Json.decodeFromString<NewsItem>(newsItemJson)
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
