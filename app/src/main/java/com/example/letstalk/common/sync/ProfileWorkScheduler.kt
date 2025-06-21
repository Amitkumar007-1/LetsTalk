package com.example.letstalk.common.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ProfileWorkScheduler {

    fun scheduleProfileWork(context: Context) {
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest =
            PeriodicWorkRequestBuilder<ProfileSyncWorker>(2,TimeUnit.HOURS)
                .setConstraints(workConstraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,30,TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "ProfileDataSync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
    }

}