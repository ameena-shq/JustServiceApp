package com.uni.justservices.data.remote

import com.uni.justservices.data.MessageBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import java.util.HashMap

interface ApiEndpoints {

    @POST("fcm/send")
    fun sendMessage(
        @HeaderMap header: HashMap<String, String>,
        @Body message: MessageBody
    ):Call<Any>
}