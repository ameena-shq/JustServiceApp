package com.uni.justservices.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.uni.justservices.R
import com.uni.justservices.data.*
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.services.SendNotification
import com.uni.justservices.ui.AppListener
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

open class BaseFragment : Fragment() {
    private lateinit var listener: AppListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppListener){
            listener = context
        }else{
            throw RuntimeException("$context must implement AppListener")
        }
    }

    fun showToastMsg(msg:String){
        listener.showToastMsg(msg)
    }

    fun showHideToolbar(show:Boolean){
        listener.showHideToolbar(show)
    }

    fun showHideProgressBar(show: Boolean){
        listener.showHideProgress(show)
    }

    fun relaunchApp(){
        listener.relaunchApp()
    }

    fun fetchUserProfile(){
        listener.fetchUserProfileImg()
    }

    fun updateNotificationBadge(count:Int){
        listener.updateNotificationBadge(count)
    }

    fun updateChatBadge(count:Int){
        listener.updateChatBadge(count)
    }

    fun sendNotification(token:String,toID:String,user:User?,notificationType:Int,title:String,message:String){
        val time = SimpleDateFormat("dd,MMMM,yyyy HH:mm").format(Date(System.currentTimeMillis()))
        val notification = MessageBody(token,
            Data(
                notificationType,
                title,
                message,
                user?.userId?:"",
                time,
                user?.img?:""
            )
        )
        SendNotification().sendNotification(notification,toID)
    }
}