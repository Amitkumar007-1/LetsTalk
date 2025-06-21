package com.example.letstalk.common.sync

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.letstalk.R

object NotificationHelper {

    private const val CHANNEL_ID = "sync_profile"
    private const val CHANNEL_NAME = "Profile Sync"
    private const val NOTIFICATION_ID=1001
    @SuppressLint("SuspiciousIndentation")
    private fun createNotificationChannel(context:Context) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
      val notificationManager=  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
    fun showSyncNotification(context: Context,message:String){
        createNotificationChannel(context)
       val syncNotification= NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Profile sync")
           .setContentText(message)
           .setSmallIcon(R.drawable.app_logo)
            .build()
        val manager=context.getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
        manager.notify(NOTIFICATION_ID,syncNotification)
    }
    fun cancelSyncNotification(context: Context){
        val manager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }

}