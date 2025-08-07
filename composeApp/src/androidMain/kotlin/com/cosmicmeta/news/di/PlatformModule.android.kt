package com.cosmicmeta.news.di

import android.content.Context
import coil3.PlatformContext
import org.koin.dsl.module

val platformModule = module {
    single<PlatformContext> { get<Context>() }
}
