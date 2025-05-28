package com.example.letstalk
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.letstalk.presentation.screens.login.LoginScreen
import com.example.letstalk.presentation.screens.sign_in.SignInScreen
import com.example.letstalk.presentation.screens.sign_up.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp(){
    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
       val startDest= FirebaseAuth.getInstance().currentUser?.let{"login"}?:"signin"
        NavHost(navController = navController, startDestination = startDest) {
            composable("signin"){
                SignInScreen(navController)
            }
            composable("signup"){
                SignUpScreen(navController)
            }
            composable("login"){
                LoginScreen()
            }
        }

    }
}