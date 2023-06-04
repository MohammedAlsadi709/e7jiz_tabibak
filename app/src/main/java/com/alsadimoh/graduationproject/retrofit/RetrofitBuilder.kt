package com.alsadimoh.graduationproject.retrofit

import android.util.Log
import com.alsadimoh.graduationproject.classes.CommonConstants
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

   // private val BASE_URL = "https://hajez.foxytech.shop/api/"
  //  private val BASE_URL = "https://hajez.mymatgar.com/api/"
    private val BASE_URL = "https://hajez.momenmusallam.com/api/"

    private fun getRetrofit(): Retrofit {
        try {

            //Interceptor
            //TimeUnit.SECONDS ممكن
            //TimeUnit.MINUTES ممكن
            val builder = OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)

            builder.addInterceptor { chain ->
                val r: Request = chain.request().newBuilder()
                    .addHeader("Authorization", token())
                    .addHeader("Accept", "application/json")
                    .addHeader("firebase_token", firebaseToken())
                    .build()
                chain.proceed(r)
            }


            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                //Interceptor
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } catch (ex: Exception) {
            Log.e("RetrofitBuilder", "getRetrofit: ${ex.message}")
            return getRetrofit()
        }
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)


    //token for Interceptor
    private fun token(): String {
        return "Bearer ${CommonConstants.myShared.getString(CommonConstants.userToken, null)}"
    }

    private fun firebaseToken(): String {
        return "${CommonConstants.myShared.getString(CommonConstants.firebaseToken, null)}"
    }
}