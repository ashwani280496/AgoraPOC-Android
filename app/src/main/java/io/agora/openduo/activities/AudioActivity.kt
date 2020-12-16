package io.agora.openduo.activities

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import io.agora.openduo.Constants
import io.agora.openduo.DataLayer.AppPreference
import io.agora.openduo.R
import io.agora.openduo.R.layout.activity_audio
import io.agora.rtc.IRtcEngineEventHandler
import kotlinx.android.synthetic.main.activity_audio.*
import kotlinx.android.synthetic.main.activity_video.*
import kotlin.concurrent.fixedRateTimer


class AudioActivity : BaseCallActivity() {
    private var mChannel: String? = null
    private var mPeerUid = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_audio)

        initVideo()
    }

    private fun initVideo() {
        val intent = intent
        btn_speaker.isSelected = true
        btn_speaker.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        mChannel = intent.getStringExtra(Constants.KEY_CALLING_CHANNEL)
        try {
            mPeerUid = Integer.valueOf(intent.getStringExtra(Constants.KEY_CALLING_PEER))
        } catch (e: NumberFormatException) {
            Toast.makeText(this, R.string.message_wrong_number,
                    Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

        name.text = AppPreference.getUsers()?.first {
            it.id == mPeerUid
        }?.user_id
        rtcEngine().setEnableSpeakerphone(true)

        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER)
        setVideoConfiguration()
        config().userId?.toInt()?.let { joinRtcChannel(mChannel, "", it) }
    }

    private fun startTimer(){

    }

    override fun finish() {
        super.finish()
        leaveChannel()
    }

    fun onLocalAudioMuteClicked(view: View) {
        rtcEngine().muteLocalAudioStream(btn_mute!!.isActivated)
        btn_mute!!.isActivated = !btn_mute!!.isActivated
    }
    fun onSwitchSpeakerphoneClicked(view: View) {
        val iv: ImageView = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        rtcEngine().setEnableSpeakerphone(view.isSelected)
    }
    fun onEncCallClicked(view: View) {
        finish()
    }
    override fun onUserJoined(uid: Int, elapsed: Int) {
        super.onUserJoined(uid, elapsed)
        var count = 0
        fixedRateTimer("timer", false, 0L, 1000) {
            this@AudioActivity.runOnUiThread {
                val minutes = count / 60
                val seconds = count % 60
                timer_lbl.text = "$minutes : $seconds"
                count ++
            }
        }
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        super.onJoinChannelSuccess(channel, uid, elapsed)
    }

    override fun onLeaveChannel(stats: IRtcEngineEventHandler.RtcStats) {
        finish()
    }
}