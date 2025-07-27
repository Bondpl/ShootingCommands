package com.example.komendystrzelanie.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.komendystrzelanie.data.preferences.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val _audioDelay = MutableStateFlow(0L)
    val audioDelay: StateFlow<Long> = _audioDelay.asStateFlow()

    private val _audioPositionDelay = MutableStateFlow(0L)
    val audioPositionChange: StateFlow<Long> = _audioPositionDelay.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.audioDelayFlow.collect { delay ->
                _audioDelay.value = delay
            }
        }

        viewModelScope.launch {
            settingsRepository.audioPositionDelayFlow.collect { delay ->
                _audioPositionDelay.value = delay
            }
        }
    }

    fun updateAudioDelay(delay: Long) {
        viewModelScope.launch {
            settingsRepository.saveAudioDelay(delay)
        }
    }

    fun updateAudioPositionDelay(delay: Long) {
        viewModelScope.launch {
            settingsRepository.saveAudioPositionDelay(delay)
        }
    }

    class Factory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(settingsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}