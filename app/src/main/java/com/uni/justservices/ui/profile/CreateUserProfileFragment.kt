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
    private var selectedMajor:String?=null
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference
    private lateinit var majorAdapter:ArrayAdapter<String>


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
        setUpMajorAdapter()
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

        binding.majorET.setOnItemClickListener { parent, view, position, id ->
            selectedMajor = majorAdapter.getItem(position)
        }
        binding.createBtn.setOnClickListener {
            createUser()
        }
        binding.uploadImg.setOnClickListener {
            selectImg()
        }

    }

    private fun setUpMajorAdapter(){
        val majorList = arrayListOf(
            "Aeronautical Engineering" ,
            "Biomedical Engineering" ,
            "Chemical Engineering" ,
            "Civil Engineering" ,
            "Electrical Engineering" ,
            "Industrial Engineering" ,
            "Mechanical Engineering" ,
            "Nuclear Engineering" ,
            "Computer Engineering" ,
            "Computer Information Systems" ,
            "Computer Science" ,
            "Network Engineering and Security" ,
            "Software Engineering" ,
            "Cyber Security" ,
            "Data science" ,
            "Artificial Intelligence" ,
            "Accident and Emergency Medicine" ,
            "Anatomy" ,
            "Anesthesia and Recovery" ,
            "Dermatology" ,
            "Diagnostic Radiology and Nuclear Medicine" ,
            "General Surgery and Urology" ,
            "Health Management and Policy" ,
            "Internal Medicine" ,
            "Legal Medicine, Toxicology, and Forensic Medicine" ,
            "Neurology" ,
            "Neurosciences" ,
            "Neurosurgery" ,
            "Obstetrics and Gynecology" ,
            "Pathology and Microbiology" ,
            "Pediatrics and Neonatology" ,
            "Pharmacology" ,
            "Physiology and Biochemistry" ,
            "Psychiatry" ,
            "Public Health and Community Medicine" ,
            "Special Surgery" ,
            "Conservative Dentistry" ,
            "Oral Medicine and Oral Surgery" ,
            "Preventive Dentistry" ,
            "Prosthodontist" ,
            "Clinical Pharmacy" ,
            "Medicinal Chemistry and Pharmacology" ,
            "Pharmaceutical Technology" ,
            "Adult Health Nursing" ,
            "Community and Mental Health Nursing" ,
            "Maternal and Child Health Nursing" ,
            "Midwifery" ,
            "Animal Production" ,
            "Natural Resources and Environment" ,
            "Nutrition and Food Technology" ,
            "Plant Production" ,
            "Basic Medical Veterinary Sciences" ,
            "Clinical Veterinary Medical Sciences" ,
            "Pathology and Public Health" ,
            "Biotechnology and Genetic Engineering" ,
            "Chemistry " ,
            "English for Applied Studies " ,
            "Basic Sciences and Humanities " ,
            "Mathematics and Statistics " ,
            "Physics " ,
            "Allied Medical Sciences" ,
            "Applied Dental Sciences" ,
            "Medical Laboratory Sciences" ,
            "Rehabilitation Sciences" ,
            "Architecture" ,
            "Design and Visual Communication" ,
            "City Planning and Design")
        majorAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,majorList)
        binding.majorET.setAdapter(majorAdapter)
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
        val major = selectedMajor ?: ""
        val bio = binding.bioET.text.toString()
        val busNum = binding.busNumET.text.toString()
        val gender = selectedGender ?:""
        when(userType){
            UserType.Student->{
                if (firstName.trim().isBlank() || lastName.trim().isBlank() || age.trim().isBlank()|| gender.trim().isBlank()
                    || academicYear.trim().isBlank() || major.trim().isBlank()){
                    showToastMsg(resources.getString(R.string.empty_field_error))
                    return
                }
            }
            UserType.Expert->{
                if (firstName.trim().isBlank() || lastName.trim().isBlank() || age.trim().isBlank()|| gender.trim().isBlank()
                    || bio.trim().isBlank() || major.trim().isBlank()){
                    showToastMsg(resources.getString(R.string.empty_field_error))
                    return
                }
            }
            UserType.BusDriver->{
                if (firstName.trim().isBlank() || lastName.trim().isBlank() || age.trim().isBlank()|| gender.trim().isBlank()
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