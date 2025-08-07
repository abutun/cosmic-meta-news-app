package com.cosmicmeta.news.utils

/**
 * Simple logger utility that logs only in debug mode
 */
object Logger {
    
    // Set to false for production builds
    private const val IS_DEBUG = true
    
    fun d(tag: String, message: String) {
        if (IS_DEBUG) {
            println("DEBUG/$tag: $message")
        }
    }
    
    fun i(tag: String, message: String) {
        if (IS_DEBUG) {
            println("INFO/$tag: $message")
        }
    }
    
    fun w(tag: String, message: String) {
        if (IS_DEBUG) {
            println("WARN/$tag: $message")
        }
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (IS_DEBUG) {
            println("ERROR/$tag: $message")
            throwable?.let {
                it.printStackTrace()
            }
        }
    }
    
    // Extension functions for easier usage
    inline fun <reified T> T.logd(message: String) {
        d(T::class.simpleName ?: "Unknown", message)
    }
    
    inline fun <reified T> T.logi(message: String) {
        i(T::class.simpleName ?: "Unknown", message)
    }
    
    inline fun <reified T> T.logw(message: String) {
        w(T::class.simpleName ?: "Unknown", message)
    }
    
    inline fun <reified T> T.loge(message: String, throwable: Throwable? = null) {
        e(T::class.simpleName ?: "Unknown", message, throwable)
    }
}
