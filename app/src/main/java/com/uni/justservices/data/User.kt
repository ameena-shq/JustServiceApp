package com.uni.justservices.data

data class User(
    val userName:String,
    val email:String,
    val password:String,
    val userId:String,
    val categoryType:Int=-1
)
