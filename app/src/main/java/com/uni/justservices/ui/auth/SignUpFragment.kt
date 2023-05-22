package com.uni.justservices.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.User
import com.uni.justservices.data.UserDetails
import com.uni.justservices.data.UserType
import com.uni.justservices.databinding.FragmentSignUpBinding
import com.uni.justservices.ui.base.BaseFragment
import com.uni.justservices.ui.custom.SpinnerCustomAdapter
import com.uni.justservices.ui.custom.UserTypeAdapter


class SignUpFragment : BaseFragment() {
    lateinit var binding: FragmentSignUpBinding
    lateinit var mAuth:FirebaseAuth
    private var selectedUserType:UserType ?=null

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
        showHideToolbar(show = false)
        val userType = mutableListOf(UserType.Student,UserType.Expert,UserType.BusDriver)
        val adapter = UserTypeAdapter(requireContext(),R.layout.dropdown_item,userType)
        binding.userTypeET.setAdapter(adapter)
        binding.userTypeET.setOnItemClickListener { parent, view, position, id ->
            selectedUserType = adapter.getItem(position)
        }
        binding.signUpBtn.setOnClickListener {
            signUp()
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
        if (selectedUserType == null){
            showToastMsg(resources.getString(R.string.userType_error_mg))
            return
        }
        if (!binding.checkBox.isChecked){
            showToastMsg(resources.getString(R.string.terms_error))
            return
        }
        showHideProgressBar(true)
        mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    val userId = mAuth.currentUser?.uid?:""
                    saveUserData(userId,userName,email, password,selectedUserType)
                }
            }
            .addOnFailureListener {exception->
                showHideProgressBar(false)
                showToastMsg(exception.localizedMessage)
            }
    }

    private fun saveUserData(userId:String,userName:String,email:String,password:String,type: UserType?){
        val database = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
        val user = User(userName = userName,email = email,password=password,userId = userId, categoryTypeID = type?.getID()
            , details = null)
        database.child("User").child(userId).setValue(user)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    verifyUserEmail()
            }
            .addOnFailureListener { exception->
                showHideProgressBar(false)
                showToastMsg(exception.localizedMessage)
            }
    }

    private fun verifyUserEmail(){
        mAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                showHideProgressBar(false)
                showToastMsg(getString(R.string.verify_email_msg))
                findNavController().navigateUp()
            }
            ?.addOnFailureListener {exception->
                showHideProgressBar(false)
                showToastMsg(exception.localizedMessage)
            }
    }


}