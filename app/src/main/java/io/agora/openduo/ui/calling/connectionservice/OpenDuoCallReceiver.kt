package io.agora.openduo.ui.calling.connectionservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.CallLog
import android.util.Log
import io.agora.openduo.Constants
import io.agora.openduo.activities.DialerActivity
import io.agora.openduo.activities.MainActivity

class OpenDuoCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val subscriber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
        Log.d(TAG, "PROCESS_OUT_GOING_CALL received, Phone number: $subscriber")
        if (subscriber?.let { shouldIntercept(context, it) }!!) {
            if (false) { // DialerActivity.running == true
                val agIntent = Intent(context, DialerActivity::class.java)
                agIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                agIntent.putExtra(Constants.CS_KEY_SUBSCRIBER, subscriber)
                context.startActivity(agIntent)
            } else {
                val agIntent = Intent(context, MainActivity::class.java)
                agIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(agIntent)
            }
            resultData = null
        }
    }

    private fun shouldIntercept(context: Context, phoneNumber: String): Boolean {
        return try {
            val cursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI,
                    null, CallLog.Calls.NUMBER + "=?", arrayOf(phoneNumber),
                    CallLog.Calls.DATE + " DESC")
            val phoneAccountID = cursor!!.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
            var shouldIntercept = false
            while (cursor.moveToNext()) {
                val phoneAccID = cursor.getString(phoneAccountID)
                Log.d(TAG, "phone number: $phoneNumber phoneAccountID: $phoneAccID")
                shouldIntercept = phoneAccID == Constants.PA_LABEL_CALL_IN || phoneAccID == Constants.PA_LABEL_CALL_OUT
                if (shouldIntercept) break
            }
            cursor.close()
            shouldIntercept
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private val TAG = OpenDuoCallReceiver::class.java.simpleName
    }
}