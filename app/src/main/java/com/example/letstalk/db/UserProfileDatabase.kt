package com.example.letstalk.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.letstalk.db.dao.UserProfileDao
import com.example.letstalk.db.model.UserProfileEntity

@Database(entities = [UserProfileEntity::class], exportSchema = false,version = 1)
abstract class UserProfileDatabase:RoomDatabase() {
    abstract  fun getDao():UserProfileDao
}