package com.example.letstalk.common.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.letstalk.common.service.UserService
import com.example.letstalk.db.dao.UserProfileDao

class ProfileSyncWorkerFactory(
    private val userProfileDao: UserProfileDao,
    private val userService: UserService
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
       return when(workerClassName){
             ProfileSyncWorker::class.java.name->{
                  ProfileSyncWorker(appContext,workerParameters,userProfileDao,userService)
             }
            else->{ return null }
        }
    }

}