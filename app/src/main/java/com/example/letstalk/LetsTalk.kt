package com.example.letstalk

import android.app.Application
import android.util.Log
import com.example.letstalk.common.observer.AppLifeCycleObserver
import com.example.letstalk.common.observer.AppLifeCycleTracker
import com.example.letstalk.common.service.MyAppEntryPoint
import com.example.letstalk.utils.Resource
import dagger.hilt.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@HiltAndroidApp
class LetsTalk():Application() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO+ SupervisorJob())
    override fun onCreate() {
        super.onCreate()

        val entryPoint=EntryPointAccessors.fromApplication(this,MyAppEntryPoint::class.java)
        val userService=entryPoint.getUserService()
        val firebaseAuth=entryPoint.getFirebaseAuth()
        registerActivityLifecycleCallbacks(AppLifeCycleTracker(
            onAppForeground = {
                firebaseAuth.currentUser?.let {
                    coroutineScope.launch {
                        when (val result = userService.setUserStatus("online")) {
                            is Resource.Success -> {
                                Log.d("Status Success", "User status ${result.data}")
                            }

                            is Resource.Error -> {
                                Log.d("Status Error", "User status ${result.message}")
                            }

                            else -> Unit
                        }
                    }
                }
            },
            onAppBackground = {
                firebaseAuth.currentUser?.let {
                    coroutineScope.launch {
                        when (val result = userService.setUserStatus("offline")) {
                            is Resource.Success -> {
                                Log.d("Status Success", "User status ${result.data}")
                            }

                            is Resource.Error -> {
                                Log.d("Status Error", "User status ${result.message}")
                            }

                            else -> Unit
                        }
                    }
                }
            },
            onDestroy = { coroutineScope.cancel()}
        ))
    }
}