package io.agora.openduo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import io.agora.openduo.DataLayer.AppPreference
import io.agora.openduo.DataLayer.RetrofitClientInstance
import io.agora.openduo.activities.DialerActivity
import io.agora.openduo.agora.Config
import io.agora.openduo.agora.EngineEventListener
import io.agora.openduo.agora.Global
import io.agora.openduo.agora.IEventListener
import io.agora.openduo.utils.FileUtil.rtmLogFile
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmCallManager
import io.agora.rtm.RtmClient


class OpenDuoApplication : Application(), LifecycleObserver {

    private var mRtcEngine: RtcEngine? = null
    private var mRtmClient: RtmClient? = null
    private var rtmCallManager: RtmCallManager? = null
    private var mEventListener: EngineEventListener? = null
    private var mConfig: Config? = null
    private var mGlobal: Global? = null
    private lateinit var shared: OpenDuoApplication


    override fun onCreate() {
        super.onCreate()
        instance = this
        init()
    }

    private fun init() {
        initAppPreference()
        initRetrofitClient()
        initConfig()
        initEngine()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerNotificationChannel()
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this);

    }

     fun initConfig() {
        mConfig = Config(applicationContext)
        mGlobal = Global()
    }


    fun initEngine() {
        val appId = applicationContext.getString(R.string.private_app_id)
        if (TextUtils.isEmpty(appId)) {
            throw RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/")
        }
        mEventListener = EngineEventListener()
        try {
            mRtcEngine = RtcEngine.create(applicationContext, appId, mEventListener)
            mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine?.enableDualStreamMode(true)
            mRtcEngine?.enableVideo()
            mRtcEngine?.setLogFile(rtmLogFile(applicationContext))
            mRtmClient = RtmClient.createInstance(applicationContext, appId, mEventListener)
            mRtmClient?.setLogFile(rtmLogFile(applicationContext))
            if (Config.DEBUG) {
                mRtcEngine?.setParameters("{\"rtc.log_filter\":65535}")
                mRtmClient?.setParameters("{\"rtm.log_filter\":65535}")
            }
            rtmCallManager = mRtmClient?.getRtmCallManager()
            rtmCallManager?.setEventListener(mEventListener)
            var accessToken: String? = getString(R.string.rtm_access_token)
            if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "<#YOUR ACCESS TOKEN#>")) {
                accessToken = null
            }
            mRtmClient?.login(AppPreference.getCurrentUser()!!.rtm_token, AppPreference.getCurrentUser()!!.id.toString(), object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    gotoDialerActivity()
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Log.i(TAG, "rtm client login failed:" + errorInfo.errorDescription)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAppPreference(){
        AppPreference.init(this)
    }

    private fun initRetrofitClient(){
        RetrofitClientInstance.init(this)
    }

    fun rtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun rtmClient(): RtmClient? {
        return mRtmClient
    }

    fun registerEventListener(listener: IEventListener?) {
        mEventListener!!.registerEventListener(listener!!)
    }

    fun removeEventListener(listener: IEventListener?) {
        mEventListener!!.removeEventListener(listener)
    }

    fun rtmCallManager(): RtmCallManager? {
        return rtmCallManager
    }

    fun config(): Config? {
        return mConfig
    }

    fun global(): Global? {
        return mGlobal
    }

    override fun onTerminate() {
        super.onTerminate()
        destroyEngine()
    }

    fun gotoDialerActivity() {
        val intent = Intent()
        intent.setClass(this, DialerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun destroyEngine() {
        RtcEngine.destroy()
        mRtmClient!!.logout(object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                Log.i(TAG, "rtm client logout success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                Log.i(TAG, "rtm client logout failed:" + errorInfo.errorDescription)
            }
        })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun registerNotificationChannel() {
        val notificationManager = NotificationManagerCompat.from(this)

        val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        Log.d("MyApp", "App in background")
       rtmClient()?.logout(object:ResultCallback<Void>{
           override fun onFailure(p0: ErrorInfo?) {
               Log.d("MyApp", "LOGOUT FAILURE")

           }

           override fun onSuccess(p0: Void?) {
               Log.d("MyApp", "RTM LOGOUT")

           }
       })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        Log.d("MyApp", "App in foreground")
    }
    companion object {
        private val TAG = OpenDuoApplication::class.java.simpleName
        lateinit var instance: OpenDuoApplication
            private set
    }


}