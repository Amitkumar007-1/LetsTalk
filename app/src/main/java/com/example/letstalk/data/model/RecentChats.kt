package com.example.letstalk.data.model

import com.example.letstalk.common.utils.TimeFormatter
import com.google.firebase.Timestamp

data class RecentChats(
    val chatWith:String="",
    val timeStamp:Timestamp?=null,
    val lastMessage:String=""
){
    @Transient
    var name:String=""
    @Transient
    var imageUrl:String=""

    @Transient
    var dateTime:String=""

    fun setTime(){
        dateTime= timeStamp?.let { TimeFormatter.formatRecentChatTimeStamp(it) }?:""
    }


}
