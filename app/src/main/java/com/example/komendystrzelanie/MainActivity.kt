package com.example.komendystrzelanie

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.komendystrzelanie.ui.theme.KomendyStrzelanieTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KomendyStrzelanieTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudioButtonScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AudioButtonScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var delayText by remember { mutableStateOf("0") }
    var audioJob by remember { mutableStateOf<Job?>(null) }


    val Level1 = listOf(
        "lewo.mp3",
        "prawo.mp3",
        "lewo_up.mp3",
        "prawo_up.mp3",
    )

    val Level2 = listOf(
        "lewo.mp3",
        "prawo.mp3",
        "lewo_up.mp3",
        "prawo_up.mp3",
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
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                // Set delayMs only when Button is clicked
                val delayMs = delayText.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
                audioJob = startAudioLoop(context, Level1, delayMs) { player ->
                    mediaPlayer?.release()
                    mediaPlayer = player
                }
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
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                // Set delayMs only when Button is clicked
                val delayMs = delayText.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
                audioJob = startAudioLoop(context, Level2, delayMs) { player ->
                    mediaPlayer?.release()
                    mediaPlayer = player
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .heightIn(min = 100.dp),
            shape = RectangleShape
        ) {
            Text("Start lvl 2")
        }

        // Stop Button
        Button(
            onClick = {
                audioJob?.cancel()
                audioJob = null
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
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


suspend fun playAudioFromAssets(context: Context, fileName: String): Boolean {
    return try {
        val player = MediaPlayer()
        val afd = context.assets.openFd("Audio/$fileName")
        player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        player.prepare()

        suspendCancellableCoroutine { continuation ->
            player.setOnCompletionListener {
                player.release()
                continuation.resume(true)
            }
            player.setOnErrorListener { _, _, _ ->
                player.release()
                continuation.resume(false)
                true
            }
            continuation.invokeOnCancellation {
                player.stop()
                player.release()
            }
            player.start()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}


fun startAudioLoop(context: Context, audioFiles: List<String>, delayMs: Long, setMediaPlayer: (MediaPlayer?) -> Unit): Job {
    return CoroutineScope(Dispatchers.Main).launch {
        try {
            while (isActive) {
                val randomAudio = audioFiles.random()

                // Odtwórz audio i czekaj na zakończenie
                val success = playAudioFromAssets(context, randomAudio)
                if (!success || !isActive) break

                // Czekaj delay przed następnym
                delay(delayMs)
            }
        } catch (e: CancellationException) {
            // Normalne anulowanie
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            setMediaPlayer(null)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AudioButtonScreenPreview() {
    KomendyStrzelanieTheme {
        AudioButtonScreen()
    }
}