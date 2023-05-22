package com.uni.justservices.data

data class LocalUser(
    val userId:String="",
    val userName:String="",
    val categoryTypeID:Int?=null,
    val img:String="",
    val details: UserDetails?=null,
)
