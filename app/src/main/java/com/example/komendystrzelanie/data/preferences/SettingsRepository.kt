package com.example.komendystrzelanie.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        private val AUDIO_DELAY_KEY = longPreferencesKey("audio_delay")
        private val AUDIO_POSITION_DELAY_KEY = longPreferencesKey("audio_Position_delay")

    }

    val audioDelayFlow: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[AUDIO_DELAY_KEY] ?: 0L
        }

    val audioPositionDelayFlow: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[AUDIO_POSITION_DELAY_KEY] ?: 0L
        }

    suspend fun saveAudioDelay(delay: Long) {
        context.dataStore.edit { preferences ->
            preferences[AUDIO_DELAY_KEY] = delay.coerceAtMost(5000L)
        }
    }

    suspend fun saveAudioPositionDelay(delay: Long) {
        context.dataStore.edit { preferences ->
            preferences[AUDIO_POSITION_DELAY_KEY] = delay
        }
    }

}