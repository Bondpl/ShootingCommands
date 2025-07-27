package com.example.komendystrzelanie.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(SettingsRepository(LocalContext.current))
    )
) {
    val audioDelay by viewModel.audioDelay.collectAsState()
    var delayText by remember { mutableStateOf(audioDelay.toString()) }
    val audioPositionDelay by viewModel.audioPositionChange.collectAsState()
    var delayPositionText by remember { mutableStateOf(audioPositionDelay.toString()) }
    var isErrorDelayText by remember { mutableStateOf(false) }
    var isErrorPositionDelayText by remember { mutableStateOf(false) }

    LaunchedEffect(audioDelay) {
        delayText = audioDelay.toString()
    }

    LaunchedEffect(audioPositionDelay) {
        delayPositionText = audioPositionDelay.toString()
    }

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
                if(it.isBlank()){
                    delayText = "0"
                    viewModel.updateAudioDelay(0)
                    isErrorDelayText = false
                }
                else{
                    val newDelayValue = it.toLongOrNull()
                    isErrorDelayText = newDelayValue == null

                    if (!isErrorDelayText && newDelayValue != null) {
                        val limitedValue = newDelayValue.coerceAtMost(5000L)
                        delayText = limitedValue.toString()
                        viewModel.updateAudioDelay(limitedValue)
                    }
                    else{
                        delayText = it
                    }
                }
            },
            label = { Text("Max 5000ms") },
            isError = isErrorDelayText,
            supportingText = {
                if (isErrorDelayText) {
                    Text("provide a valid number")
                } else {
                    Text("Saved")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text(
            text = "Audio Delay after only changing position (ms):",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = delayPositionText,
            onValueChange = {
                if (it.isBlank()){
                    delayPositionText = "0"
                    viewModel.updateAudioPositionDelay(0)
                    isErrorPositionDelayText = false
                } else {
                    val newDelayValue = it.toLongOrNull()
                    isErrorPositionDelayText = newDelayValue == null

                    if (!isErrorPositionDelayText && newDelayValue != null) {
                        val limitedValue = newDelayValue.coerceAtMost(10000L)
                        delayPositionText = limitedValue.toString()
                        viewModel.updateAudioPositionDelay(limitedValue)
                    } else {
                        delayPositionText = it
                    }
                }
            },
            label = { Text("Max 10000ms") },
            isError = isErrorPositionDelayText,
            supportingText = {
                if (isErrorPositionDelayText) {
                    Text("provide a valid number")
                } else {
                    Text("Saved")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

    }
}