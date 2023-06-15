package com.uni.justservices.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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
import com.uni.justservices.data.Chat
import com.uni.justservices.data.LocalUser
import com.uni.justservices.data.NotificationType
import com.uni.justservices.data.User
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.FragmentChatBinding
import com.uni.justservices.ui.base.BaseFragment
import kotlinx.coroutines.runBlocking

class ChatFragment : BaseFragment() {
    private lateinit var binding:FragmentChatBinding
    private val adapter = ChatAdapter()
    private lateinit var currentUserID: String
    private lateinit var selectedUser: User
    private lateinit var currentUser: User
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference
    private val navArgs:ChatFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideToolbar(show = false)
        selectedUser = navArgs.selectedUser
        binding.userName.text = "${selectedUser.details?.firstName} ${selectedUser.details?.lastName}"
        if (selectedUser.img.isNotEmpty())
            Glide.with(requireContext())
                .load(selectedUser.img)
                .into(binding.profileImg)
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?:""
        binding.chateRV.adapter = adapter
        adapter.setCurrentUser(currentUserID)
        fetchData()
        fetchLocalUserData()
        binding.sendBtn.setOnClickListener {
            val message = binding.chatET.text.toString()
            if (message.isNotEmpty()) {
                sendChat(message)
                binding.chatET.text?.clear()
            }
        }
    }
    private fun prepareNotification(user: User){
        val message = "${currentUser.details?.firstName} ${currentUser.details?.lastName} Sent you message."
        sendNotification(user.token,user.userId,currentUser,
            NotificationType.CHAT.getID(),getString(R.string.chat_notification),message)
    }

    private fun sendChat(msg:String){
        showHideProgressBar(true)
        val chat = Chat(currentUserID,msg)
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUserID).child("chat")
            .child(selectedUser.userId)
            .child(System.currentTimeMillis().toString())
            .setValue(chat)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    prepareNotification(selectedUser)
                    //save message to other user
                    Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
                        .child("User").child(selectedUser.userId)
                        .child("chat")
                        .child(currentUserID)
                        .child(System.currentTimeMillis().toString()).setValue(chat)
                        .addOnCompleteListener {
                            showHideProgressBar(false)
                        }
                        .addOnFailureListener { ex->
                            showHideProgressBar(false)
                            showToastMsg(ex.localizedMessage)
                        }
                }else
                    showHideProgressBar(false)

            }
            .addOnFailureListener { ex->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

    private fun fetchData(){
        showHideProgressBar(true)
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUserID)
            .child("chat")
            .child(selectedUser.userId)
        eventListener = databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                showHideProgressBar(false)
                val data = snapshot.getValue<HashMap<String,Chat>>()
                val chatList = data?.toSortedMap()?.map { it.value }?.filter { it.userId == selectedUser.userId || it.userId == currentUserID }
                chatList?.let {
                    adapter.setList(it)
                }
                //updateChatBadge(chatList?.size ?: 0)
            }

            override fun onCancelled(error: DatabaseError) {
                showHideProgressBar(false)
                showToastMsg(error.message)
            }

        })
    }

    private fun fetchLocalUserData(){
        runBlocking {
            val user = UserLocalDataSource(requireContext()).getUserData()
            user?.let { data->
                currentUser = User(
                    userName = data.userName,
                    userId = data.userId,
                    img = data.img,
                    categoryTypeID =data.categoryTypeID,
                    details = data.details)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        databaseRef.removeEventListener(eventListener)
    }

}