package com.example.komendystrzelanie.ui.screens.home

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.komendystrzelanie.data.preferences.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

class HomeViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val _delayText = MutableStateFlow("0")
    private val _positionDelayText = MutableStateFlow("0")

    private var mediaPlayer = MediaPlayer()
    private var audioJob: Job? = null

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            stopAudio()
        }
    }

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
    // Double the amount of normal commands to increase their occurrence relative to position commands (first/second/third)
    val level3 = listOf(
        "lewo.mp3",
        "prawo.mp3",
        "lewo_up.mp3",
        "prawo_up.mp3",
        "tyl_przez_lewe.mp3",
        "tyl_przez_prawe.mp3",
        "tyl_przez_lewe_up.mp3",
        "tyl_przez_prawe_up.mp3",
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

    init {
        viewModelScope.launch {
            settingsRepository.audioDelayFlow.collect { delay ->
                _delayText.value = delay.toString()
            }
        }

        viewModelScope.launch {
            settingsRepository.audioPositionDelayFlow.collect { delay ->
                _positionDelayText.value = delay.toString()
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    fun startAudio(context: Context, audioFiles: List<String>) {
        stopAudio()

        val delayMs = _delayText.value.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
        audioJob = startAudioLoop(context, audioFiles, delayMs)
    }

    fun stopAudio() {
        audioJob?.cancel()
        audioJob = null
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
    }

    private fun startAudioLoop(context: Context, audioFiles: List<String>, delayMs: Long): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            try {
                while (isActive) {
                    val randomAudio = audioFiles.random()

                    mediaPlayer.reset()
                    val success = playAudioFromAssets(context, randomAudio, mediaPlayer)
                    if (!success || !isActive) break

                    if (randomAudio == "pierwsza.mp3" || randomAudio == "druga.mp3" || randomAudio == "trzecia.mp3") {
                        delay(_positionDelayText.value.toLongOrNull() ?: 0L)
                    }else{
                        delay(delayMs)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun playAudioFromAssets(context: Context, fileName: String, player: MediaPlayer): Boolean {
        return try {
            val afd = context.assets.openFd("Audio/$fileName")
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.prepare()

            suspendCancellableCoroutine { cont ->
                player.setOnCompletionListener {
                    cont.resume(true)
                }
                player.setOnErrorListener { _, _, _ ->
                    cont.resume(false)
                    true
                }
                cont.invokeOnCancellation {
                    try {
                        if (player.isPlaying) {
                            player.stop()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                player.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
        mediaPlayer.release()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
    }

    class Factory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(settingsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}