package com.uni.justservices.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.uni.justservices.R
import com.uni.justservices.data.UserType
import com.uni.justservices.databinding.FragmentUserCategoryBinding


class UserCategoryFragment : Fragment() {
    private lateinit var binding:FragmentUserCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserCategoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.studentCard.setOnClickListener {
            findNavController().navigate(UserCategoryFragmentDirections.actionUserCategoryFragmentToCreateUserProfileFragment(
                UserType.Student))
        }
        binding.expertCard.setOnClickListener {
            findNavController().navigate(UserCategoryFragmentDirections.actionUserCategoryFragmentToCreateUserProfileFragment(
                UserType.Expert))
        }
        binding.busDriverCard.setOnClickListener {
            findNavController().navigate(UserCategoryFragmentDirections.actionUserCategoryFragmentToCreateUserProfileFragment(
                UserType.BusDriver))
        }


    }

}