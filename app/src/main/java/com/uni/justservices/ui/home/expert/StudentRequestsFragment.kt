package com.uni.justservices.ui.home.expert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.*
import com.uni.justservices.databinding.FragmentStudentRequestsBinding
import com.uni.justservices.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class StudentRequestsFragment : BaseFragment(), StudentRequestAdapter.RequestListener {

    private lateinit var binding:FragmentStudentRequestsBinding
    private lateinit var adapter:StudentRequestAdapter
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference
    private lateinit var currentUserID:String
    private var currentUser:User?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentRequestsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StudentRequestAdapter(requireContext())
        adapter.setupListener(this)
        binding.requestListV.adapter = adapter
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        fetchData()

    }

    private fun fetchData(){
        showHideProgressBar(true)
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User")
        databaseRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = snapshot.getValue<HashMap<String, User>>()
                currentUser = userList?.map { it.value }?.find{it.userId == currentUserID}
                val newList = userList?.map { it.value }?.filter { it.categoryTypeID == UserType.Student.getID() }
                databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
                    .child("User").child(currentUserID).child("requestListStatus")
                eventListener = databaseRef.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        showHideProgressBar(false)
                        val requestList = snapshot.getValue<HashMap<String, User.RequestStatus>>()
                        val pendingRequests = requestList?.map { it.value }?.filter { it.status == RequestStatusEnum.Pending.getID() }
                        val finalList = newList?.filter {user->
                            pendingRequests?.map { it.userId }?.contains(user.userId)?:false }
                        finalList?.let {
                            adapter.setItems(it)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        showHideProgressBar(false)
                        showToastMsg(error.message)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                showHideProgressBar(false)
                showToastMsg(error.message)
            }

        })
    }

    override fun accept(user: User) {
        showHideProgressBar(true)
        //delete student request
        var request = User.RequestStatus(RequestStatusEnum.Friends.getID(), user.userId)
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUserID).child("requestListStatus")
            .child(user.userId).setValue(request)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    //send notification
                    prepareNotification(user)
                    request = User.RequestStatus(RequestStatusEnum.Friends.getID(), currentUserID)
                    //delete from student
                    Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
                        .child("User").child(user.userId).child("requestListStatus")
                        .child(currentUserID).setValue(request)
                        .addOnCompleteListener {
                            showHideProgressBar(false)
                        }
                        .addOnFailureListener { ex->
                            showHideProgressBar(false)
                            showToastMsg(ex.localizedMessage)
                        }
                }
            }
            .addOnFailureListener {ex->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

    private fun prepareNotification(user: User){
        val message = "${currentUser?.details?.firstName} ${currentUser?.details?.lastName} Accept your guidance request."
        sendNotification(user.token,user.userId,currentUser,NotificationType.ACCEPT_REQUEST.getID(),getString(R.string.friend_request),message)
    }

    override fun delete(user: User) {
        showHideProgressBar(true)
        //delete student request
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUserID).child("requestListStatus")
            .child(user.userId).removeValue()
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    //delete from student
                    Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
                        .child("User").child(user.userId).child("requestListStatus")
                        .child(currentUserID).removeValue()
                        .addOnCompleteListener {
                            showHideProgressBar(false)
                        }
                        .addOnFailureListener { ex->
                            showHideProgressBar(false)
                            showToastMsg(ex.localizedMessage)
                        }
                }
            }
            .addOnFailureListener {ex->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

}