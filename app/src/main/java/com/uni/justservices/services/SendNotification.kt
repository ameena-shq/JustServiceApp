package com.uni.justservices.services

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.data.MessageBody
import com.uni.justservices.data.NotificationType
import com.uni.justservices.data.remote.ApiEndpoints
import com.uni.justservices.data.remote.RetrofitObject
import com.uni.justservices.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendNotification {

    fun sendNotification(message:MessageBody,userId:String){
        val authMap = HashMap<String,String>()
        authMap["Authorization"]="key=AAAAiZb4a0g:APA91bFMVRYCaYSIuBq-oYyP2eqPGxnwVTlMP0ijlQvyrjZgzO1jjMRSRL-xaGdmYg8wcAdl3d4fo5zVBjC5A93cAMtYeMz7Dh2jKyoMBhfyOub55ekzcpfse0LG2Tnn76LUZpZWPRiz"
        val service = RetrofitObject.buildService(ApiEndpoints::class.java)
        val apiCall = service.sendMessage(authMap,message)
        apiCall.enqueue(object :Callback<Any>{
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                //save notification:
                when(message.data?.notificationType){
                    NotificationType.CHAT.getID()->{
                        //don't save notification
                    }else->{
                        val notificationId = System.currentTimeMillis().toString()
                        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
                            .child("User").child(userId).child("notification")
                            .child(notificationId)
                            .setValue(message.data)
                            .addOnCompleteListener {  }
                            .addOnFailureListener {  }
                    }

                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                val x = t.message
            }

        })

    }
}