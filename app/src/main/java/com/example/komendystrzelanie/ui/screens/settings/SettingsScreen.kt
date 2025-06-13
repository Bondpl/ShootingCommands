// ui/screens/settings/SettingsScreen.kt
package com.example.komendystrzelanie.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.komendystrzelanie.data.preferences.SettingsRepository

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(SettingsRepository(LocalContext.current))
    )
) {
    val audioDelay by viewModel.audioDelay.collectAsState()
    var delayText by remember { mutableStateOf(audioDelay.toString()) }
    val audioPositionDelay by viewModel.audioPositionChange.collectAsState()
    var delayPostitionText by remember { mutableStateOf(audioPositionDelay.toString()) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Audio Delay after each command (ms):",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = delayText,
            onValueChange = {
                delayText = it
                val newDelayValue = it.toLongOrNull()
                isError = newDelayValue == null || it.isBlank()

                if (!isError && newDelayValue != null) {
                    val limitedValue = newDelayValue.coerceAtMost(5000L)
                    delayText = limitedValue.toString()
                    viewModel.updateAudioDelay(limitedValue)
                }
            },
            label = { Text("Max 5000ms") },
            isError = isError,
            supportingText = {
                if (isError) {
                    Text("provide a valid number")
                } else {
                    Text("Saved")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        //Audio Delay after only changing position
        Text(
            text = "Audio Delay after only changing position (ms):",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = delayPostitionText,
            onValueChange = {
                delayPostitionText = it
                val newDelayValue = it.toLongOrNull()
                isError = newDelayValue == null || it.isBlank()

                if (!isError && newDelayValue != null) {
                    val limitedValue = newDelayValue.coerceAtMost(10000L)
                    delayPostitionText = limitedValue.toString()
                    viewModel.updateAudioPositionDelay(limitedValue)
                }
            },
            label = { Text("Max 10000ms") },
            isError = isError,
            supportingText = {
                if (isError) {
                    Text("provide a valid number")
                } else {
                    Text("Saved")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

    }
}