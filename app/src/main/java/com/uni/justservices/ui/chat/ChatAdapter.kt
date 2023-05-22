package com.uni.justservices.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uni.justservices.data.Chat
import com.uni.justservices.databinding.MyChatLayoutBinding
import com.uni.justservices.databinding.OtherChatLayoutBinding

class ChatAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val chatList = mutableListOf<Chat>()
    private lateinit var currentUserId:String

    fun setCurrentUser(user:String){
        currentUserId = user
    }
    fun setList(list: List<Chat>){
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MY_CHAT->{
                MyChatViewHolder(MyChatLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),parent,false
                ))
            }
            OTHER_CHAT->{
                OtherChatViewHolder(OtherChatLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),parent,false
                ))
            }
            else -> {
                MyChatViewHolder(MyChatLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),parent,false
                ))
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatList[position]
        if (holder is MyChatViewHolder){
            holder.message.text = item.message
        }else if (holder is OtherChatViewHolder){
            holder.message.text = item.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].userId == currentUserId)
            MY_CHAT
        else
            OTHER_CHAT
    }

    inner class MyChatViewHolder(view:MyChatLayoutBinding):RecyclerView.ViewHolder(view.root){
        val message = view.msgTV
    }

    inner class OtherChatViewHolder(view:OtherChatLayoutBinding):RecyclerView.ViewHolder(view.root){
        val message = view.msgTV
    }

    companion object{
        const val MY_CHAT = 0
        const val OTHER_CHAT = 1
    }


}