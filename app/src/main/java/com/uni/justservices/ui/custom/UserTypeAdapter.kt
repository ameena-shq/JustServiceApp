package com.uni.justservices.ui.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.uni.justservices.R
import com.uni.justservices.data.UserType

class UserTypeAdapter(
    mContext: Context,
    private val mLayoutResourceId: Int,
    list: List<UserType>
):ArrayAdapter<UserType>(mContext,mLayoutResourceId,list) {
    private val typeList :MutableList<UserType> = ArrayList(list)

    override fun getCount(): Int {
        return typeList.size
    }

    override fun getItem(position: Int): UserType {
        return typeList[position]
    }

    override fun getItemId(position: Int): Long {
        return typeList[position].getID().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null){
            val inflater = LayoutInflater.from(parent.context)
            convertView = inflater.inflate(mLayoutResourceId,parent,false)
        }
        val user = typeList[position]
        val textName = convertView?.findViewById<View>(R.id.title) as TextView
        textName.text = parent.context.getString(user.getName())

        return convertView
    }
}