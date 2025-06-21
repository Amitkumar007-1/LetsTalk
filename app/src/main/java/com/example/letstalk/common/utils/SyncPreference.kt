package com.example.letstalk.common.utils

import android.content.Context

object SyncPreference {
    private const val SYNC_PROFILE="sync_profile_pref"
    private const val SYNC_INITIALIZED="sync_initialised"

    fun isInitialized(context:Context):Boolean{
      val pref=  context.getSharedPreferences(SYNC_PROFILE,Context.MODE_PRIVATE)
       return pref.getBoolean(SYNC_INITIALIZED,false)
    }
    fun markInitialised(context: Context){
       val pref= context.getSharedPreferences(SYNC_PROFILE,Context.MODE_PRIVATE)
        pref.edit().putBoolean(SYNC_INITIALIZED,true).apply()
    }
    fun markUnInitialized(context: Context){
        val pref=context.getSharedPreferences(SYNC_PROFILE,Context.MODE_PRIVATE)
        pref.edit().putBoolean(SYNC_INITIALIZED,false).apply()
    }

}