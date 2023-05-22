package com.uni.justservices.ui.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.uni.justservices.databinding.DropdownItemBinding

class SpinnerCustomAdapter:BaseAdapter() {
    private var itemList = listOf<String>()

    fun setData(itemList:List<String>){
        this.itemList = itemList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ItemHolder
        if (view == null){
            val inflater = LayoutInflater.from(parent?.context)
            val binding = DropdownItemBinding.inflate(inflater)
            view = binding.root
            holder = ItemHolder()
            holder.title = binding.title
            view.tag = holder
        }else{
            holder = view.tag as ItemHolder
        }
        holder.title?.text = itemList[position]

        return view
    }

    private inner class ItemHolder(){
        var title:TextView?=null
    }
}