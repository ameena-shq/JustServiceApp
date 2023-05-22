package com.uni.justservices.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

import com.uni.justservices.data.Data
import com.uni.justservices.data.User
import com.uni.justservices.data.UserType
import com.uni.justservices.databinding.FragmentNotificationBinding
import com.uni.justservices.ui.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : BaseFragment(), NotificationAdapter.NotificationListener {
    private lateinit var binding:FragmentNotificationBinding
    private lateinit var adapter:NotificationAdapter
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NotificationAdapter(requireContext())
        adapter.setupListener(this)
        binding.notificationListV.adapter = adapter
        fetchData()
    }

    private fun fetchData(){
        showHideProgressBar(true)
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?:""
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(currentUserID).child("notification")
        eventListener = databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                showHideProgressBar(false)
                if (snapshot.exists()){
                    val data = snapshot.getValue<HashMap<String,Data>>()
                    val notificationList = data?.map { it.value }
                    notificationList?.let {list->
                        adapter.setItems(list)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showHideProgressBar(false)
                showToastMsg(error.message)
            }

        })

    }

    override fun onItemClicked(userID: String) {
        showHideProgressBar(true)
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    showHideProgressBar(false)
                    if (snapshot.exists()){
                        val date = snapshot.getValue(User::class.java)
                        date?.let { user->
                            when(user.categoryTypeID){
                                UserType.Student.getID()->{
                                    openChatFragment(user.userId)
                                }
                                UserType.Expert.getID()->{
                                    findNavController().navigate(
                                        NotificationFragmentDirections.actionNotificationFragmentToExpertProfileFragment(user)
                                    )
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showHideProgressBar(false)
                    showToastMsg(error.message)

                }

            })
    }

    private fun openChatFragment(userID: String){
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL).child("User")
            .child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val data = snapshot.getValue(User::class.java)
                        data?.let {
                            findNavController().navigate(NotificationFragmentDirections.actionNotificationFragmentToChatFragment(it))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onStop() {
        super.onStop()
        databaseRef.removeEventListener(eventListener)
    }

}