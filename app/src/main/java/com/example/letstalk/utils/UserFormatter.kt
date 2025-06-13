package com.example.letstalk.utils

import kotlin.text.Regex

object UserFormatter {

    fun formatUserName(name:String):String{
       val updatedName=  name.split(Regex("[ _]"))[0]
        return updatedName.replaceFirstChar { it.uppercase() }
    }
}