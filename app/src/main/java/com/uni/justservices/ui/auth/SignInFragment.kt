package com.uni.justservices.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.uni.justservices.R
import com.uni.justservices.databinding.FragmentSignInBinding
import com.uni.justservices.ui.base.BaseFragment



class SignInFragment : BaseFragment() {
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInBtn.setOnClickListener {
            signIn()
        }
        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
    }

    private fun signIn(){
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()
        if (email.trim().isBlank()){
            showToastMsg(resources.getString(R.string.email_error))
            return
        }
        if (password.trim().isBlank()){
            showToastMsg(resources.getString(R.string.password_error))
            return
        }

        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    showToastMsg("login")
                }
            }
            .addOnFailureListener {exception->
                exception.localizedMessage?.let { showToastMsg(it) }
            }
    }


}