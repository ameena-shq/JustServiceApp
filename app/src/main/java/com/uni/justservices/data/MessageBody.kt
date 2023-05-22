package com.uni.justservices.data

data class MessageBody(
    val to:String="",
    val data:Data?=null,
)

data class Data(
    val notificationType:Int = NotificationType.NON.getID(),
    val title:String="",
    val message:String="",
    val userID:String="",
    val time:String?=null,
    val userProfileImg:String = ""
)

enum class NotificationType{
    FRIEND_REQUEST {
        override fun getID(): Int {
            return 1
        }
    },
    CHAT{
        override fun getID(): Int {
            return 2
        }
    },
    ACCEPT_REQUEST{
        override fun getID(): Int {
            return 3
        }
    }
    ,
    NON{
        override fun getID(): Int {
            return 0
        }
    };

    abstract fun getID():Int
}
