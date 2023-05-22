package com.uni.justservices.ui.home.bus

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.Trip
import com.uni.justservices.databinding.FragmentAddTripBinding
import com.uni.justservices.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

class AddTripFragment : BaseFragment() {
    private lateinit var binding:FragmentAddTripBinding
    private lateinit var selectedDay:String
    private val navArgs:AddTripFragmentArgs by navArgs()
    private var editTrip:Boolean = false
    private lateinit var auth :FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTripBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trip = navArgs.trip
        trip?.let {
            editTrip = true
            updateUI(it)
        }
        binding.addTripBtn.setOnClickListener {
            if (editTrip)
                editTrip(navArgs.trip)
            else
                addTrip()
        }
        auth = FirebaseAuth.getInstance()
        binding.dayET.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(requireActivity().supportFragmentManager,"DatePicker")

            datePicker.addOnPositiveButtonClickListener {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                cal.time = Date(it)
                val days = arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
                selectedDay = days[cal.get(Calendar.DAY_OF_WEEK)-1]
                val simpleDate = SimpleDateFormat("dd,MMMM,yyyy")
                val date = simpleDate.format(Date(it))
                binding.dayET.text = date.toString()

            }
        }

        binding.timeET.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .build()
            timePicker.show(requireActivity().supportFragmentManager,"TimePicker")

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val min = timePicker.minute
                val cal = Calendar.getInstance()
                cal.set(0, 0, 0, hour, min, 0)
                val timeInMillis = cal.timeInMillis
                val timeFormat = SimpleDateFormat("hh:mm a")
                binding.timeET.text = timeFormat.format(Date(timeInMillis))
            }
        }
    }

    private fun updateUI(trip: Trip){
        binding.cityET.setText(trip.city)
        binding.stationET.setText(trip.station)
        binding.dayET.text = trip.date
        binding.timeET.text = trip.time
        binding.addTripBtn.text = getString(R.string.edit_trip)

    }

    private fun editTrip(trip: Trip?){
        val station = binding.stationET.text.toString()
        val city = binding.cityET.text.toString()
        val date = binding.dayET.text.toString()
        val time = binding.timeET.text.toString()
        if (TextUtils.isEmpty(station.trim()) || TextUtils.isEmpty(city.trim())
            || TextUtils.isEmpty(date.trim()) || TextUtils.isEmpty(time.trim())){
            showToastMsg(resources.getString(R.string.empty_field_error))
            return
        }
        val tripId = trip?.tripID?:""
        val newTrip = Trip(auth.currentUser?.uid?:"",city,station,date,trip?.day?:"",time,tripId)
        val db = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("Trips").child(tripId).setValue(newTrip)
            .addOnCompleteListener {
                showHideProgressBar(false)
                showToastMsg("Trip Edit successfully.")
                findNavController().navigateUp()

            }
            .addOnFailureListener { ex->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }

    }

    private fun addTrip(){
        val station = binding.stationET.text.toString()
        val city = binding.cityET.text.toString()
        val date = binding.dayET.text.toString()
        val time = binding.timeET.text.toString()
        if (TextUtils.isEmpty(station.trim()) || TextUtils.isEmpty(city.trim())
            || TextUtils.isEmpty(date.trim()) || TextUtils.isEmpty(time.trim())){
            showToastMsg(resources.getString(R.string.empty_field_error))
            return
        }
        showHideProgressBar(true)
        val id = UUID.randomUUID().toString()
        val trip = Trip(auth.currentUser?.uid?:"",city,station,date,selectedDay,time,id)
        val db = Firebase.database.getReferenceFromUrl(BuildConfig.STORAGE_URL)
            .child("Trips").child(id).setValue(trip)
            .addOnCompleteListener {
                showHideProgressBar(false)
                showToastMsg("Trip Added successfully.")
                findNavController().navigateUp()
            }
            .addOnFailureListener { ex->
                showHideProgressBar(false)
                showToastMsg(ex.localizedMessage)
            }
    }

}