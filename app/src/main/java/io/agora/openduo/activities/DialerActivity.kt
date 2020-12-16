package io.agora.openduo.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import io.agora.openduo.Adapters.UserCellAdapter
import io.agora.openduo.Constants
import io.agora.openduo.DataLayer.AppPreference
import io.agora.openduo.DataLayer.AppPreference.getCurrentUser
import io.agora.openduo.DataLayer.AppPreference.init
import io.agora.openduo.DataLayer.Models.Staff
import io.agora.openduo.DataLayer.Models.StaffResponse
import io.agora.openduo.DataLayer.NestapApis
import io.agora.openduo.DataLayer.RetrofitClientInstance
import io.agora.openduo.R
import io.agora.openduo.utils.RtcUtils
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import kotlinx.android.synthetic.main.activity_dialer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timerTask


class DialerActivity : BaseCallActivity() {
    private var service: NestapApis = RetrofitClientInstance.retrofitInstance!!.create(
            NestapApis::class.java
    )

    var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer)
        fetchUsers()
        initUI()

    }

    private fun initUI() {
        val identifier = findViewById<TextView>(R.id.identifier_text)
        identifier.text = getCurrentUser()?.user_id + "(" + getCurrentUser()?.id.toString()+" )"
    }

    private fun fetchUsers(){
        val call:Call<StaffResponse> = service.fetchUsers()
        call.enqueue(object: Callback<StaffResponse>{
            override fun onResponse(call: Call<StaffResponse>, response: Response<StaffResponse>) {
                AppPreference.saveUsers(response.body()!!)
                val a = response.body()!!

                var validUsers = a.filter { s -> s.user_id != getCurrentUser()?.user_id }

                val llm = LinearLayoutManager(this@DialerActivity)
                llm.orientation = LinearLayoutManager.VERTICAL
                recyclerview.setLayoutManager(llm)
                recyclerview.adapter = UserCellAdapter(this@DialerActivity, validUsers) { callee ->
//                    this.openBottomSheetFragment { AdminDetailsBSD(admin) }
                    Log.e("CALL CLOUSRE#####", callee.user_id)
                  startCall(callee)

                }
            }
            override fun onFailure(call: Call<StaffResponse>, t: Throwable) {
                Log.e("LOGIN API FAILURE#####", t.localizedMessage)
            }
        })
    }

    private fun startCall(user: Staff) {
        val number = user.id
        val peer = number.toString()
        val peerSet: MutableSet<String> = HashSet()
        peerSet.add(peer)
        rtmClient()!!.queryPeersOnlineStatus(peerSet,
                object : ResultCallback<Map<String?, Boolean?>> {
                    override fun onSuccess(statusMap: Map<String?, Boolean?>) {
                        val bOnline = statusMap[peer]
                        if (bOnline != null && bOnline) {
                            val uid = application().config()?.userId.toString()
                            val channel = RtcUtils.channelName(uid, peer)
                            gotoCallingInterface(peer, channel, Constants.ROLE_CALLER)
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@DialerActivity,
                                        R.string.peer_not_online,
                                        Toast.LENGTH_SHORT).show()
                            }

                            initVoIP(user)
                        }
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        Log.i("ErrorInfo #########", errorInfo.errorDescription)
                    }
                })
    }

    private fun gotoLoginActivity() {
        val intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun initVoIP(callee:Staff){

        val param = JsonObject()
        param.addProperty("caller_id", getCurrentUser()?.user_id)
        param.addProperty("callee_id",callee.user_id)

        val call:Call<JsonObject> = service.initVoIP(param)
        call.enqueue(object:Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Init VoIP Error >>>>",t.localizedMessage)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                runOnUiThread {
                    Toast.makeText(this@DialerActivity,
                           "Init VoIP Success",
                            Toast.LENGTH_SHORT).show()
                }
                startCall(callee)
            }
        })

    }

    override fun onGlobalLayoutCompleted() {
//        val dialerLayout = findViewById<DialerLayout>(R.id.dialer_layout)
//        dialerLayout.setActivity(this)
//        dialerLayout.adjustScreenWidth(displayMetrics.widthPixels)
//        val dialerHeight = dialerLayout.height
//        val dialerRemainHeight = displayMetrics.heightPixels - statusBarHeight - dialerHeight
//        val params = dialerLayout.layoutParams as RelativeLayout.LayoutParams
//        params.topMargin = dialerRemainHeight / 4 + statusBarHeight
//        dialerLayout.layoutParams = params
    }

    public override fun rtmClient(): RtmClient? {
        return application().rtmClient()
    }



    fun logoutRTM(view: View) {
//        rtmClient()?.logout(object: ResultCallback<Void>{
//            override fun onSuccess(p0: Void) {
//                AppPreference.setLoggedIn(false)
//                gotoLoginActivity()
//            }
//
//            override fun onFailure(error: ErrorInfo?) {
//                Log.e("LOGOUT FAILURE #####",error.toString() )
//                AppPreference.setLoggedIn(false)
//            }
//        })
        AppPreference.setLoggedIn(false)
        gotoLoginActivity()

        //rtmClient()?.logout()
    }


}