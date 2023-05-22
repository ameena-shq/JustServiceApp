package com.uni.justservices.ui.home.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
import com.uni.justservices.databinding.FragmentExpertBinding
import com.uni.justservices.services.SendNotification
import com.uni.justservices.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ExpertListFragment : BaseFragment(), ExpertListAdapter.SendRequestListener {
    private lateinit var binding: FragmentExpertBinding
    private lateinit var adapter: ExpertListAdapter
    private var currentUser: User? = null
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExpertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ExpertListAdapter(requireContext())
        binding.expertListV.adapter = adapter
        adapter.setupListener(this)
        fetchDate()

    }

    private fun fetchDate() {
        showHideProgressBar(true)
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
        eventListener = databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showHideProgressBar(false)
                val list = snapshot.getValue<HashMap<String, User>>()
                currentUser = list?.map { it.value }?.find { it.userId == currentUserID }
                val expertList = list?.map { it.value }
                    ?.filter { it.categoryTypeID == UserType.Expert.getID() }
                expertList?.let {
                    currentUser?.let { user ->
                        adapter.setCurrentUser(user)
                    }
                    adapter.setItems(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showHideProgressBar(false)
                showToastMsg(error.message)
            }
        })
    }

    override fun sendRequest(user: User) {
        showHideProgressBar(true)
        //step 1->send request to expert
        //step 2->update my request list
        val request =
            User.RequestStatus(RequestStatusEnum.Pending.getID(), currentUser?.userId ?: "")
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(user.userId).child("requestListStatus")
            .child(currentUser?.userId ?: "")
            .setValue(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addExpertToMyList(user)
                    //send notification
                    prepareNotification(user)
                }
            }
            .addOnFailureListener { ex ->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

    private fun prepareNotification(user: User){
        val message = "${currentUser?.details?.firstName} ${currentUser?.details?.lastName} Sent you guidance request."
        sendNotification(user.token,user.userId,currentUser,NotificationType.FRIEND_REQUEST.getID(),getString(R.string.friend_request),message)
    }



    override fun startChat(user: User) {
        findNavController().navigate(ExpertListFragmentDirections.actionExpertListFragmentToChatFragment(user))
    }

    private fun addExpertToMyList(user: User) {
        val request = User.RequestStatus(RequestStatusEnum.Pending.getID(), user.userId)
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUser?.userId ?: "").child("requestListStatus")
            .child(user.userId)
            .setValue(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showHideProgressBar(false)
                }
            }
            .addOnFailureListener { ex ->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

    override fun onStop() {
        super.onStop()
        databaseRef.removeEventListener(eventListener)
    }

}