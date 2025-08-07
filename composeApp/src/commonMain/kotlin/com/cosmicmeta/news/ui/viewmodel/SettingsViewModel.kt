package com.cosmicmeta.news.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cosmicmeta.news.data.NotificationFrequency
import com.cosmicmeta.news.data.NotificationSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        // Load saved settings (in a real app, this would come from local storage)
        loadSettings()
    }
    
    private fun loadSettings() {
        // For now, use default settings
        // In a real app, load from SharedPreferences/UserDefaults
        _uiState.value = _uiState.value.copy(
            settings = NotificationSettings()
        )
    }
    
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val newSettings = currentSettings.copy(enableNotifications = enabled)
            _uiState.value = _uiState.value.copy(settings = newSettings)
            saveSettings(newSettings)
        }
    }
    
    fun toggleCategory(category: String, enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val newCategories = if (enabled) {
                currentSettings.enabledCategories + category
            } else {
                currentSettings.enabledCategories - category
            }
            val newSettings = currentSettings.copy(enabledCategories = newCategories)
            _uiState.value = _uiState.value.copy(settings = newSettings)
            saveSettings(newSettings)
        }
    }
    
    fun updateNotificationFrequency(frequency: NotificationFrequency) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val newSettings = currentSettings.copy(notificationFrequency = frequency)
            _uiState.value = _uiState.value.copy(settings = newSettings)
            saveSettings(newSettings)
        }
    }
    
    private fun saveSettings(settings: NotificationSettings) {
        // In a real app, save to SharedPreferences/UserDefaults
        println("Saving settings: $settings")
    }
}

data class SettingsUiState(
    val settings: NotificationSettings = NotificationSettings(),
    val availableCategories: List<String> = listOf(
        "Technology",
        "Science", 
        "Business",
        "Health",
        "Entertainment",
        "Sports",
        "Politics"
    )
)
