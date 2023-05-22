package com.uni.justservices.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.data.LocalUser
import com.uni.justservices.data.User
import com.uni.justservices.data.UserType
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.FragmentMyProfileBinding
import com.uni.justservices.ui.base.BaseFragment
import com.uni.justservices.ui.notification.NotificationFragmentDirections
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ProfileFragment : BaseFragment() {
    private lateinit var binding:FragmentMyProfileBinding
    private var user:LocalUser?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()

        binding.logoutBtn.setOnClickListener {
            runBlocking {
                UserLocalDataSource(requireContext()).clearUserData()
                FirebaseAuth.getInstance().signOut()
                relaunchApp()
            }

        }
    }

    private fun fetchData(){
        lifecycleScope.launch {
            val localDB = UserLocalDataSource(requireContext())
            user = localDB.getUserData()
            user?.let { data->
                if (data.img.isNotEmpty())
                    Glide.with(requireContext())
                        .load(data.img)
                        .into(binding.profileImg)
                binding.userName.text = "${data?.details?.firstName} ${data?.details?.lastName}"
                binding.ageValue.text = data.details?.age
                binding.gender.text = data.details?.gender
                when(data.categoryTypeID){
                    UserType.Student.getID()->{
                        binding.isStudent = true
                        binding.studentView.academicYearValue.text = data.details?.academicYear
                        binding.studentView.majorValue.text = data.details?.major
                    }
                    UserType.Expert.getID()->{
                        binding.isExpert = true
                        binding.expertView.majorValue.text = data.details?.major
                        binding.expertView.bioValue.text = data.details?.bio
                    }
                    UserType.BusDriver.getID()->{
                        binding.isDriver = true
                        binding.driverView.busNumValue.text = data.details?.busNumber
                    }
                }
            }
        }

    }

}