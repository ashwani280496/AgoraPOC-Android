package io.agora.openduo.ui.calling.connectionservice

import android.net.Uri
import android.os.Build
import android.telecom.*
import android.util.Log
import androidx.annotation.RequiresApi
import io.agora.openduo.Constants
import io.agora.openduo.OpenDuoApplication

@RequiresApi(api = Build.VERSION_CODES.M)
class OpenDuoConnectionService : ConnectionService() {
    fun application(): OpenDuoApplication {
        return application as OpenDuoApplication
    }

    override fun onCreateIncomingConnection(phoneAccount: PhoneAccountHandle, request: ConnectionRequest): Connection {
        Log.i(TAG, "onCreateIncomingConnection: called. $phoneAccount $request")
        val extras = request.extras
        val uid = extras.getString(Constants.CS_KEY_UID)
        val channel = extras.getString(Constants.CS_KEY_CHANNEL)
        val subscriber = extras.getString(Constants.CS_KEY_SUBSCRIBER)
        val role = extras.getInt(Constants.CS_KEY_ROLE)
        val videoState = extras.getInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE)
        val connection = OpenDuoConnection(applicationContext, uid, channel, subscriber, role)
        connection.setVideoState(videoState)
        connection.setAddress(Uri.fromParts(SCHEME_AG, subscriber, null), TelecomManager.PRESENTATION_ALLOWED)
        connection.setCallerDisplayName(subscriber, TelecomManager.PRESENTATION_ALLOWED)
        connection.setRinging()
        application().config()?.setConnection(connection)
        return connection
    }

    override fun onCreateOutgoingConnection(phoneAccount: PhoneAccountHandle, request: ConnectionRequest): Connection {
        Log.i(TAG, "onCreateOutgoingConnection: called. $phoneAccount $request")
        val extras = request.extras
        val uid = extras.getString(Constants.CS_KEY_UID)
        val channel = extras.getString(Constants.CS_KEY_CHANNEL)
        val subscriber = extras.getString(Constants.CS_KEY_SUBSCRIBER)
        val role = extras.getInt(Constants.CS_KEY_ROLE)
        val videoState = extras.getInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE)
        val connection = OpenDuoConnection(applicationContext, uid, channel, subscriber, role)
        connection.setVideoState(videoState)
        connection.setAddress(Uri.fromParts(SCHEME_AG, subscriber, null), TelecomManager.PRESENTATION_ALLOWED)
        connection.setCallerDisplayName(subscriber, TelecomManager.PRESENTATION_ALLOWED)
        connection.setRinging()
        application().config()?.setConnection(connection)
        return connection
    }

    override fun onCreateIncomingConnectionFailed(phoneAccount: PhoneAccountHandle, request: ConnectionRequest) {
        Log.e(TAG, "onCreateIncomingConnectionFailed: called. $phoneAccount $request")
    }

    override fun onCreateOutgoingConnectionFailed(phoneAccount: PhoneAccountHandle, request: ConnectionRequest) {
        Log.e(TAG, "onCreateOutgoingConnectionFailed: called. $phoneAccount $request")
    }

    companion object {
        private val TAG = OpenDuoConnectionService::class.java.simpleName
        const val SCHEME_AG = "customized_call"
    }
}