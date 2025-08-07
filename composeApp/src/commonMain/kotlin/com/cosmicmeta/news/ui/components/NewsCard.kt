package com.cosmicmeta.news.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import coil3.compose.AsyncImage
import androidx.compose.material3.CircularProgressIndicator

import com.cosmicmeta.news.data.NewsItem
import com.cosmicmeta.news.utils.Logger.logd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Image (if available)
            newsItem.imageUrl?.let { imageUrl ->
                logd("NewsCard displaying image: $imageUrl")
                
                var showLoading by remember { mutableStateOf(true) }
                var hasError by remember { mutableStateOf(false) }
                
                // Auto-hide loading after 10 seconds to prevent infinite loading
                LaunchedEffect(imageUrl) {
                    delay(10000) // 10 seconds timeout
                    if (showLoading) {
                        logd("Image loading timeout for: $imageUrl")
                        showLoading = false
                        hasError = true
                    }
                }
                
                Box {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = newsItem.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        onLoading = {
                            logd("AsyncImage loading: $imageUrl")
                            showLoading = true
                            hasError = false
                        },
                        onError = { errorState ->
                            logd("AsyncImage error loading: $imageUrl")
                            logd("Error details: ${errorState.result.throwable}")
                            showLoading = false
                            hasError = true
                        },
                        onSuccess = {
                            logd("AsyncImage successfully loaded: $imageUrl")
                            showLoading = false
                            hasError = false
                        }
                    )
                    
                    // Show loading or error state
                    if (showLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else if (hasError) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📷",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            } ?: run {
                logd("NewsCard: No image URL for article '${newsItem.title}'")
            }
            
            // Title
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = newsItem.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bottom row with source and category/date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source on the left
                Text(
                    text = newsItem.source,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                
                // Category and date on the right
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Category
                    newsItem.category?.let { category ->
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Publication date
                    Text(
                        text = newsItem.formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
