package com.uni.justservices.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uni.justservices.R
import com.uni.justservices.data.NavigationDirectionEnum
import com.uni.justservices.ui.MainActivity
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val ADMIN_CHANNEL_ID = "admin_channel"
    private var localBroadcast: LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()
        localBroadcast = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)

        setupChannels(notificationManager)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(message?.data?.get("title"))
            .setContentText(message?.data?.get("message"))
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)
        //notificationManager.cancel(notificationID)
        notificationManager.notify(notificationID, notificationBuilder.build())

        if (message?.data?.get("title") == "New Message") {
            val intentBroadcast = Intent("MyData")
            intentBroadcast.putExtra("updateChat", true)
            localBroadcast?.sendBroadcast(intentBroadcast)
        }else{
            val intentBroadcast = Intent("MyData")
            intentBroadcast.putExtra("updateNotification", true)
            localBroadcast?.sendBroadcast(intentBroadcast)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = "New notification"
        val adminChannelDescription = "Device to devie notification"

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }


}