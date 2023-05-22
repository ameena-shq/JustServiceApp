package com.uni.justservices.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.uni.justservices.R
import com.uni.justservices.databinding.FragmentForgotPasswordBinding
import com.uni.justservices.ui.base.BaseFragment


class ForgotPasswordFragment : BaseFragment() {
    private lateinit var binding:FragmentForgotPasswordBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideToolbar(show = false)
        mAuth = FirebaseAuth.getInstance()
        binding.resetBtn.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword(){
        val email = binding.emailET.text.toString()
        if (email.trim().isEmpty()){
            showToastMsg(resources.getString(R.string.email_error))
            return
        }
        showHideProgressBar(true)
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task->
                showHideProgressBar(false)
                if (task.isSuccessful) {
                    showToastMsg(getString(R.string.check_email_msg))
                    findNavController().navigateUp()
                }
            }
            .addOnFailureListener {ex->
                showHideProgressBar(true)
                showToastMsg(ex.localizedMessage)
            }

    }

}