package com.relyvo.izem.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

object SmartAudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(context: Context, url: String, fileName: String) {
        if (url.isEmpty()) return

        val file = File(context.cacheDir, "$fileName.mp3")

        if (file.exists()) {
            playFromFile(context, file)
        } else {
            downloadAndPlay(context, url, file)
        }
    }

    private fun playFromFile(context: Context, file: File) {
        try {
            stopAudio()
            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadAndPlay(context: Context, urlString: String, destinationFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connect()

                val input = connection.getInputStream()
                val output = destinationFile.outputStream()

                input.use { inputStr ->
                    output.use { outputStr ->
                        inputStr.copyTo(outputStr)
                    }
                }

                withContext(Dispatchers.Main) {
                    playFromFile(context, destinationFile)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopAudio() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}