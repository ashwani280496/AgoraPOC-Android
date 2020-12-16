package io.agora.openduo.ui.calling.connectionservice

import android.telecom.PhoneAccount

class OpenDuoCallSession {
    var phoneAccountOut: PhoneAccount? = null
    var phoneAccountIn: PhoneAccount? = null
    var connection: OpenDuoConnection? = null

}