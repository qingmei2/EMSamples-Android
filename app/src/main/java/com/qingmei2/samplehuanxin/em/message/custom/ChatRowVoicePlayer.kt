package com.qingmei2.samplehuanxin.em.message.custom

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMVoiceMessageBody

import java.io.IOException

/**
 * Created by zhangsong on 17-10-20.
 */

class ChatRowVoicePlayer private constructor(cxt: Context) {

    private val audioManager: AudioManager
    val player: MediaPlayer
    /**
     * May null, please consider.
     *
     * @return
     */
    var currentPlayingId: String? = null
        private set

    private var onCompletionListener: MediaPlayer.OnCompletionListener? = null

    val isPlaying: Boolean
        get() = player.isPlaying

    fun play(msg: EMMessage, listener: MediaPlayer.OnCompletionListener) {
        if (msg.body !is EMVoiceMessageBody) {
            return
        }

        if (player.isPlaying) {
            stop()
        }

        currentPlayingId = msg.msgId
        onCompletionListener = listener

        try {
            setSpeaker()
            val voiceBody = msg.body as EMVoiceMessageBody
            player.setDataSource(voiceBody.localUrl)
            player.prepare()
            player.setOnCompletionListener {
                stop()

                currentPlayingId = null
                onCompletionListener = null
            }
            player.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun stop() {
        player.stop()
        player.reset()

        /**
         * This listener is to stop the voice play animation currently, considered the following 3 conditions:
         *
         * 1.A new voice item is clicked to play, to stop the previous playing voice item animation.
         * 2.The voice is play complete, to stop it's voice play animation.
         * 3.Press the voice record button will stop the voice play and must stop voice play animation.
         *
         */
        if (onCompletionListener != null) {
            onCompletionListener!!.onCompletion(player)
        }
    }

    init {
        val baseContext = cxt.applicationContext
        audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        player = MediaPlayer()
    }

    private fun setSpeaker() {
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = true
        player.setAudioStreamType(AudioManager.STREAM_RING)
    }

    companion object {
        private val TAG = "ConcurrentMediaPlayer"

        private var instance: ChatRowVoicePlayer? = null

        fun getInstance(context: Context): ChatRowVoicePlayer {
            if (instance == null) {
                synchronized(ChatRowVoicePlayer::class.java) {
                    if (instance == null) {
                        instance = ChatRowVoicePlayer(context)
                    }
                }
            }
            return instance!!
        }
    }
}
