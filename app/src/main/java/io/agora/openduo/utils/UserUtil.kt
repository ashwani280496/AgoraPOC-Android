package io.agora.openduo.utils

import java.util.*

object UserUtil {
    private const val MAX_ID_NUMBER = 9999
    fun randomUserId(): String {
        val number = Random().nextInt(MAX_ID_NUMBER)
        var id = ""
        if (number < 1000) {
            id += "0"
        }
        if (number < 100) {
            id += "0"
        }
        if (number < 10) {
            id += "0"
        }
        return id + number
    }
}