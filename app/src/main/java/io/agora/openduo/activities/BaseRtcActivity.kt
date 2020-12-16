package io.agora.openduo.activities

import android.text.TextUtils
import android.view.SurfaceView
import io.agora.openduo.DataLayer.AppPreference
import io.agora.openduo.R
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration

abstract class BaseRtcActivity : BaseActivity() {
    protected fun joinRtcChannel(channel: String?, info: String?, uid: Int) {
        var accessToken: String? = AppPreference.getCurrentUser()?.rtc_token
        var uID: Int = AppPreference.getCurrentUser()?.id!!

        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "<#YOUR ACCESS TOKEN#>")) {
            accessToken = null
        }
        rtcEngine().joinChannel(accessToken, "channel", info, uid)
    }

    protected fun leaveChannel() {
        rtcEngine().leaveChannel()
    }

    protected fun setVideoConfiguration() {
        rtcEngine().setVideoEncoderConfiguration(
                VideoEncoderConfiguration(
                        config().dimension,
                        config().frameRate,
                        VideoEncoderConfiguration.STANDARD_BITRATE,
                        config().orientation)
        )
    }

    protected fun setupVideo(uid: Int, local: Boolean): SurfaceView {
        val surfaceView = RtcEngine.CreateRendererView(applicationContext)
        if (local) {
            rtcEngine().setupLocalVideo(VideoCanvas(surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid))
        } else {
            rtcEngine().setupRemoteVideo(VideoCanvas(surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid))
        }
        return surfaceView
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {}
    override fun onUserJoined(uid: Int, elapsed: Int) {}
    override fun onUserOffline(uid: Int, reason: Int) {}
    override fun onLeaveChannel(stats: IRtcEngineEventHandler.RtcStats) {

    }

}