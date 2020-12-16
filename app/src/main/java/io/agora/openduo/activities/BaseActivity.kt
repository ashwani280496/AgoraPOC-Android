package io.agora.openduo.activities

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import io.agora.openduo.OpenDuoApplication
import io.agora.openduo.agora.Config
import io.agora.openduo.agora.Global
import io.agora.openduo.agora.IEventListener
import io.agora.openduo.utils.WindowUtil
import io.agora.rtc.RtcEngine
import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation
import io.agora.rtm.RtmCallManager
import io.agora.rtm.RtmClient

abstract class BaseActivity : AppCompatActivity(), IEventListener {
    protected var statusBarHeight = 0
    protected var displayMetrics = DisplayMetrics()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowUtil.hideWindowStatusBar(window)
        setGlobalLayoutListener()
        getDisplayMetrics()
        initStatusBarHeight()
    }

    private fun setGlobalLayoutListener() {
        val layout = findViewById<View>(Window.ID_ANDROID_CONTENT)
        val observer = layout.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                onGlobalLayoutCompleted()
            }
        })
    }

    /**
     * Give a chance to obtain view layout attributes when the
     * content view layout process is completed.
     * Some layout attributes will be available here but not
     * in onCreate(), like measured width/height.
     * This callback will be called ONLY ONCE before the whole
     * window content is ready to be displayed for first time.
     */
    protected open fun onGlobalLayoutCompleted() {}
    private fun getDisplayMetrics() {
        windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    private fun initStatusBarHeight() {
        statusBarHeight = WindowUtil.getSystemStatusBarHeight(this)
    }

    public override fun onStart() {
        super.onStart()
        registerEventListener(this)
    }

    public override fun onStop() {
        super.onStop()
        removeEventListener(this)
    }

    fun application(): OpenDuoApplication {
        return application as OpenDuoApplication
    }

    protected fun rtcEngine(): RtcEngine {
        return application().rtcEngine()!!
    }

    protected open fun rtmClient(): RtmClient? {
        return application().rtmClient()
    }

    protected fun rtmCallManager(): RtmCallManager {
        return application().rtmCallManager()!!
    }

    protected fun config(): Config {
        return application().config()!!
    }

    protected fun global(): Global {
        return application().global()!!
    }

    private fun registerEventListener(listener: IEventListener) {
        application().registerEventListener(listener)
    }

    private fun removeEventListener(listener: IEventListener) {
        application().removeEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeEventListener(this)
    }

    override fun onConnectionStateChanged(status: Int, reason: Int) {
        Log.i(TAG, "onConnectionStateChanged status:$status reason:$reason")
    }

    override fun onPeersOnlineStatusChanged(map: Map<String, Int>?) {}
    override fun onLocalInvitationReceived(localInvitation: LocalInvitation?) {}
    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?) {}
    override fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?) {}
    override fun onLocalInvitationCanceled(localInvitation: LocalInvitation?) {}
    override fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int) {}
    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {}
    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?) {}
    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?) {}
    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?) {}
    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int) {}

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }
}