package com.cosmicmeta.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

data class SimpleNewsItem(
    val title: String,
    val description: String,
    val source: String = "Cosmic Meta"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SimpleApp() {
    val sampleNews = remember {
        listOf(
            SimpleNewsItem(
                "Welcome to Cosmic Meta News",
                "This is a simple news app built with Kotlin Multiplatform and Compose Multiplatform."
            ),
            SimpleNewsItem(
                "Technology Updates",
                "Stay updated with the latest technology news and developments."
            ),
            SimpleNewsItem(
                "Science Discoveries",
                "Explore recent scientific breakthroughs and research findings."
            )
        )
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cosmic Meta News") }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleNews) { newsItem ->
                    SimpleNewsCard(newsItem)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleNewsCard(newsItem: SimpleNewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = newsItem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = newsItem.source,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
