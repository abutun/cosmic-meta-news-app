package com.cosmicmeta.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cosmicmeta.news.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinApplication(application = {
                androidContext(this@MainActivity)
                modules(
                    appModule
                )
            }) {
                CosmicMetaNewsApp()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
