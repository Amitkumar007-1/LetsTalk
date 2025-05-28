package com.example.letstalk.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
 fun  LoginScreen(){
     Column(modifier = Modifier.fillMaxSize().background(Color.Cyan)){
       Box(modifier = Modifier
           .fillMaxSize(),
           contentAlignment = Alignment.Center){
           Text(text = "Login Screen")
       }
     }
}