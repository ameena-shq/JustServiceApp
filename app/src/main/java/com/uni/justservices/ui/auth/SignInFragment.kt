package com.uni.justservices.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.LocalUser
import com.uni.justservices.data.User
import com.uni.justservices.data.UserType
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.FragmentSignInBinding
import com.uni.justservices.ui.base.BaseFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SignInFragment : BaseFragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var mAuth:FirebaseAuth
    //private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef:DatabaseReference
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
        showHideToolbar(show = false)
        mAuth = FirebaseAuth.getInstance()
        binding.signInBtn.setOnClickListener {
            signIn()
            //findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToCreateUserProfileFragment(UserType.BusDriver))
        }
        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }

        binding.forgotPassBtn.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment())
        }

    }

    private fun signIn() {
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()
        if (email.trim().isBlank()) {
            showToastMsg(resources.getString(R.string.email_error))
            return
        }
        if (password.trim().isBlank()) {
            showToastMsg(resources.getString(R.string.password_error))
            return
        }

        showHideProgressBar(true)
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goTo()
                    /*if (mAuth.currentUser?.isEmailVerified == true) {
                        goTo()
                    }
                    else{
                        showToastMsg(getString(R.string.verify_email_msg))
                    }*/
                }
            }
            .addOnFailureListener { exception ->
                showHideProgressBar(false)
                exception.localizedMessage?.let { showToastMsg(it) }
            }


    }

    private fun resendLink(){
        mAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                showHideProgressBar(false)
                showToastMsg(getString(R.string.verify_email_msg))
            }
            ?.addOnFailureListener {exception->
                showHideProgressBar(false)
                showToastMsg(exception.localizedMessage)
            }
    }

    //go to homePage or createAccountPage
    private fun goTo(){
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(mAuth.currentUser?.uid?:"")
        databaseRef.addListenerForSingleValueEvent( object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //showHideProgressBar(false)
                val user = snapshot.getValue(User::class.java)
                //fill user token
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener {task->
                        if (task.isSuccessful){
                            val token = task.result
                            Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
                                .child(mAuth.currentUser?.uid?:"").child("token").setValue(token)
                                .addOnCompleteListener { task->
                                    if (task.isSuccessful){
                                        showHideProgressBar(false)
                                        user?.let {
                                            when(it.categoryTypeID){
                                                UserType.Student.getID()->handelStudentFlow(it)
                                                UserType.Expert.getID()->handelExpertFlow(it)
                                                UserType.BusDriver.getID()->handelBusDriverFlow(it)
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { ex->
                                    showToastMsg(ex.localizedMessage)
                                }

                        }
                    }
                    .addOnFailureListener { ex->
                        showToastMsg(ex.localizedMessage)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                showHideProgressBar(false)
                showToastMsg(error.message)
            }

        })
    }

    private fun handelStudentFlow(user: User){
        user.details?.let {
            //go to home
            runBlocking {
                val localUser = LocalUser(user.userId,user.userName,user.categoryTypeID,user.img,user.details)
                UserLocalDataSource(requireContext()).saveUserData(localUser)
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment(userType=UserType.Student))
            }
        } ?:let {
            //go to create account
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToCreateUserProfileFragment(userType=UserType.Student))
        }
    }

    private fun handelExpertFlow(user: User){
        user.details?.let {
            //go to home
            runBlocking {
                val localUser = LocalUser(
                    user.userId,
                    user.userName,
                    user.categoryTypeID,
                    user.img,
                    user.details
                )
                UserLocalDataSource(requireContext()).saveUserData(localUser)
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment(userType=UserType.Expert))
            }
        } ?:let {
            //go to create account
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToCreateUserProfileFragment(userType=UserType.Expert))
        }
    }

    private fun handelBusDriverFlow(user: User){
        user.details?.let {
            //go to home
            runBlocking {
                val localUser = LocalUser(user.userId,user.userName,user.categoryTypeID,user.img,user.details)
                UserLocalDataSource(requireContext()).saveUserData(localUser)
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment(userType=UserType.BusDriver))
                }
        } ?:let {
            //go to create account
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToCreateUserProfileFragment(userType=UserType.BusDriver))
        }
    }

    override fun onStop() {
        super.onStop()
        /*if (this::databaseRef.isInitialized && this::eventListener.isInitialized)
            databaseRef.removeEventListener(eventListener)*/
    }

    
}