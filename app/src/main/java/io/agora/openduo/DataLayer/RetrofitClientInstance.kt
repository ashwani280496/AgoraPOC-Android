package io.agora.openduo.DataLayer

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientInstance {
    var retrofitInstance: Retrofit? = null
    //    private const val BASE_URL = "http://54.251.72.209:49999/api/"
    private const val BASE_URL = "http://13.250.25.252:49999/"

    fun init(context: Context) {

        val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(NetworkConnectionInterceptor(context))
                .addNetworkInterceptor { chain ->
                    val request: Request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json").build()
                    chain.proceed(request)
                }
                .build()
        if (retrofitInstance == null) {
            retrofitInstance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okkHttpclient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
    }
}