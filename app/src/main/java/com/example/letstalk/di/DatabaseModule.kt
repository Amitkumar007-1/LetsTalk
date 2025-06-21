package com.example.letstalk.di

import android.content.Context
import androidx.room.Room
import com.example.letstalk.db.UserProfileDatabase
import com.example.letstalk.db.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun getUserProfileDatabase(@ApplicationContext context:Context):UserProfileDatabase{
        return Room.databaseBuilder(context,UserProfileDatabase::class.java,"user_profile_db")
            .build()
    }

    @Provides
    fun getUserProfileDao(userProfileDatabase: UserProfileDatabase):UserProfileDao{
       return userProfileDatabase.getDao()
    }

}