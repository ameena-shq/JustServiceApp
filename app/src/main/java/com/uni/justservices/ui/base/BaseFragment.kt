package com.uni.justservices.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.uni.justservices.ui.AppListener

open class BaseFragment : Fragment() {
    private lateinit var listener: AppListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppListener){
            listener = context
        }else{
            throw RuntimeException("$context must implement AppListener")
        }
    }

    fun showToastMsg(msg:String){
        listener.showToastMsg(msg)
    }
}