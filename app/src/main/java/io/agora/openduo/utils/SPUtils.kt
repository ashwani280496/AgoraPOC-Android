package io.agora.openduo.utils

import android.content.Context
import io.agora.openduo.Constants

object SPUtils {
    fun saveUserId(context: Context, id: String?) {
        val pf = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
        pf.edit().putString(Constants.PREF_USER_ID, id).apply()
    }

    fun getUserId(context: Context): String? {
        val pf = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
        return pf.getString(Constants.PREF_USER_ID, null)
    }
}