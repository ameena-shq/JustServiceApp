package com.uni.justservices.ui.home.bus

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
import com.uni.justservices.data.Trip
import com.uni.justservices.databinding.FragmentTripListBinding
import com.uni.justservices.ui.base.BaseFragment

class DriverTripListFragment : BaseFragment(), DriverTripAdapter.TripClickListener {
    private lateinit var binding:FragmentTripListBinding
    private val adapter = DriverTripAdapter()
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTripListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tripListRV.adapter = adapter
        adapter.setupListener(this)
        fetchData()
    }

    private fun fetchData(){
        showHideProgressBar(true)
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        databaseRef = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("Trips")
        eventListener = databaseRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    showHideProgressBar(false)
                    val list =snapshot.getValue<HashMap<String,Trip>>()
                    val myTrips = list?.toSortedMap()?.map { it.value }
                        ?.filter { it.userId == currentUser }
                    myTrips?.let {
                        adapter.setItems(myTrips)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showHideProgressBar(false)
                    showToastMsg(error.message)
                }
            })
    }

    override fun onDeleteTrip(trip: Trip, position: Int) {
        showHideProgressBar(true)
        Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("Trips").child(trip.tripID).removeValue()
            .addOnCompleteListener {
                showHideProgressBar(false)
            }
            .addOnFailureListener { ex->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

    override fun onEditTrip(trip: Trip, position: Int) {
        findNavController().navigate(DriverTripListFragmentDirections.actionTripListFragmentToAddTripFragment(trip = trip))
    }

    override fun onStop() {
        super.onStop()
        databaseRef.removeEventListener(eventListener)
    }
}