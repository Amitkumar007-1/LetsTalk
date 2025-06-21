package com.example.letstalk.common.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeFormatter {
    fun formatServerTimeStamp(timeStamp: Timestamp):String{
        val messageDate = Calendar.getInstance().apply { time = timeStamp.toDate() }
        val now = Calendar.getInstance()

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        return when {
            now.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR)
                    && now.get(Calendar.DAY_OF_YEAR) == messageDate.get(
                Calendar.DAY_OF_YEAR
            )->{
                "Today,${timeFormat.format(timeStamp.toDate())}"
            }
            now.get(Calendar.YEAR)== messageDate.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) -messageDate.get(Calendar.DAY_OF_YEAR)==1->{
                        "Yesterday ${timeFormat.format(timeStamp.toDate())}"
                    }
            else->{
                "${dateFormat.format(timeStamp.toDate())}, ${timeFormat.format(timeStamp.toDate())}"
            }
        }
    }
    fun formatRecentChatTimeStamp(timeStamp: Timestamp):String{
        val chatDate=Calendar.getInstance().apply { time=timeStamp.toDate()}
        val now=Calendar.getInstance()

        val timeFormat=SimpleDateFormat("h:mm a",Locale.getDefault())
        val dateFormat=SimpleDateFormat("d/M/yy", Locale.getDefault())

        return when{
            now.get(Calendar.YEAR)==chatDate.get(Calendar.YEAR)
                    && now.get(Calendar.DAY_OF_YEAR)==chatDate.get(Calendar.DAY_OF_YEAR)->{
                        timeFormat.format(timeStamp.toDate())
                    }

            now.get(Calendar.YEAR)==chatDate.get(Calendar.YEAR)
                    && now .get(Calendar.DAY_OF_YEAR)- chatDate.get(Calendar.DAY_OF_YEAR)==1-> "Yesterday"

            else->{
                dateFormat.format(timeStamp.toDate())
            }
        }
    }
}