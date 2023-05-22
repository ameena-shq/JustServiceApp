package com.uni.justservices.ui.home.expert

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uni.justservices.data.User
import com.uni.justservices.databinding.StudentReqLayoutBinding

class StudentRequestAdapter(val context: Context):RecyclerView.Adapter<StudentRequestAdapter.MyViewHolder>() {
    private val studentReqList = mutableListOf<User>()
    private lateinit var listener:RequestListener

    interface RequestListener{
        fun accept(user: User)
        fun delete(user: User)
    }
    fun setupListener(listener:RequestListener){
        this.listener = listener
    }

    fun setItems(list:List<User>){
        studentReqList.clear()
        studentReqList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val request = studentReqList[position]
        holder.userName.text = "${request.details?.firstName} ${request.details?.lastName}"
        if (request.img.isNotEmpty())
            Glide.with(context)
                .load(request.img)
                .into(holder.profileImg)

        holder.acceptBtn.setOnClickListener {
            listener.accept(request)
        }
        holder.deleteBtn.setOnClickListener {
            listener.delete(request)
        }
    }

    inner class MyViewHolder(view:StudentReqLayoutBinding):RecyclerView.ViewHolder(view.root){
        val profileImg = view.profileImg
        val userName = view.userName
        val acceptBtn = view.acceptBtn
        val deleteBtn = view.deleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(StudentReqLayoutBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return studentReqList.size
    }


}