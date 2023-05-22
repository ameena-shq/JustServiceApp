package com.uni.justservices.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObject {

    private var baseURl = "https://fcm.googleapis.com/"

    private var okHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)

    private var retrofit : Retrofit?=null

    private fun createRetrofitObject():Retrofit?{
        if (retrofit == null){
            val retrofitBuilder = Retrofit.Builder()
                .baseUrl(baseURl)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            retrofit = retrofitBuilder.build()
        }
        return retrofit
    }

    fun <T> buildService(serviceType: Class<T>):T{
        createRetrofitObject()
        return retrofit?.create(serviceType)!!
    }
}