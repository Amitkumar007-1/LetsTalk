package com.example.letstalk.presentation.screens.sign_up

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.letstalk.R

//@Preview(showSystemUi = true)
@Composable
fun SignUpScreen(navController: NavHostController?=null) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White)
            )
            OutlinedTextField(value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(0.75f),
                textStyle = TextStyle(color = Color.Black),
                shape = RoundedCornerShape(5.dp),
                label = { Text(text = "Name") })

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(value = "",
                modifier = Modifier.fillMaxWidth(0.75f),
                onValueChange = {},
                textStyle = TextStyle(color = Color.Black),
                shape = RoundedCornerShape(5.dp),
                label = { Text(text = "Email") })

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(value = "",
                modifier = Modifier.fillMaxWidth(0.75f),
                onValueChange = {},
                textStyle = TextStyle(color = Color.Black),
                shape = RoundedCornerShape(5.dp),
                label = { Text(text = "Password") })

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(value = "",
                modifier = Modifier.fillMaxWidth(0.75f),
                onValueChange = {},
                textStyle = TextStyle(color = Color.Black),
                shape = RoundedCornerShape(5.dp),
                label = { Text(text = "Confirm Password") })
            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                //onclick func
            }, modifier = Modifier.fillMaxWidth(0.75f)) {
                Text(text = "Sign In")
            }
            Spacer(modifier = Modifier.height(5.dp))

            TextButton(onClick = {
               navController?.popBackStack()
            }) {
                Text(text="Already have account ?")
            }
        }
    }
}