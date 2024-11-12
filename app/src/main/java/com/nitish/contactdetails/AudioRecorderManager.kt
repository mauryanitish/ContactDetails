package com.nitish.contactdetails

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object AudioRecorderManager {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var audioFilePath: String = ""
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    // Request permissions if not granted
    fun requestPermissions(context: Context) {
        ActivityCompat.requestPermissions(
            context as android.app.Activity,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1001
        )
    }

    // Start recording and save the file in WAV format
    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecording(context: Context) {
        Log.e("RecordingStatus", "Start Recording")

        // Check if already recording
        if (isRecording) {
            Log.e("RecordingStatus", "Already Recording")
            return
        }

        // Request permission if not granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(context)
        }

        // Prepare the file path
        val musicDirectory = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

        val file = File(musicDirectory, "audio_recording.wav")
        audioFilePath = file.absolutePath
        Log.e("RecordingStatus", "File Path: $audioFilePath")

        // Initialize AudioRecord
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("RecordingStatus", "AudioRecord initialization failed.")
            return
        }

        try {
            // Start recording
            audioRecord?.startRecording()
            isRecording = true
            Log.e("RecordingStatus", "Recording Started")

            // Write audio data to file in a background thread
            Thread(Runnable {
                try {
                    val outputStream = FileOutputStream(audioFilePath)
                    val buffer = ByteArray(bufferSize)
                    writeWavHeader(outputStream)  // Write header at the beginning
                    while (isRecording) {

                        val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                        if (read > 0) {
                            outputStream.write(buffer, 0, read)
                        }
                    }

                    // Finalize the WAV header after recording
                    writeWavHeader(outputStream, true)
                    outputStream.close()
                    Log.e("RecordingStatus", "Recording Stopped and File Saved")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("RecordingStatus", "IOException: ${e.message}")
                }
            }).start()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("RecordingStatus", "Error during recording: ${e.message}")
        }
    }

    // Stop the recording
    fun stopRecording() {
        if (!isRecording) return

        // Stop recording
        audioRecord?.stop()
        isRecording = false

        // Release resources
        audioRecord?.release()
        audioRecord = null
        Log.e("RecordingStatus", "Recording Stopped")
    }

    // Return the audio file path
    fun getAudioFilePath(): String {
        Log.d("Audio File", audioFilePath.toString())
        return audioFilePath
    }

    // Write the WAV header (overloaded to finalize header after recording)
    private fun writeWavHeader(outputStream: FileOutputStream, finalize: Boolean = false) {
        val header = ByteArray(44)

        // RIFF header
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()

        // File size (placeholder)
        if (finalize) {
            val fileSize = outputStream.channel.size().toInt() - 8
            header[4] = (fileSize and 0xFF).toByte()
            header[5] = ((fileSize shr 8) and 0xFF).toByte()
            header[6] = ((fileSize shr 16) and 0xFF).toByte()
            header[7] = ((fileSize shr 24) and 0xFF).toByte()
        } else {
            header[4] = 0
            header[5] = 0
            header[6] = 0
            header[7] = 0
        }

        // WAVE identifier
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()

        // Format chunk identifier
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()

        // Subchunk size
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0

        // Audio format (PCM)
        header[20] = 1
        header[21] = 0

        // Number of channels (Mono)
        header[22] = 1
        header[23] = 0

        // Sample rate (44100 Hz)
        header[24] = (sampleRate and 0xFF).toByte()
        header[25] = ((sampleRate shr 8) and 0xFF).toByte()
        header[26] = ((sampleRate shr 16) and 0xFF).toByte()
        header[27] = ((sampleRate shr 24) and 0xFF).toByte()

        // Byte rate (sampleRate * numChannels * bitsPerSample / 8)
        header[28] = (sampleRate * 2).toByte()
        header[29] = ((sampleRate * 2) shr 8).toByte()
        header[30] = ((sampleRate * 2) shr 16).toByte()
        header[31] = ((sampleRate * 2) shr 24).toByte()

        // Block align (numChannels * bitsPerSample / 8)
        header[32] = 2
        header[33] = 0

        // Bits per sample (16 bits)
        header[34] = 16
        header[35] = 0

        // Data chunk identifier
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()

        // Data size (placeholder)
        if (finalize) {
            val dataSize = outputStream.channel.size().toInt()
            header[40] = (dataSize and 0xFF).toByte()
            header[41] = ((dataSize shr 8) and 0xFF).toByte()
            header[42] = ((dataSize shr 16) and 0xFF).toByte()
            header[43] = ((dataSize shr 24) and 0xFF).toByte()
        } else {
            header[40] = 0
            header[41] = 0
            header[42] = 0
            header[43] = 0
        }

        outputStream.write(header)
    }

}
