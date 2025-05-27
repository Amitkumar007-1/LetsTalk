package com.example.letstalk.utils

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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
}