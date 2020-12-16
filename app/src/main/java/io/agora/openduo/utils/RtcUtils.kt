package io.agora.openduo.utils

object RtcUtils {
    fun channelName(myUid: String, peerUid: String): String {
        return myUid + peerUid
    }
}