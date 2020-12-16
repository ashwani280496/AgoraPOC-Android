package io.agora.openduo.agora

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import io.agora.openduo.Constants
import io.agora.openduo.ui.calling.connectionservice.OpenDuoCallSession
import io.agora.openduo.ui.calling.connectionservice.OpenDuoConnection
import io.agora.openduo.ui.calling.connectionservice.OpenDuoConnectionService
import io.agora.openduo.utils.SPUtils
import io.agora.openduo.utils.UserUtil
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc.video.VideoEncoderConfiguration.ORIENTATION_MODE

class Config(context: Context) {
    private fun initUserId(context: Context) {
        var currentId = SPUtils.getUserId(context)
        if (currentId == null) {
            currentId = UserUtil.randomUserId()
            SPUtils.saveUserId(context, currentId)
        }
        userId = currentId
    }

    private fun checkSystemCallSupport(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerPhoneAccount(context)
            mUseSystemCall = true
        }
    }

    fun useSystemCallInterface(): Boolean {
        return mUseSystemCall && FLAG_USE_SYSTEM_CALL_UI
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun registerPhoneAccount(context: Context) {
        val accountHandleIn = PhoneAccountHandle(
                ComponentName(context, OpenDuoConnectionService::class.java), Constants.PA_LABEL_CALL_IN)
        val accountHandleOut = PhoneAccountHandle(
                ComponentName(context, OpenDuoConnectionService::class.java), Constants.PA_LABEL_CALL_OUT)
        var paBuilder: PhoneAccount.Builder
        paBuilder = PhoneAccount.builder(accountHandleIn, Constants.PA_LABEL_CALL_IN)
                .setCapabilities(PhoneAccount.CAPABILITY_CONNECTION_MANAGER)
        val phoneIn = paBuilder.build()
        paBuilder = PhoneAccount.builder(accountHandleOut, Constants.PA_LABEL_CALL_OUT)
                .setCapabilities(PhoneAccount.CAPABILITY_CONNECTION_MANAGER)
        val extra = Bundle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            extra.putBoolean(PhoneAccount.EXTRA_LOG_SELF_MANAGED_CALLS, true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            paBuilder.setExtras(extra)
        }
        val phoneOut = paBuilder.build()
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        telecomManager.registerPhoneAccount(phoneIn)
        telecomManager.registerPhoneAccount(phoneOut)
        mCallSession = OpenDuoCallSession()
        mCallSession!!.phoneAccountIn = phoneIn
        mCallSession!!.phoneAccountOut = phoneOut
    }

    val phoneAccountIn: PhoneAccount?
        get() = if (mCallSession == null) null else mCallSession!!.phoneAccountIn

    val phoneAccountOut: PhoneAccount?
        get() = if (mCallSession == null) null else mCallSession!!.phoneAccountOut

    fun setConnection(connection: OpenDuoConnection?) {
        if (mCallSession != null) {
            mCallSession!!.connection = connection
        }
    }

    var userId: String? = null
        private set
    private var mUseSystemCall = false
    private var mCallSession: OpenDuoCallSession? = null
    val dimension = VideoEncoderConfiguration.VD_640x480
    val frameRate = FRAME_RATE.FRAME_RATE_FPS_15
    val orientation = ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT

    companion object {
        const val DEBUG = false
        private const val FLAG_USE_SYSTEM_CALL_UI = false
    }

    init {
        initUserId(context)
        checkSystemCallSupport(context)
    }
}