package com.example.letstalk.common.observer

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.example.letstalk.common.service.UserService
import com.example.letstalk.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AppLifeCycleObserver(
    private val userService: UserService,
    private val firebaseAuth: FirebaseAuth
) : DefaultLifecycleObserver {


    override fun onStart(owner: LifecycleOwner) {
        firebaseAuth.currentUser?.let {
            owner.lifecycle.coroutineScope.launch {
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
    }

    override fun onStop(owner: LifecycleOwner) {
        firebaseAuth.currentUser?.let {
            owner.lifecycle.coroutineScope.launch {
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
    }
}