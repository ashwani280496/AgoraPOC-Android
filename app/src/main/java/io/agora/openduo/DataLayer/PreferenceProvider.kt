package io.agora.openduo.DataLayer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import io.agora.openduo.DataLayer.Models.Staff
import io.agora.openduo.DataLayer.Models.StaffResponse
import org.json.JSONException

private const val KEY_LOGGED_IN = "key_is_logged_in"

private const val USERS = "key_staff"
private const val CURRENT_USERS = "key_current_user"
private const val FCM_DEVICE_ID = "fcm_device_id"



object AppPreference {
    private var PRIVATE_MODE = 0
    private const val PREF_NAME = "AgoraVoicePOC"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_LOGGED_IN, false)
    }

    fun saveUsers(users:StaffResponse){
        try {
            val string = Gson().toJson(
                    users,
                    StaffResponse::class.java
            )
            preferences.edit().putString(USERS, string).apply()
        } catch (ex: JSONException) {
            Log.i("JsonException: ", ex.localizedMessage)
        }
    }

    fun saveCurrentUsers(user:Staff){
        try {
            val string = Gson().toJson(
                    user,
                    Staff::class.java
            )
            preferences.edit().putString(CURRENT_USERS, string).apply()
        } catch (ex: JSONException) {
            Log.i("JsonException: ", ex.localizedMessage)
        }
    }

    fun getCurrentUser():Staff?{
        return try {
            val string = preferences.getString(CURRENT_USERS, "")
            return Gson().fromJson(
                    string,
                    Staff::class.java
            )
        } catch (ex: JSONException) {
            Log.i("JsonException: ", ex.localizedMessage)
            null
        }
    }

    fun getUsers():StaffResponse?{
        return try {
            val string = preferences.getString(USERS, "")
            return Gson().fromJson(
                    string,
                    StaffResponse::class.java
            )
        } catch (ex: JSONException) {
            Log.i("JsonException: ", ex.localizedMessage)
            null
        }
    }

    fun saveFCMDeviceID(deviceID: String) {
        preferences.edit().putString(FCM_DEVICE_ID, deviceID).apply()
    }

    fun getFCMDeviceID(): String? {
        return preferences.getString(FCM_DEVICE_ID, "")
    }
}