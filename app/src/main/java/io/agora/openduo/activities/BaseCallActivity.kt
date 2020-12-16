package io.agora.openduo.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import io.agora.openduo.Constants
import io.agora.openduo.ui.calling.connectionservice.OpenDuoConnectionService
import io.agora.rtm.*

abstract class BaseCallActivity : BaseRtcActivity(), RtmChannelListener, ResultCallback<Void?> {
    protected var mRtmCallManager: RtmCallManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRtmCallManager = rtmCallManager()
    }

    fun gotoCallingInterface(peerUid: String?, channel: String?, role: Int) {
        if (config().useSystemCallInterface()) {
            // Not supported yet.
            // placeSystemCall(String.valueOf(config().getUserId()), peerUid, channel);
        } else {
            gotoCallingActivity(channel, peerUid, role)
        }
    }

    private fun placeSystemCall(myUid: String, peerUid: String, channel: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val extras = Bundle()
            extras.putInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL)
            val extraBundle = Bundle()
            extraBundle.putString(Constants.CS_KEY_UID, myUid)
            extraBundle.putString(Constants.CS_KEY_SUBSCRIBER, peerUid)
            extraBundle.putString(Constants.CS_KEY_CHANNEL, channel)
            extraBundle.putInt(Constants.CS_KEY_ROLE, Constants.CALL_ID_OUT)
            extras.putBundle(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extraBundle)
            try {
                val telecomManager = applicationContext.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                val pa = telecomManager.getPhoneAccount(
                        config().phoneAccountOut?.accountHandle)
                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, pa.accountHandle)
                telecomManager.placeCall(Uri.fromParts(
                        OpenDuoConnectionService.SCHEME_AG, peerUid, null), extras)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    protected fun gotoCallingActivity(channel: String?, peer: String?, role: Int) {
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra(Constants.KEY_CALLING_CHANNEL, channel)
        intent.putExtra(Constants.KEY_CALLING_PEER, peer)
        intent.putExtra(Constants.KEY_CALLING_ROLE, role)
        startActivity(intent)
    }

    protected fun inviteCall(peerUid: String?, channel: String?) {
        val invitation = mRtmCallManager!!.createLocalInvitation(peerUid)
        invitation.content = channel
        mRtmCallManager!!.sendLocalInvitation(invitation, this)
        global().localInvitation = invitation
    }

    protected fun answerCall(invitation: RemoteInvitation?) {
        if (mRtmCallManager != null && invitation != null) {
            mRtmCallManager!!.acceptRemoteInvitation(invitation, this)
        }
    }

    protected fun cancelLocalInvitation() {
        if (mRtmCallManager != null && global().localInvitation != null) {
            mRtmCallManager!!.cancelLocalInvitation(global().localInvitation, this)
        }
    }

    protected fun refuseRemoteInvitation(invitation: RemoteInvitation) {
        if (mRtmCallManager != null) {
            mRtmCallManager!!.refuseRemoteInvitation(invitation, this)
        }
    }

    override fun onMemberCountUpdated(count: Int) {}
    override fun onAttributesUpdated(list: List<RtmChannelAttribute>) {}
    override fun onMessageReceived(rtmMessage: RtmMessage, rtmChannelMember: RtmChannelMember) {}
    override fun onMemberJoined(rtmChannelMember: RtmChannelMember) {}
    override fun onMemberLeft(rtmChannelMember: RtmChannelMember) {}
    override fun onSuccess(aVoid: Void?) {}
    override fun onFailure(errorInfo: ErrorInfo) {}
    override fun onLocalInvitationReceived(localInvitation: LocalInvitation?) {
        super.onLocalInvitationReceived(localInvitation)
    }

    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?) {
        Log.i("BaseActivity", "onLocalInvitationAccepted by peer:" + localInvitation?.calleeId)
        gotoAudioActivity(localInvitation?.content, localInvitation?.calleeId)
    }

    override fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?) {
        super.onLocalInvitationRefused(localInvitation, response)
    }

    override fun onLocalInvitationCanceled(localInvitation: LocalInvitation?) {
        super.onLocalInvitationCanceled(localInvitation)
    }

    override fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int) {
        super.onLocalInvitationFailure(localInvitation, errorCode)
        Log.w("BaseActivity", "onLocalInvitationFailure:$errorCode")
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {
        Log.i("BaseActivity", "onRemoteInvitationReceived from caller:" + remoteInvitation?.callerId)
        global().remoteInvitation = remoteInvitation
        gotoCallingActivity(remoteInvitation?.content, remoteInvitation?.callerId, Constants.ROLE_CALLEE)
    }

    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?) {
        Log.i("BaseActivity", "onRemoteInvitationAccepted from caller:" + remoteInvitation?.callerId)
        gotoAudioActivity(remoteInvitation?.content, remoteInvitation?.callerId)
    }

    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?) {
        super.onRemoteInvitationRefused(remoteInvitation)
    }

    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?) {
        super.onRemoteInvitationCanceled(remoteInvitation)
    }

    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int) {
        super.onRemoteInvitationFailure(remoteInvitation, errorCode)
        Log.w("BaseActivity", "onRemoteInvitationFailure:$errorCode")
    }

    fun gotoAudioActivity(channel: String?, peer: String?) {
        val intent = Intent(this, AudioActivity::class.java)
        intent.putExtra(Constants.KEY_CALLING_CHANNEL, channel)
        intent.putExtra(Constants.KEY_CALLING_PEER, peer)
        startActivity(intent)
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private val TAG = BaseCallActivity::class.java.simpleName
    }
}