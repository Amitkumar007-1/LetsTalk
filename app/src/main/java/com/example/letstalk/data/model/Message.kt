package com.example.letstalk.data.model

import com.example.letstalk.common.utils.TimeFormatter
import com.google.firebase.Timestamp

data class Message(
     val senderId:String="",
     val receiverId:String="",
     val message:String="",
     var timeStamp:Timestamp?=null
 ){
    @Transient
    var  dateTime:String=""

    fun setDataTime(){
        dateTime=timeStamp?.let { TimeFormatter.formatServerTimeStamp(it) }?:""
    }

    override fun toString(): String {
        return "Message(senderId='$senderId', receiverId='$receiverId', message='$message', timeStamp=$timeStamp, dateTime='$dateTime')"
    }
}