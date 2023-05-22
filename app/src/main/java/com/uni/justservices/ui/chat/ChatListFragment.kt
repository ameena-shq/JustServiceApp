package com.uni.justservices.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.uni.justservices.data.Chat
import com.uni.justservices.data.ChatData
import com.uni.justservices.data.User
import com.uni.justservices.databinding.FragmentChatListBinding
import com.uni.justservices.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.Calendar


class ChatListFragment : BaseFragment(), ChatListAdapter.ChatListener {
    private lateinit var binding:FragmentChatListBinding
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference
    private lateinit var currentUserID: String
    private lateinit var chatListAdapter:ChatListAdapter
    private var userList: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        chatListAdapter = ChatListAdapter(requireContext())
        chatListAdapter.setupListener(this)
        binding.notificationListV.adapter = chatListAdapter
        fetchUserList()
    }



    private fun fetchData(list: List<User>){
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUserID)
            .child("chat")
        eventListener = databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue<HashMap<String,HashMap<String, Chat>>>()
                //val list = data?.toSortedMap()?.map { it.value }?.map { it.values }?.flatten()
                val userKeys = data?.map { it.key }//userID
                userKeys?.let {key->
                    val newList = list.filter { key.contains(it.userId) }
                    val finalList = mutableListOf<ChatData>()
                    newList.forEach { user->
                            val lastChat = user.chat?.toSortedMap()?.filter { it.key == currentUserID }?.values?.first()?.toSortedMap()?.values?.last()
                            /*val lastChatTime = user.chat?.toSortedMap()?.filter { it.key == currentUserID }?.values?.first()?.keys?.first()
                            val sdf = SimpleDateFormat("hh")
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = lastChatTime?.toLong() ?: 0
                            val time = sdf.format(cal.time)*/
                            finalList.add(ChatData(
                                userName = "${user.details?.firstName} ${user.details?.lastName}",
                                userId = user.userId,
                                user.img,
                                lastChat = lastChat?.message ?: "",
                                isOpen = false,
                            ))
                        }
                    chatListAdapter.setItems(finalList)
                }
                showHideProgressBar(false)
            }

            override fun onCancelled(error: DatabaseError) {
                showHideProgressBar(false)
            }

        })
    }

    private fun fetchUserList(){
        showHideProgressBar(true)
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue<HashMap<String,User>>()
                    val userListData = data?.map { it.value }
                    userList.clear()
                    userListData?.let {
                        userList.addAll(userListData)
                        fetchData(userListData)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    showHideProgressBar(false)
                }

            })
    }

    override fun onItemClicked(userID: String) {
        val user = userList.find { it.userId == userID }
        user?.let {
            findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToChatFragment(it))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::databaseRef.isInitialized)
            databaseRef.removeEventListener(eventListener)
    }

}