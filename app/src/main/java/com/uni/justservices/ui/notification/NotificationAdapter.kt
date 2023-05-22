package com.uni.justservices.ui.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uni.justservices.data.Data
import com.uni.justservices.data.MessageBody
import com.uni.justservices.data.NotificationType
import com.uni.justservices.data.User
import com.uni.justservices.databinding.NotificationViewBinding

class NotificationAdapter(val context: Context):RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    private val notificationList = mutableListOf<Data>()
    private lateinit var listener :NotificationListener

    interface NotificationListener{
        fun onItemClicked(userID: String)
    }

    fun setupListener(listener :NotificationListener){
        this.listener = listener
    }

    fun setItems(list: List<Data>){
        notificationList.clear()
        notificationList.addAll(list)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notification = notificationList[position]
        if (notification.userProfileImg.isNotEmpty())
            Glide.with(context)
                .load(notification.userProfileImg)
                .into(holder.profileImg)
        holder.notificationTV.text = notification.message
        holder.timeTV.text = notification.time

        holder.itemView.setOnClickListener {
            if (notification.notificationType == NotificationType.ACCEPT_REQUEST.getID()
                || notification.notificationType == NotificationType.CHAT.getID())
                listener.onItemClicked(notification.userID)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(NotificationViewBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }



    inner class MyViewHolder(view:NotificationViewBinding):RecyclerView.ViewHolder(view.root){
        val notificationTV = view.notificationTxt
        val timeTV = view.date
        val profileImg = view.profileImg
    }

}