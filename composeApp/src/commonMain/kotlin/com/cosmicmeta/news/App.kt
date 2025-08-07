package com.cosmicmeta.news

import androidx.compose.runtime.Composable
import com.cosmicmeta.news.di.appModule
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        CosmicMetaNewsApp()
    }
}