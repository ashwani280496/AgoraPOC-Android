package io.agora.openduo.ui.calling.conventional

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import io.agora.openduo.Constants
import io.agora.openduo.R
import io.agora.openduo.activities.DialerActivity
import io.agora.openduo.utils.RtcUtils
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import java.util.*

class DialerLayout : RelativeLayout, View.OnClickListener {
    private inner class CallInputManager {
        var callNumber = 0
            private set
        private var mCount = 0
        fun append(digit: String) {
            if (mCount == MAX_COUNT) return
            if (TextUtils.isDigitsOnly(digit)) {
                mCount++
                callNumber = callNumber * 10 + digitToInt(digit)
                mCallNumberSlots?.get(mCount - 1)!!.text = digit
            }
        }

        fun backspace() {
            if (mCount == 0) return
            mCount--
            callNumber /= 10
            mCallNumberSlots?.get(mCount)!!.text = ""
        }

        private fun digitToInt(digit: String): Int {
            return Integer.valueOf(digit)
        }

        val isValid: Boolean
            get() = mCount == MAX_COUNT

        private val MAX_COUNT = 4
    }

    private var mActivity: DialerActivity? = null
    private var mCallInputManager: CallInputManager? = null
    private var mCallNumberSlots: Array<AppCompatTextView?>? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val view = LayoutInflater.from(context).inflate(
                R.layout.dialer_layout, this, false)
        addView(view)
        mCallInputManager = CallInputManager()
        initDialer()
    }

    fun adjustScreenWidth(width: Int) {
        val callNumberLayout = findViewById<LinearLayout>(R.id.call_number_layout)
        var params = callNumberLayout.layoutParams as LayoutParams
        val margin = width / 10
        params.leftMargin = margin
        params.rightMargin = margin
        callNumberLayout.layoutParams = params
        val dialNumberLayout = findViewById<LinearLayout>(R.id.dial_number_layout)
        params = dialNumberLayout.layoutParams as LayoutParams
        params.leftMargin = margin
        params.rightMargin = margin
        dialNumberLayout.layoutParams = params
    }

    private fun initDialer() {
        mCallNumberSlots = arrayOfNulls(CALL_NUMBER_SLOT_RES.size)
        for (i in mCallNumberSlots!!.indices) {
            mCallNumberSlots!![i] = findViewById(CALL_NUMBER_SLOT_RES[i])
        }
        for (id in DIAL_BUTTON_RES) {
            findViewById<View>(id).setOnClickListener(this)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dial_number_0, R.id.dial_number_1, R.id.dial_number_2, R.id.dial_number_3, R.id.dial_number_4, R.id.dial_number_5, R.id.dial_number_6, R.id.dial_number_7, R.id.dial_number_8, R.id.dial_number_9 -> {
                val digit = view.tag as String
                mCallInputManager!!.append(digit)
            }
            R.id.dialer_start_call -> startCall()
            R.id.dialer_backspace -> mCallInputManager!!.backspace()
        }
    }

    fun setActivity(activity: DialerActivity?) {
        mActivity = activity
    }

    private fun startCall() {
        if (!mCallInputManager!!.isValid) {
            Toast.makeText(context,
                    R.string.incomplete_dial_number,
                    Toast.LENGTH_SHORT).show()
        }
        if (mActivity != null) {
            val number = mCallInputManager!!.callNumber
            val peer = number.toString()
            val peerSet: MutableSet<String> = HashSet()
            peerSet.add(peer)
            mActivity!!.rtmClient()!!.queryPeersOnlineStatus(peerSet,
                    object : ResultCallback<Map<String?, Boolean?>> {
                        override fun onSuccess(statusMap: Map<String?, Boolean?>) {
                            val bOnline = statusMap[peer]
                            if (bOnline != null && bOnline) {
                                val uid = mActivity!!.application().config()?.userId.toString()
                                val channel = RtcUtils.channelName(uid, peer)
                                mActivity!!.gotoCallingInterface(peer, channel, Constants.ROLE_CALLER)
                            } else {
                                mActivity!!.runOnUiThread {
                                    Toast.makeText(mActivity,
                                            R.string.peer_not_online,
                                            Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(errorInfo: ErrorInfo) {
                            Log.i("ErrorInfo #########", errorInfo.errorDescription)
                        }
                    })
        }
    }

    companion object {
        private val CALL_NUMBER_SLOT_RES = intArrayOf(
                R.id.call_number_text1,
                R.id.call_number_text2,
                R.id.call_number_text3,
                R.id.call_number_text4
        )
        private val DIAL_BUTTON_RES = intArrayOf(
                R.id.dial_number_0,
                R.id.dial_number_1,
                R.id.dial_number_2,
                R.id.dial_number_3,
                R.id.dial_number_4,
                R.id.dial_number_5,
                R.id.dial_number_6,
                R.id.dial_number_7,
                R.id.dial_number_8,
                R.id.dial_number_9,
                R.id.dialer_start_call,
                R.id.dialer_backspace
        )
    }
}