package com.cosmicmeta.news.di

import com.cosmicmeta.news.network.KtorNewsApi
import com.cosmicmeta.news.network.NewsApi
import com.cosmicmeta.news.repository.NewsRepository
import com.cosmicmeta.news.repository.NewsRepositoryImpl
import com.cosmicmeta.news.ui.viewmodel.NewsListViewModel
import com.cosmicmeta.news.ui.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Network
    single<NewsApi> { KtorNewsApi() }
    
    // Repository
    single<NewsRepository> { NewsRepositoryImpl(get()) }
    
    // ViewModels
    viewModel { NewsListViewModel(get()) }
    viewModel { SettingsViewModel() }
}
