package com.uni.justservices.ui.home.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uni.justservices.data.Trip
import com.uni.justservices.databinding.TripRowViewBinding

class TripAdapter: RecyclerView.Adapter<TripAdapter.MyViewHolder>() {
    private val tripList = mutableListOf<Trip>()

    fun setItems(list: List<Trip>){
        tripList.clear()
        tripList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val trip = tripList[position]
        holder.city.text = trip.city
        holder.station.text = trip.station
        holder.date.text = trip.date
        holder.time.text = trip.time
        holder.busNumber.text = trip.busNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(TripRowViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    inner class MyViewHolder(view:TripRowViewBinding):RecyclerView.ViewHolder(view.root){
        val city = view.cityValue
        val station = view.stationValue
        val date = view.dateValue
        val time = view.timeValue
        val busNumber = view.numberValue
    }


}