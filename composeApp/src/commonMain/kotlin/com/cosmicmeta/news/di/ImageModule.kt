package com.cosmicmeta.news.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import io.ktor.client.HttpClient
import org.koin.dsl.module

val imageModule = module {
    single<ImageLoader> {
        ImageLoader.Builder(get<PlatformContext>())
            .crossfade(true)
            .build()
    }
}
