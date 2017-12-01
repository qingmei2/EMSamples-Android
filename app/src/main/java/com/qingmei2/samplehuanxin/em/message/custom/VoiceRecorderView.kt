package com.qingmei2.samplehuanxin.em.message.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.PowerManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.hyphenate.EMError
import com.qingmei2.samplehuanxin.R
import kotlinx.android.synthetic.main.ease_widget_voice_recorder.view.*

/**
 * Voice recorder view
 */
class VoiceRecorderView : RelativeLayout {

    internal var inflater: LayoutInflater? = null
    lateinit var micImages: Array<Drawable>
    internal var voiceRecorder: VoiceRecorder? = null

    lateinit var wakeLock: PowerManager.WakeLock

    protected var micImageHandler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            // change image
            mic_image.setImageDrawable(micImages[msg.what])
        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_voice_recorder, this)

        voiceRecorder = VoiceRecorder(micImageHandler)

        // animation resources, used for recording
        micImages = arrayOf(
                resources.getDrawable(R.drawable.ease_record_animate_01),
                resources.getDrawable(R.drawable.ease_record_animate_02),
                resources.getDrawable(R.drawable.ease_record_animate_03),
                resources.getDrawable(R.drawable.ease_record_animate_04),
                resources.getDrawable(R.drawable.ease_record_animate_05),
                resources.getDrawable(R.drawable.ease_record_animate_06),
                resources.getDrawable(R.drawable.ease_record_animate_07),
                resources.getDrawable(R.drawable.ease_record_animate_08),
                resources.getDrawable(R.drawable.ease_record_animate_09),
                resources.getDrawable(R.drawable.ease_record_animate_10),
                resources.getDrawable(R.drawable.ease_record_animate_11),
                resources.getDrawable(R.drawable.ease_record_animate_12),
                resources.getDrawable(R.drawable.ease_record_animate_13),
                resources.getDrawable(R.drawable.ease_record_animate_14))

        wakeLock = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo")
    }

    /**
     * on speak button touched
     *
     * @param v
     * @param event
     */
    fun onPressToSpeakBtnTouch(v: View, event: MotionEvent, recorderCallback: EaseVoiceRecorderCallback?): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                try {
                    val voicePlayer = ChatRowVoicePlayer.getInstance(context)
                    if (voicePlayer.isPlaying)
                        voicePlayer.stop()
                    v.isPressed = true
                    startRecording()
                } catch (e: Exception) {
                    v.isPressed = false
                }

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.y < 0) {
                    showReleaseToCancelHint()
                } else {
                    showMoveUpToCancelHint()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                v.isPressed = false
                if (event.y < 0) {
                    // discard the recorded audio.
                    discardRecording()
                } else {
                    // stop recording and send voice file
                    try {
                        val length = stopRecoding()
                        if (length > 0) {
                            recorderCallback?.onVoiceRecordComplete(getVoiceFilePath(), length)
                        } else if (length == EMError.FILE_INVALID) {
                            Toast.makeText(context, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, R.string.send_failure_please, Toast.LENGTH_SHORT).show()
                    }

                }
                return true
            }
            else -> {
                discardRecording()
                return false
            }
        }
    }

    interface EaseVoiceRecorderCallback {
        /**
         * on voice record complete
         *
         * @param voiceFilePath
         * 录音完毕后的文件路径
         * @param voiceTimeLength
         * 录音时长
         */
        fun onVoiceRecordComplete(voiceFilePath: String, voiceTimeLength: Int)
    }

    fun startRecording() {
        //check if sdcard exist
        if (android.os.Environment.getExternalStorageState() != android.os.Environment.MEDIA_MOUNTED) {
            Toast.makeText(context, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            wakeLock.acquire()
            this.visibility = View.VISIBLE
            recording_hint.text = context.getString(R.string.move_up_to_cancel)
            recording_hint.setBackgroundColor(Color.TRANSPARENT)
            voiceRecorder!!.startRecording(context)
        } catch (e: Exception) {
            e.printStackTrace()
            if (wakeLock.isHeld)
                wakeLock.release()
            if (voiceRecorder != null)
                voiceRecorder!!.discardRecording()
            this.visibility = View.INVISIBLE
            Toast.makeText(context, R.string.recoding_fail, Toast.LENGTH_SHORT).show()
            return
        }
    }

    fun showReleaseToCancelHint() {
        recording_hint.text = context.getString(R.string.release_to_cancel)
        recording_hint.setBackgroundResource(R.drawable.ease_recording_text_hint_bg)
    }

    fun showMoveUpToCancelHint() {
        recording_hint.text = context.getString(R.string.move_up_to_cancel)
        recording_hint.setBackgroundColor(Color.TRANSPARENT)
    }

    fun discardRecording() {
        if (wakeLock.isHeld)
            wakeLock.release()
        try {
            // stop recording
            if (voiceRecorder!!.isRecording()) {
                voiceRecorder!!.discardRecording()
                this.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
        }

    }

    fun stopRecoding(): Int {
        this.visibility = View.INVISIBLE
        if (wakeLock.isHeld)
            wakeLock.release()
        return voiceRecorder!!.stopRecoding()
    }

    fun getVoiceFilePath(): String = voiceRecorder!!.getVoiceFilePath()!!

    fun getVoiceFileName(): String = voiceRecorder!!.getVoiceFileName()!!

    fun isRecording(): Boolean = voiceRecorder!!.isRecording()!!
}
