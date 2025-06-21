package com.example.letstalk.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.letstalk.db.model.UserProfileEntity

@Dao
interface UserProfileDao {

    @Query("Select * from user_profile WHERE uid =:id")
    suspend fun getUserProfileOnId(id:String):UserProfileEntity?
    @Upsert
    suspend fun insertUserProfile(userProfileList:List<UserProfileEntity>)

    @Query("select * from user_profile")
    suspend fun getAllUserProfile():List<UserProfileEntity>

    @Query("Delete from user_profile")
    suspend fun clearUserProfile()
}