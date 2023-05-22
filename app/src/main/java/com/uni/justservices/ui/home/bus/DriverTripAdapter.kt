package com.uni.justservices.ui.home.bus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uni.justservices.data.Trip
import com.uni.justservices.databinding.DriverTripsLayoutBinding

class DriverTripAdapter:RecyclerView.Adapter<DriverTripAdapter.MyViewHolder>() {
    private val tripList = mutableListOf<Trip>()

    interface TripClickListener{
        fun onDeleteTrip(trip: Trip,position: Int)
        fun onEditTrip(trip: Trip,position: Int)
    }
    private lateinit var listener:TripClickListener
    fun setupListener(listener:TripClickListener){
        this.listener = listener
    }

    fun setItems(list: List<Trip>){
        tripList.clear()
        tripList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val trip = tripList[position]
        holder.day.text = trip.day
        holder.date.text = "${trip.date} ${trip.time}"
        holder.edit.setOnClickListener {
            listener.onEditTrip(trip,position)
        }

        holder.delete.setOnClickListener {
            listener.onDeleteTrip(trip, position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(DriverTripsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return tripList.size
    }



    inner class MyViewHolder(view: DriverTripsLayoutBinding): RecyclerView.ViewHolder(view.root){
        val day = view.dayTxt
        val date = view.dateTxt
        val edit = view.editBtn
        val delete = view.deleteBtn

    }
}