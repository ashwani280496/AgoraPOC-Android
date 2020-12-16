package io.agora.openduo.ui.calling.connectionservice

import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi
import io.agora.openduo.Constants
import io.agora.openduo.activities.AudioActivity

@RequiresApi(api = Build.VERSION_CODES.M)
class OpenDuoConnection(private val mContext: Context, private val mUid: String?, private val mChannel: String?, private val mSubscriber: String?, private val mRole: Int) : Connection() {
    override fun onShowIncomingCallUi() {
        Log.d(TAG, "onShowIncomingCallUi called")
        super.onShowIncomingCallUi()
    }

    override fun onDisconnect() {
        Log.d(TAG, "onDisconnect called")
        super.onDisconnect()
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onAbort() {
        Log.d(TAG, "onAbort called")
        super.onAbort()
        setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
        destroy()
    }

    override fun onAnswer() {
        Log.d(TAG, "onAnswer called")
        super.onAnswer()
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL)) // tricky way to dismiss the system incall ui
        val intent = Intent(mContext, AudioActivity::class.java)
        intent.putExtra(Constants.CS_KEY_UID, mUid)
        intent.putExtra(Constants.CS_KEY_CHANNEL, mChannel)
        intent.putExtra(Constants.CS_KEY_SUBSCRIBER, mSubscriber)
        intent.putExtra(Constants.CS_KEY_ROLE, mRole)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(intent)
    }

    override fun onReject() {
        Log.d(TAG, "onReject called")
        super.onReject()

//        WorkerThread worker = ((AGApplication) mContext.getApplicationContext()).getWorkerThread();
//        EngineConfig config = worker.getEngineConfig();
//
//        // "status": 0 // Default
//        // "status": 1 // Busy
//        config.mRemoteInvitation.setResponse("{\"status\":0}");
//        worker.hangupTheCall(config.mRemoteInvitation);
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
    }

    companion object {
        private val TAG = OpenDuoConnection::class.java.simpleName
    }

}