package com.uni.justservices.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.Chat
import com.uni.justservices.data.Data
import com.uni.justservices.data.User
import com.uni.justservices.data.UserType
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.FragmentHomeBinding

import com.uni.justservices.ui.base.BaseFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var user:User
    private val navArgs:HomeFragmentArgs by navArgs()
    private lateinit var requestPermissionLauncherNotification: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        requestPermissionLauncherNotification = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted->
            if (isGranted){
                //done
            }else{
                showPermissionDialog()
            }

        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideToolbar(show = true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //show allow notification dialog:
            checkNotificationPermission()
        }
        fetchUserProfile()

        navArgs.userType.let {type->
            when(type){
                UserType.Student-> {
                    binding.isStudent = true
                    activeBadge()
                }
                UserType.Expert-> {
                    binding.isExpert = true
                    activeBadge()
                }
                UserType.BusDriver->binding.isDriver = true
                UserType.Non->{
                    runBlocking {
                        val localDB = UserLocalDataSource(requireContext())
                        val user = localDB.getUserData()
                        when(user?.categoryTypeID){
                            UserType.Student.getID()-> {
                                binding.isStudent = true
                                activeBadge()
                            }
                            UserType.Expert.getID()-> {
                                binding.isExpert = true
                                activeBadge()
                            }
                            UserType.BusDriver.getID()->binding.isDriver = true
                        }
                    }
                }
            }
        }

        binding.studentContainer.askCardContainer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToExpertListFragment())
        }
        binding.studentContainer.busCardContainer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStudentBusScheduleFragment())
        }
        binding.driverContainer.AddTripCardContainer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddTripFragment())
        }
        binding.driverContainer.tripListCardContainer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTripListFragment())
        }

        binding.expertContainer.guidanceTripCardContainer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStudentRequestsFragment())
        }
        binding.expertContainer.noteCardContainer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNotesFragment())
        }

    }

    private fun activeBadge(){
        fetchNotificationLiveData()
        fetchChatLiveData()
    }

    private fun checkNotificationPermission(){
        when{
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                Log.e("notifications", "User accepted the notifications!")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->{
                Toast.makeText(requireContext(),"The user denied the notifications ):", Toast.LENGTH_SHORT).show()
                requestPermissionLauncherNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
            }else->
            requestPermissionLauncherNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showPermissionDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Alert for permission")
            .setPositiveButton("Settings"){ dialog,which->
                //go to settings:
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri =
                    Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()

            }
            .setNegativeButton("Dismiss"){ dialog,which->
                dialog.dismiss()
            }
            .show()
    }

    private fun fetchNotificationLiveData(){
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?:""
        val databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(currentUserID).child("notification")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.getValue<HashMap<String, Data>>()?.size ?: 0
                    updateNotificationBadge(count)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToastMsg(error.message)
            }

        })

    }

    private fun fetchChatLiveData(){
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?:""
        val databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(currentUserID).child("Chat")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.getValue<HashMap<String,HashMap<String, Chat>>>()?.size ?: 0
                    updateChatBadge(count)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToastMsg(error.message)
            }

        })

    }
}