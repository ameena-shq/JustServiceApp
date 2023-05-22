package com.uni.justservices.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.User
import com.uni.justservices.databinding.FragmentExpertProfileBinding
import com.uni.justservices.ui.base.BaseFragment
import com.uni.justservices.ui.notification.NotificationFragmentDirections

class ExpertProfileFragment : BaseFragment() {
    private lateinit var binding:FragmentExpertProfileBinding
    private lateinit var expert:User
    private val navArgs:ExpertProfileFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExpertProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expert = navArgs.selectedUser
        binding.userName.text = "${expert?.details?.firstName} ${expert?.details?.lastName}"
        binding.majorValue.text = expert.details?.major
        binding.bioValue.text = expert.details?.bio
        if (expert.img.isNotEmpty())
            Glide.with(requireContext())
                .load(expert.img)
                .into(binding.profileImg)

        binding.chatBtn.setOnClickListener {
            openChatFragment(expert.userId)
        }
    }

    private fun openChatFragment(userID: String){
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val data = snapshot.getValue(User::class.java)
                        data?.let {
                            findNavController().navigate(ExpertProfileFragmentDirections.actionExpertProfileFragmentToChatFragment(it))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToastMsg(error.message)
                }

            })
    }

}