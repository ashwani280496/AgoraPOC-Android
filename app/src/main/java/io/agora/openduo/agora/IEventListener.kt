package io.agora.openduo.agora

import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation

interface IEventListener {
    fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int)
    fun onUserJoined(uid: Int, elapsed: Int)
    fun onUserOffline(uid: Int, reason: Int)
    fun onLeaveChannel(stats: IRtcEngineEventHandler.RtcStats)
    fun onConnectionStateChanged(status: Int, reason: Int)
    fun onPeersOnlineStatusChanged(map: Map<String, Int>?)
    fun onLocalInvitationReceived(localInvitation: LocalInvitation?)
    fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?)
    fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?)
    fun onLocalInvitationCanceled(localInvitation: LocalInvitation?)
    fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int)
    fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int)
}