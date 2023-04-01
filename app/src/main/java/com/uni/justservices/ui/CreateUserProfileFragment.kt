package com.uni.justservices.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uni.justservices.R
import com.uni.justservices.data.UserType
import com.uni.justservices.databinding.FragmentUserCreateProfileBinding
import com.uni.justservices.ui.start.SpinnerCustomAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 */
class CreateUserProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userType: UserType? = null
    private lateinit var binding:FragmentUserCreateProfileBinding
    private val navArgs: CreateUserProfileFragmentArgs by navArgs()
    private lateinit var spinnerAdapter: SpinnerCustomAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserCreateProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userType = navArgs.userType
        when(userType){
            UserType.Student->{
                binding.isStudent = true
            }
            UserType.Expert->{
                binding.isExpert = true
            }
            UserType.BusDriver->{
                binding.isDriver = true
            }
            else -> {}
        }
        val genderList = listOf("Gender","Male","Female")
        spinnerAdapter = SpinnerCustomAdapter()
        //val genderAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,genderList)
        binding.genderSpinner.adapter = spinnerAdapter
        spinnerAdapter.setData(genderList)
        binding.genderSpinner.setSelection(0)

        binding.createBtn.setOnClickListener {
            findNavController().navigate(CreateUserProfileFragmentDirections.actionCreateUserProfileFragmentToHomeFragment())
        }
    }

}