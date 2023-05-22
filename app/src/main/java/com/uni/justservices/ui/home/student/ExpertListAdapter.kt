package com.uni.justservices.ui.home.student

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uni.justservices.data.RequestStatusEnum
import com.uni.justservices.data.User
import com.uni.justservices.databinding.ExpertViewBinding

class ExpertListAdapter(val context: Context): RecyclerView.Adapter<ExpertListAdapter.MyViewHolder>() {

    private val expertList = mutableListOf<User>()
    private lateinit var currentUser:User
    private lateinit var listener:SendRequestListener

    interface SendRequestListener{
        fun sendRequest(user: User)
        fun startChat(user: User)
    }
    fun setupListener(listener:SendRequestListener){
        this.listener = listener
    }

    fun setItems(list:List<User>){
        expertList.clear()
        expertList.addAll(list)
        notifyDataSetChanged()
    }
    fun setCurrentUser(user: User){
        this.currentUser = user
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = expertList[position]
        holder.userName.text = "${user?.details?.firstName} ${user?.details?.lastName}"
        if (user.img.isNotEmpty())
            Glide.with(context)
                .load(user.img)
                .into(holder.userImg)

        val request = currentUser.requestListStatus?.map { it.value }?.find {it.userId == user.userId  }
        request?.let {status->
            when(status.status){
                RequestStatusEnum.Pending.getID()->{
                    holder.requestBtn.isVisible = false
                    holder.chatBtn.isVisible = false
                }
                RequestStatusEnum.Friends.getID()->{
                    holder.requestBtn.isVisible = false
                    holder.chatBtn.isVisible = true
                }
                RequestStatusEnum.Non.getID()->{
                    holder.requestBtn.isVisible = true
                    holder.chatBtn.isVisible = false
                }
            }

        }?:let {
            holder.requestBtn.isVisible = true
            holder.chatBtn.isVisible = false
        }

        holder.requestBtn.setOnClickListener {
            listener.sendRequest(user)
        }
        holder.chatBtn.setOnClickListener {
            listener.startChat(user)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ExpertViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return expertList.size
    }



    inner class MyViewHolder(view: ExpertViewBinding):RecyclerView.ViewHolder(view.root){
        val userImg = view.profileImg
        val userName = view.userName
        val requestBtn = view.sendRequestBtn
        val chatBtn = view.chatBtn
    }
}