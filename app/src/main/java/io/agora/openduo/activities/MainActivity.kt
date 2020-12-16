package io.agora.openduo.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import io.agora.openduo.DataLayer.AppPreference
import io.agora.openduo.DataLayer.Models.StaffResponse
import io.agora.openduo.DataLayer.NestapApis
import io.agora.openduo.DataLayer.RetrofitClientInstance
import io.agora.openduo.OpenDuoApplication
import io.agora.openduo.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : BaseCallActivity() {
    private val PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG)
    private var service: NestapApis = RetrofitClientInstance.retrofitInstance!!.create(
    NestapApis::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()

        if (AppPreference.isLoggedIn()){
            gotoDialerActivity()
        }

    }

    override fun onGlobalLayoutCompleted() {
//        // Move whole layout to the relative middle of screen
//        val layout = findViewById<RelativeLayout>(R.id.content_layout)
//        val visibleHeight = displayMetrics.heightPixels - statusBarHeight
//        val residual = visibleHeight - layout.measuredHeight
//        var params = layout.layoutParams as RelativeLayout.LayoutParams
//        params.topMargin = residual * 2 / 5
//        layout.layoutParams = params

    }


    private fun checkPermissions() {
        if (!permissionArrayGranted(null)) {
            requestPermissions(PERMISSION_REQ_STAY)
        }
    }

    private fun permissionArrayGranted(permissions: Array<String>?): Boolean {
        var permissionArray = permissions
        if (permissionArray == null) {
            permissionArray = PERMISSIONS
        }
        var granted = true
        for (per in permissionArray) {
            if (!permissionGranted(per)) {
                granted = false
                break
            }
        }
        return granted
    }

    private fun permissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(request: Int) {
        ActivityCompat.requestPermissions(this, PERMISSIONS, request)
    }

    private fun login(){
    val userName = user_name_tf.text.toString()
        val jsonParam = JsonObject()
        jsonParam.addProperty("user_id",userName)
        val data = JsonObject()
        data.addProperty("device_id",AppPreference.getFCMDeviceID())
        jsonParam.add("data",data)

        val call :Call<StaffResponse> = service.loginNFetchUsers(jsonParam)
        call.enqueue(object :Callback<StaffResponse>{
            override fun onResponse(call: Call<StaffResponse>, response: Response<StaffResponse>) {
                AppPreference.saveUsers(response.body()!!)
                AppPreference.setLoggedIn(true)
                for (user in response.body()!!){
                    if (user.user_id.equals(user_name_tf.text.toString())){
                        AppPreference.saveCurrentUsers(user)
                    }
                }

                OpenDuoApplication.instance.initEngine()
            }

            override fun onFailure(call: Call<StaffResponse>, t: Throwable) {
                Log.e("LOGIN API FAILURE#####", t.localizedMessage)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQ_FORWARD ||
                requestCode == PERMISSION_REQ_STAY) {
            val granted = permissionArrayGranted(permissions)
            if (granted && requestCode == PERMISSION_REQ_FORWARD) {
               // gotoDialerActivity()
                login()
            } else if (!granted) {
                toastNeedPermissions()
            }
        }
    }

    fun onStartCall(view: View?) {
        requestPermissions(PERMISSION_REQ_FORWARD)
    }

    private fun toastNeedPermissions() {
        Toast.makeText(this, R.string.need_necessary_permissions, Toast.LENGTH_LONG).show()
    }

    fun gotoDialerActivity() {
        val intent = Intent()
        intent.setClass(this, DialerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }



    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Permission request when we want to go to next activity
        // when all necessary permissions are granted.
        private const val PERMISSION_REQ_FORWARD = 1 shl 4

        // Permission request when we want to stay in
        // current activity even if all permissions are granted.
        private const val PERMISSION_REQ_STAY = 1 shl 3
    }
}