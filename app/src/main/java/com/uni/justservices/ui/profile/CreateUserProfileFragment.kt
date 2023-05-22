package com.uni.justservices.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.LocalUser
import com.uni.justservices.data.User
import com.uni.justservices.data.UserType
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.FragmentUserCreateProfileBinding
import com.uni.justservices.ui.base.BaseFragment
import com.uni.justservices.ui.custom.SpinnerCustomAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 */
class CreateUserProfileFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var userType: UserType? = null
    private lateinit var binding:FragmentUserCreateProfileBinding
    private val navArgs: CreateUserProfileFragmentArgs by navArgs()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var profileImgUrl = ""
    private var selectedGender:String?=null
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserCreateProfileBinding.inflate(inflater,container,false)
        launcher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode == Activity.RESULT_OK) {
                showHideProgressBar(true)
                val uri = result.data?.data
                val fileName = UUID.randomUUID().toString() +".jpg"
                val uploadTask = FirebaseStorage.getInstance().reference
                    .child("images/$fileName")
                uri?.let {fileUri->
                    uploadTask.putFile(fileUri)
                        .addOnSuccessListener {taskSnapshot->
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener{
                                showHideProgressBar(false)
                                val imageUrl = it.toString()
                                profileImgUrl = imageUrl
                                Glide.with(requireContext())
                                    .load(imageUrl)
                                    .into(binding.profileImg)
                            }
                    }
                        .addOnFailureListener{exception->
                            showHideProgressBar(false)
                            showToastMsg(exception.localizedMessage)
                        }
                }


            }

        }
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideToolbar(show = false)
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
        val genderList = listOf("Male","Female")
        val genderAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,genderList)
        binding.genderSpinner.setAdapter(genderAdapter)
        binding.genderSpinner.setOnItemClickListener { parent, view, position, id ->
            selectedGender = genderAdapter.getItem(position)
        }
        binding.createBtn.setOnClickListener {
            createUser()
            //findNavController().navigate(CreateUserProfileFragmentDirections.actionCreateUserProfileFragmentToHomeFragment(userType=UserType.BusDriver))
        }
        binding.uploadImg.setOnClickListener {
            selectImg()
        }

    }


    private fun selectImg() {
        try {
            val galleryIntent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            launcher.launch(
                galleryIntent)
        } catch (ex: Exception) {
            ex.toString()
        }
    }


    private fun createUser(){
        val firstName = binding.firstNameET.text.toString()
        val lastName = binding.lastNameET.text.toString()
        val age = binding.ageET.text.toString()
        val academicYear = binding.academicYearET.text.toString()
        val major = binding.majorET.text.toString()
        val bio = binding.bioET.text.toString()
        val busNum = binding.busNumET.text.toString()
        when(userType){
            UserType.Student->{
                if (firstName.trim().isBlank() || lastName.trim().isBlank() || age.trim().isBlank()|| selectedGender == null
                    || academicYear.trim().isBlank() || major.trim().isBlank()){
                    showToastMsg(resources.getString(R.string.empty_field_error))
                    return
                }
            }
            UserType.Expert->{
                if (firstName.trim().isBlank() || lastName.trim().isBlank() || age.trim().isBlank()|| selectedGender == null
                    || bio.trim().isBlank() || major.trim().isBlank()){
                    showToastMsg(resources.getString(R.string.empty_field_error))
                    return
                }
            }
            UserType.BusDriver->{
                if (firstName.trim().isBlank() || lastName.trim().isBlank() || age.trim().isBlank()|| selectedGender == null
                    || busNum.trim().isBlank()){
                    showToastMsg(resources.getString(R.string.empty_field_error))
                    return
                }
            }
            else -> {}
        }
        val childUpdates=HashMap<String,Any>()
        childUpdates["firstName"]=firstName
        childUpdates["lastName"]=lastName
        childUpdates["age"]=age
        childUpdates["gender"]=selectedGender?:""
        when(userType){
            UserType.Student->{
                childUpdates["academicYear"]=academicYear
                childUpdates["major"]=major
            }
            UserType.Expert->{
                childUpdates["major"]=major
                childUpdates["bio"]=bio
            }
            UserType.BusDriver->{
                childUpdates["busNumber"]=busNum
            }
            else->{}
        }
        showHideProgressBar(true)
        val auth = FirebaseAuth.getInstance()
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(auth.currentUser?.uid ?: "")
        val baseDB = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(auth.currentUser?.uid ?: "")
        baseDB.child("img").setValue(profileImgUrl)
            .addOnCompleteListener {
                //update other details
                baseDB.child("details").updateChildren(childUpdates)
                    .addOnCompleteListener {
                        eventListener = databaseRef.addValueEventListener( object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                showHideProgressBar(false)
                                if (snapshot.exists()){
                                    val user = snapshot.getValue(User::class.java)
                                    user?.let {
                                        saveUserToLocalDB(user)
                                    }?:showToastMsg("Error!!")
                                }else{
                                    showToastMsg("Error!!")
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                showHideProgressBar(false)
                                showToastMsg(error.message)
                            }

                        })

                    }
                    .addOnFailureListener { exception->
                        showHideProgressBar(false)
                        showToastMsg(exception.localizedMessage)
                    }
            }
            .addOnFailureListener {exception->
                showHideProgressBar(false)
                showToastMsg(exception.localizedMessage)
            }
    }

    private fun saveUserToLocalDB(user:User){
        val userLocalDataSource = UserLocalDataSource(requireContext())
        runBlocking {
            val localUser =  LocalUser(user.userId,user.userName,user.categoryTypeID,user.img,user.details)
            userLocalDataSource.saveUserData(localUser)
            relaunchApp()
        }
    }

    override fun onStop() {
        super.onStop()
        if (this::databaseRef.isInitialized && this::eventListener.isInitialized)
            databaseRef.removeEventListener(eventListener)
    }

}