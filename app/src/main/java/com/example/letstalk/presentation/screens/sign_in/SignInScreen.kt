package com.example.letstalk.presentation.screens.sign_in

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.letstalk.R
import com.example.letstalk.presentation.screens.sign_in.viewmodel.SignInViewModel
import com.example.letstalk.utils.AuthUiState

@Composable
fun SignInScreen(navController: NavHostController?=null) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val signInViewModel:SignInViewModel = hiltViewModel()
    val snackBarState = remember { SnackbarHostState() }
    val authUiState = signInViewModel.authUiState.collectAsState(AuthUiState.Nothing)

    LaunchedEffect(authUiState.value) {
        when( val state=authUiState.value){
            is AuthUiState.Success->{
                navController?.navigate("login"){
                    popUpTo("signin"){inclusive=true}
                }
            }
            is AuthUiState.Error->{
                snackBarState.showSnackbar(state.error!!)
            }
            else->Unit
        }
    }
    Scaffold (modifier = Modifier.fillMaxSize(), snackbarHost = { SnackbarHost(snackBarState) }){ paddingValues ->
        Column (modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White))
            OutlinedTextField( value=email,
                onValueChange = {
                    email=it
                },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface ),
                modifier = Modifier.fillMaxWidth(0.75f),
                shape = RoundedCornerShape(5.dp),
                label = { Text(text = "Email")})

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(value=password,
                modifier = Modifier.fillMaxWidth(0.75f),
                onValueChange = {password=it},
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface ),
                shape = RoundedCornerShape(5.dp),
                visualTransformation = PasswordVisualTransformation(),
                label = { Text(text = "Password")}
            )

            Spacer(modifier = Modifier.height(5.dp))

            Button(onClick = {
                signInViewModel.signInUser(email.trim(),password.trim())
            },
                modifier = Modifier
                .fillMaxWidth(0.75f),
                enabled = email.isNotEmpty() && password.isNotEmpty()
                ) {
                if(authUiState.value is AuthUiState.Loading){
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(15.dp))
                }else{
                    Text("Sign In")
                }
            }
            TextButton(onClick = {
                navController?.navigate("signup")
            }) {
                Text(text="Don't have account? Sign Up")
            }
        }
    }
}