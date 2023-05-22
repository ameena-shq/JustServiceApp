package com.uni.justservices.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userName:String="",
    val email:String="",
    val password:String="",
    val userId:String="",
    val categoryTypeID:Int?=null,
    val img:String="",
    val details: UserDetails?=null,
    val requestListStatus:HashMap<String,RequestStatus>?=null,
    val notes:HashMap<String,String>?=null,
    val chat:HashMap<String,HashMap<String,Chat>>?=null,
    val token:String=""
    ):Parcelable{

    @Parcelize
    data class RequestStatus(
        val status:Int=0,
        val userId: String=""
    ):Parcelable


}
@Parcelize
data class ChatData(
    val userName: String="",
    val userId:String="",
    val img:String="",
    val lastChat:String = "",
    val isOpen:Boolean = false,
    val date:String="",
    val chatList:HashMap<String,Chat>?=null
):Parcelable

@Parcelize
data class Chat(
    val userId: String="",
    val message:String=""
):Parcelable

enum class RequestStatusEnum{
    Pending {
        override fun getID(): Int {
            return 1
        }
    },
    Friends{
        override fun getID(): Int {
            return 2
        }
    },
    Non{
        override fun getID(): Int {
            return 0
        }
    };

    abstract fun getID():Int


}

@Parcelize
data class UserDetails(
    val firstName:String="",
    val lastName:String="",
    val age:String="",
    val gender:String="",
    val academicYear:String="",
    val major:String="",
    val bio:String="",
    val busNumber:String="",
):Parcelable


@Parcelize
data class Trip(
    val userId:String="",
    val city: String ="",
    val station: String ="",
    val date: String ="",
    val day:String="",
    val time:String="",
    val tripID:String=""
):Parcelable
