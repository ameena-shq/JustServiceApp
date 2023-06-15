package com.uni.justservices.ui

interface AppListener {
    fun showToastMsg(msg:String)
    fun showHideToolbar(show:Boolean)
    fun showHideProgress(show: Boolean)
    fun relaunchApp()
    fun fetchUserProfileImg()
    //fun updateNotificationBadge(count:Int)
    //fun updateChatBadge(count:Int)
}