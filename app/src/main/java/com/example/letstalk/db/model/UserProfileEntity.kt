package com.example.letstalk.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val uid:String="",
    val name:String="",
    val imageUrl:String=""
)
