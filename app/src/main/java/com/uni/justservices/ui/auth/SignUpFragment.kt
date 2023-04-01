package com.uni.justservices.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.User
import com.uni.justservices.databinding.FragmentSignUpBinding
import com.uni.justservices.ui.base.BaseFragment



class SignUpFragment : BaseFragment() {
    lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpBtn.setOnClickListener {
            //signUp()
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToUserCategoryFragment())
        }

        binding.signInBtn.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
        }
    }

    private fun signUp(){
        val userName = binding.userNameET.text.toString()
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()
        val confirmPassword = binding.confirmPasswordET.text.toString()

        if (userName.trim().isBlank()){
            showToastMsg(resources.getString(R.string.userName_error))
            return
        }
        if (email.trim().isBlank()){
            showToastMsg(resources.getString(R.string.email_error))
            return
        }
        if (password.trim().isBlank()){
            showToastMsg(resources.getString(R.string.password_error))
            return
        }
        if (confirmPassword.trim().isBlank()){
            showToastMsg(resources.getString(R.string.confirmPassword_error))
            return
        }
        if (password.trim() != confirmPassword.trim()){
            showToastMsg(resources.getString(R.string.matchPassword_error))
            return
        }
        if (!binding.checkBox.isChecked){
            showToastMsg(resources.getString(R.string.terms_error))
            return
        }
        showProgress(true)
        val mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    val userId = mAuth.currentUser?.uid?:""
                    saveUserData(userId,userName,email, password)
                }
            }
            .addOnFailureListener {exception->
                showProgress(false)
                showToastMsg(exception.localizedMessage)
            }
    }

    private fun saveUserData(userId:String,userName:String,email:String,password:String){
        val database = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
        val user = User(userName,email,password,userId)
        database.child("user").child(userId).setValue(user)
            .addOnCompleteListener {
                showProgress(false)
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToUserCategoryFragment())
            }
            .addOnFailureListener { exception->
                showProgress(false)
                showToastMsg(exception.localizedMessage)
            }
        /*database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    showToastMsg("Username already exists.")
                }else{
                    database.child(userId).setValue(user)
                        .addOnCompleteListener {
                            showToastMsg("signup successfully")
                        }
                        .addOnFailureListener { exception->
                            showToastMsg(exception.localizedMessage)
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                showToastMsg(error.message)
            }
        })*/

    }

    private fun showProgress(show:Boolean){
        binding.layoutProgress.root.isVisible = show
    }

}