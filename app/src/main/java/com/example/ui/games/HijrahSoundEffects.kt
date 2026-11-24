package com.example.ui.games

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object HijrahSoundEffects {
    private val soundScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    fun playTap(isMuted: Boolean) {
        if (isMuted) return
        playToneSequence(listOf(523.25 to 55))
    }

    fun playDice(isMuted: Boolean) {
        if (isMuted) return
        playToneSequence(listOf(330.0 to 45, 440.0 to 45, 587.33 to 65))
    }

    fun playFlip(isMuted: Boolean) {
        if (isMuted) return
        playToneSequence(listOf(440.0 to 50, 554.37 to 55))
    }

    fun playMatch(isMuted: Boolean) {
        if (isMuted) return
        playToneSequence(listOf(523.25 to 70, 659.25 to 80, 783.99 to 120))
    }

    fun playWrong(isMuted: Boolean) {
        if (isMuted) return
        playToneSequence(listOf(293.66 to 90, 220.0 to 140))
    }

    fun playVictory(isMuted: Boolean) {
        if (isMuted) return
        playToneSequence(
            listOf(
                523.25 to 90,
                659.25 to 90,
                783.99 to 90,
                1046.50 to 210
            )
        )
    }

    private fun playToneSequence(notes: List<Pair<Double, Int>>) {
        soundScope.launch {
            runCatching {
                val totalSamples = notes.sumOf { (_, ms) -> (SAMPLE_RATE * ms) / 1000 }
                if (totalSamples <= 0) return@runCatching
                val buffer = ShortArray(totalSamples)
                var offset = 0
                for ((freq, ms) in notes) {
                    val count = (SAMPLE_RATE * ms) / 1000
                    for (i in 0 until count) {
                        val t = i.toDouble() / SAMPLE_RATE
                        val progress = i.toDouble() / count.coerceAtLeast(1)
                        val envelope = exp(-3.5 * progress) * (1.0 - exp(-40.0 * progress))
                        val wave = sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)
                        val sample = (wave * envelope * 0.28 * Short.MAX_VALUE)
                            .toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                        if (offset + i < buffer.size) {
                            buffer[offset + i] = sample
                        }
                    }
                    offset += count
                }
                val minBufBytes = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val neededBytes = buffer.size * 2
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(maxOf(minBufBytes, neededBytes))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                val totalMs = notes.sumOf { it.second }.toLong() + 120L
                Thread.sleep(totalMs)
                track.stop()
                track.release()
            }
        }
    }
}
