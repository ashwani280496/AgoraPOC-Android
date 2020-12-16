package io.agora.openduo.agora

import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtm.*
import java.util.*

class EngineEventListener : IRtcEngineEventHandler(), RtmClientListener, RtmCallEventListener {
    private val mListeners: MutableList<IEventListener> = ArrayList()
    fun registerEventListener(listener: IEventListener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener)
        }
    }

    fun removeEventListener(listener: IEventListener?) {
        mListeners.remove(listener)
    }

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onJoinChannelSuccess(channel, uid, elapsed)
        }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onUserJoined(uid, elapsed)
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onUserOffline(uid, reason)
        }
    }

    override fun onConnectionStateChanged(status: Int, reason: Int) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onConnectionStateChanged(status, reason)
        }
    }

    override fun onLeaveChannel(stats: RtcStats?) {
        val size = mListeners.size
        if (size > 0) {
            if (stats != null) {
                mListeners[size - 1].onLeaveChannel(stats)
            }
        }
    }

    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String) {}
    override fun onTokenExpired() {}
    override fun onPeersOnlineStatusChanged(map: Map<String, Int>) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onPeersOnlineStatusChanged(map)
        }
    }

    override fun onLocalInvitationReceivedByPeer(localInvitation: LocalInvitation) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onLocalInvitationReceived(localInvitation)
        }
    }

    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation, response: String) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onLocalInvitationAccepted(localInvitation, response)
        }
    }

    override fun onLocalInvitationRefused(localInvitation: LocalInvitation, response: String) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onLocalInvitationRefused(localInvitation, response)
        }
    }

    override fun onLocalInvitationCanceled(localInvitation: LocalInvitation) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onLocalInvitationCanceled(localInvitation)
        }
    }

    override fun onLocalInvitationFailure(localInvitation: LocalInvitation, errorCode: Int) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onLocalInvitationFailure(localInvitation, errorCode)
        }
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onRemoteInvitationReceived(remoteInvitation)
        }
    }

    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onRemoteInvitationAccepted(remoteInvitation)
        }
    }

    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onRemoteInvitationRefused(remoteInvitation)
        }
    }

    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onRemoteInvitationCanceled(remoteInvitation)
        }
    }

    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation, errorCode: Int) {
        val size = mListeners.size
        if (size > 0) {
            mListeners[size - 1].onRemoteInvitationFailure(remoteInvitation, errorCode)
        }
    }
}