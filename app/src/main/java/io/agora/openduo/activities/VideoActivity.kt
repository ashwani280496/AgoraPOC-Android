package io.agora.openduo.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import io.agora.openduo.Constants
import io.agora.openduo.R
import io.agora.rtm.RemoteInvitation

class VideoActivity : BaseCallActivity() {
    private var mLocalPreviewLayout: FrameLayout? = null
    private var mRemotePreviewLayout: FrameLayout? = null
    private var mMuteBtn: AppCompatImageView? = null
    private var mChannel: String? = null
    private var mPeerUid = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        initUI()
        initVideo()
    }

    private fun initUI() {
        mLocalPreviewLayout = findViewById(R.id.local_preview_layout)
        mRemotePreviewLayout = findViewById(R.id.remote_preview_layout)
        mMuteBtn = findViewById(R.id.btn_mute)
        mMuteBtn?.setActivated(true)
    }

    override fun onGlobalLayoutCompleted() {
        var params = mLocalPreviewLayout!!.layoutParams as RelativeLayout.LayoutParams
        params.topMargin += statusBarHeight
        mLocalPreviewLayout!!.layoutParams = params
        val buttonLayout = findViewById<RelativeLayout>(R.id.button_layout)
        params = buttonLayout.layoutParams as RelativeLayout.LayoutParams
        params.bottomMargin = displayMetrics.heightPixels / 8
        params.leftMargin = displayMetrics.widthPixels / 6
        params.rightMargin = displayMetrics.widthPixels / 6
        buttonLayout.layoutParams = params
    }

    private fun initVideo() {
        val intent = intent
        mChannel = intent.getStringExtra(Constants.KEY_CALLING_CHANNEL)
        try {
            mPeerUid = Integer.valueOf(intent.getStringExtra(Constants.KEY_CALLING_PEER))
        } catch (e: NumberFormatException) {
            Toast.makeText(this, R.string.message_wrong_number,
                    Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER)
        setVideoConfiguration()
        setupLocalPreview()
        config().userId?.toInt()?.let { joinRtcChannel(mChannel, "", it) }
    }

    private fun setupLocalPreview() {
        val surfaceView = config().userId?.toInt()?.let { setupVideo(it, true) }
        surfaceView!!.setZOrderOnTop(true)
        mLocalPreviewLayout!!.addView(surfaceView)
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        if (uid != mPeerUid) return
        runOnUiThread {
            if (mRemotePreviewLayout!!.childCount == 0) {
                val surfaceView = setupVideo(uid, false)
                mRemotePreviewLayout!!.addView(surfaceView)
                rtcEngine().setEnableSpeakerphone(true)
            }
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        if (uid != mPeerUid) return
        finish()
    }

    fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.btn_endcall -> finish()
            R.id.btn_mute -> {
                rtcEngine().muteLocalAudioStream(mMuteBtn!!.isActivated)
                mMuteBtn!!.isActivated = !mMuteBtn!!.isActivated
            }
            R.id.btn_switch_camera -> rtcEngine().switchCamera()
        }
    }

    override fun finish() {
        super.finish()
        leaveChannel()
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {
        // Do not respond to any other calls
        Log.i(TAG, "Ignore remote invitation from " +
                remoteInvitation?.callerId + " while in calling")
    }

    companion object {
        private val TAG = AudioActivity::class.java.simpleName
    }
}