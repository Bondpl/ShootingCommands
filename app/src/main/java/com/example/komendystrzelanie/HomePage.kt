package com.example.komendystrzelanie

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

@Composable
fun AudioButtonScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf(MediaPlayer()) }
    var delayText by remember { mutableStateOf("0") }
    var audioJob by remember { mutableStateOf<Job?>(null) }

    val level1 = listOf(
        "lewo.mp3",
        "prawo.mp3",
        "lewo_up.mp3",
        "prawo_up.mp3",
    )

    val level2 = listOf(
        "lewo.mp3",
        "prawo.mp3",
        "lewo_up.mp3",
        "prawo_up.mp3",
        "tyl_przez_lewe.mp3",
        "tyl_przez_prawe.mp3",
        "tyl_przez_lewe_up.mp3",
        "tyl_przez_prawe_up.mp3",
    )

    val level3 = listOf(
        "lewo.mp3",
        "prawo.mp3",
        "lewo_up.mp3",
        "prawo_up.mp3",
        "tyl_przez_lewe.mp3",
        "tyl_przez_prawe.mp3",
        "tyl_przez_lewe_up.mp3",
        "tyl_przez_prawe_up.mp3",
        "pierwsza.mp3",
        "druga.mp3",
        "trzecia.mp3",
    )

    // Clean up MediaPlayer when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            audioJob?.cancel()
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),

        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ){
        Text(
            text = "Random Shooting Commands",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)

        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {


        OutlinedTextField(
            value = delayText,
            onValueChange = {
                val filtered = it.filter { c -> c.isDigit() }
                val value = filtered.toLongOrNull() ?: 0L
                val limited = value.coerceAtMost(5000L)
                delayText = if (filtered.isEmpty()) "0" else limited.toString()
            },
            label = { Text("Delay (ms, max 5000)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        //Start lvl1 Button
        Button(
            onClick = {
                audioJob?.cancel()
                // Set delayMs only when Button is clicked
                val delayMs = delayText.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
                audioJob = startAudioLoop(context, level1, delayMs,mediaPlayer) {}
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .heightIn(min = 100.dp),
            shape = RectangleShape
        ) {
            Text("Start lvl 1")
        }

        //Start lvl2 Button
        Button(
            onClick = {
                audioJob?.cancel()
                // Set delayMs only when Button is clicked
                val delayMs = delayText.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
                audioJob = startAudioLoop(context, level2, delayMs,mediaPlayer) {}

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .heightIn(min = 100.dp),
            shape = RectangleShape
        ) {
            Text("Start lvl 2")
        }
        //Start lvl3 Button
        Button(
            onClick = {
                audioJob?.cancel()
                // Set delayMs only when Button is clicked
                val delayMs = delayText.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
                audioJob = startAudioLoop(context, level3, delayMs,mediaPlayer) {}

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .heightIn(min = 100.dp),
            shape = RectangleShape
        ) {
            Text("Start lvl 3")
        }

        // Stop Button
        Button(
            onClick = {
                audioJob?.cancel()
                audioJob = null
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .heightIn(min = 100.dp),
            shape = RectangleShape,

            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Stop")
        }
    }
}


suspend fun playAudioFromAssets(context: Context, fileName: String, player: MediaPlayer): Boolean {
    try {
        val afd = context.assets.openFd("Audio/$fileName")
        player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        player.prepare()

        suspendCancellableCoroutine { continuation ->
            player.setOnCompletionListener {
                continuation.resume(true)
            }
            player.setOnErrorListener { _, _, _ ->
                continuation.resume(false)
                true
            }
            continuation.invokeOnCancellation {
                player.stop()
            }
            player.start()
        }
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun startAudioLoop(context: Context, audioFiles: List<String>, delayMs: Long, mediaPlayer: MediaPlayer, setMediaPlayer: (MediaPlayer?) -> Unit): Job {
    return CoroutineScope(Dispatchers.Main).launch {
        try {
            while (isActive) {
                val randomAudio = audioFiles.random()

                mediaPlayer.reset()
                val success = playAudioFromAssets(context, randomAudio, mediaPlayer)
                if (!success || !isActive) break

                // Delay for these specific files
                if (randomAudio == "pierwsza.mp3" || randomAudio == "druga.mp3" || randomAudio == "trzecia.mp3") {
                    delay(DELAY_AFTER_PLAYING_SPECIFIC_COMMANDS)
                }

                delay(delayMs)
            }
        } catch (e: CancellationException) {
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            setMediaPlayer(null)
        }
    }
}