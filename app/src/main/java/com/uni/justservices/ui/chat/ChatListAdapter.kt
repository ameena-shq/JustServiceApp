package com.uni.justservices.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uni.justservices.data.ChatData
import com.uni.justservices.databinding.ChatListViewBinding

class ChatListAdapter(val context: Context):RecyclerView.Adapter<ChatListAdapter.MyViewHolder>() {
    private val chatList= mutableListOf<ChatData>()
    private lateinit var listener :ChatListener

    interface ChatListener{
        fun onItemClicked(userID: String)
    }

    fun setupListener(listener : ChatListener){
        this.listener = listener
    }

    fun setItems(list:List<ChatData>){
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userChat = chatList[position]
        if (userChat.img.isNotEmpty())
            Glide.with(context)
                .load(userChat.img)
                .into(holder.profileImg)
        holder.userNameTxt.text = userChat.userName
        holder.lastChat.text = userChat.lastChat

        holder.itemView.setOnClickListener {
            listener.onItemClicked(chatList[position].userId)
        }
        //holder.time.text = userChat.date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ChatListViewBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return chatList.size
    }



    inner class MyViewHolder(view:ChatListViewBinding):RecyclerView.ViewHolder(view.root){
        val profileImg = view.profileImg
        val userNameTxt = view.userNameTxt
        val lastChat = view.lastChat
        val time = view.time
    }
}