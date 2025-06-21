package com.example.letstalk.common.sync
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.letstalk.common.service.UserService
import com.example.letstalk.common.utils.Resource
import com.example.letstalk.db.dao.UserProfileDao

class ProfileSyncWorker
    (
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val userProfileDao: UserProfileDao,
    private val userService: UserService
) :
    CoroutineWorker(context, workerParams) {

        override suspend fun doWork(): Result {
            if(runAttemptCount>3){
                NotificationHelper.showSyncNotification(context,"Sync failed")
                return Result.failure()
            }
            if(runAttemptCount==0){
                NotificationHelper.showSyncNotification(applicationContext,"Syncing...")
            }

            return syncLetsTalkProfiles()
    }


    private suspend fun syncLetsTalkProfiles(): Result {
        when (val resource = userService.fetchUsers()) {
            is Resource.Error -> {
                Log.d("Worker Error", resource.message ?: "Something went wrong")
                NotificationHelper.showSyncNotification(applicationContext,"Retrying...")
               return  Result.retry()
            }

            is Resource.Success -> {
                userProfileDao.clearUserProfile()
                userProfileDao.insertUserProfile(resource.data)
                NotificationHelper.showSyncNotification(applicationContext,"Sync done")
                return Result.success()
            }
            else -> Unit
        }
        return Result.failure()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }
}