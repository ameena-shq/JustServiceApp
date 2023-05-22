package com.uni.justservices.ui.home.expert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uni.justservices.databinding.NoteLayoutBinding

class NoteAdapter:RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {
    private val noteList = mutableListOf<String>()

    fun setItems(list: List<String>){
        noteList.clear()
        noteList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = noteList[position]
        holder.noteTxt.text = note
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(NoteLayoutBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return noteList.size
    }



    inner class MyViewHolder(view:NoteLayoutBinding):RecyclerView.ViewHolder(view.root){
        val noteTxt = view.note
    }


}