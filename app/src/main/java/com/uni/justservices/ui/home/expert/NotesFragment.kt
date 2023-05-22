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
import com.uni.justservices.databinding.FragmentNotesBinding
import com.uni.justservices.ui.base.BaseFragment


class NotesFragment : BaseFragment() {
    private lateinit var binding:FragmentNotesBinding
    private val adapter = NoteAdapter()
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference
    private lateinit var currentUserID:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid?:""
        binding.notesListV.adapter = adapter
        binding.addNoteBtn.setOnClickListener {
            val note = binding.chatET.text.toString()
            if (note.trim().isNotEmpty()){
                addNote(note)
            }
        }
        fetchData()
    }

    private fun addNote(note:String){
        showHideProgressBar(true)
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUserID).child("notes")
            .push().setValue(note)
            .addOnCompleteListener {task->
                showHideProgressBar(false)
                if (task.isSuccessful)
                    binding.chatET.text?.clear()
            }
            .addOnFailureListener { ex->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

    private fun fetchData(){
        showHideProgressBar(true)
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("User").child(currentUserID?:"").child("notes")
        eventListener = databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                showHideProgressBar(false)
                val data = snapshot.getValue<HashMap<String,String>>()
                val noteList = data?.map { it.value }
                noteList?.let {
                    adapter.setItems(noteList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showHideProgressBar(false)
                showToastMsg(error.message)
            }

        })
    }

    override fun onStop() {
        super.onStop()
        databaseRef.removeEventListener(eventListener)
    }

}