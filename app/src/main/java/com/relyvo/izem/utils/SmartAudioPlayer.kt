package com.relyvo.izem.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

object SmartAudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(context: Context, url: String, wordId: String) {
        if (url.isEmpty()) return

        val safeFileName = if (wordId.isNotEmpty()) {
            val extension = if (url.contains(".m4a")) "m4a" else "mp3"
            "$wordId.$extension"
        } else {
            url.substringAfterLast("/").substringBefore("?")
        }

        Log.d("IzemAudio", "Playing: $safeFileName")

        val file = File(context.cacheDir, safeFileName)

        if (file.exists() && file.length() > 0) {
            playFromFile(context, file)
        } else {
            downloadAndPlay(context, url, file)
        }
    }

    private fun playFromFile(context: Context, file: File) {
        try {
            stopAudio()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(file))
                prepare()
                start()
                setOnCompletionListener {
                    it.reset()
                    it.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            Log.e("IzemAudio", "Error playing: ${e.message}")
            if (file.exists()) file.delete()
        }
    }

    private fun downloadAndPlay(context: Context, urlString: String, destinationFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(urlString)
                val bytes = url.readBytes()
                destinationFile.writeBytes(bytes)

                withContext(Dispatchers.Main) {
                    playFromFile(context, destinationFile)
                }
            } catch (e: Exception) {
                Log.e("IzemAudio", "Download failed: ${e.message}")
            }
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            mediaPlayer = null
        }
    }
}