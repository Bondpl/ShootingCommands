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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.komendystrzelanie.ui.theme.KomendyStrzelanieTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var delayMs by remember { mutableStateOf(0L) }

    val audioFiles = listOf(
        "piano2.mp3",
        "1.mp3",
        "2.mp3",
    )

    // Clean up MediaPlayer when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
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

        //Start Button
        Button(
            onClick = {
                // Set delayMs only when Start is clicked
                delayMs = delayText.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
                playRandomAudioLoop(context, audioFiles, delayMs) { player ->;
                    mediaPlayer?.release()
                    mediaPlayer = player
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Start")
        }

        // Stop Button
        Button(
            onClick = {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Stop")
        }
    }
}


private fun playAudioFromAssets(
    context: android.content.Context,
    fileName: String,
    onPlayerCreated: (MediaPlayer) -> Unit
) {
    try {
        val player = MediaPlayer()
        val afd = context.assets.openFd("Audio/$fileName")
        player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        player.prepare()
        onPlayerCreated(player)

        player.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun playRandomAudioLoop(context: Context, audioFiles: List<String>, delayMs: Long, setMediaPlayer: (MediaPlayer?) -> Unit){
    val randomAudio = audioFiles.random()
    playAudioFromAssets(context, randomAudio) { player ->
        setMediaPlayer(player)
        player.setOnCompletionListener {
            CoroutineScope(Dispatchers.Main).launch {
                delay(delayMs)
                playRandomAudioLoop(context, audioFiles, delayMs, setMediaPlayer)
            }
        }
        player.start()
    }

}
@Preview(showBackground = true)
@Composable
fun AudioButtonScreenPreview() {
    KomendyStrzelanieTheme {
        AudioButtonScreen()
    }
}