package io.agora.openduo.DataLayer

import com.google.gson.JsonObject
import io.agora.openduo.DataLayer.Models.StaffResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
/**
 * Created by Ashwani Shakya on 29/04/20.
 * Define here your Api calls
 */
interface NestapApis {
    // This call for registering device
    @POST("register-voip-device/")
    fun loginNFetchUsers(@Body body: JsonObject): Call<StaffResponse>

    @GET("voip-devices/")
    fun fetchUsers():Call<StaffResponse>

    @POST("initiate-voip-call/")
    fun initVoIP(@Body body:JsonObject):Call<JsonObject>
}