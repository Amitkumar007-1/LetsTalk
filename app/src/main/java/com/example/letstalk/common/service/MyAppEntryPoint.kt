package com.example.letstalk.common.service

import com.example.letstalk.db.dao.UserProfileDao
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MyAppEntryPoint {
    fun getUserService():UserService
    fun getFirebaseAuth():FirebaseAuth
    fun getUserProfileDao():UserProfileDao
}