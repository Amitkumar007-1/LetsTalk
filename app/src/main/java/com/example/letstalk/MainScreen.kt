package com.example.letstalk

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.letstalk.presentation.screens.chats.ChatScreen
import com.example.letstalk.presentation.screens.home.HomeScreen
import com.example.letstalk.presentation.screens.home.viewmodel.HomeViewModel
import com.example.letstalk.presentation.screens.sign_in.SignInScreen
import com.example.letstalk.presentation.screens.sign_up.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp() {
    Surface(modifier = Modifier.fillMaxSize()) {
        val homeViewModel= hiltViewModel<HomeViewModel>()
        val navController = rememberNavController()
        val user=FirebaseAuth.getInstance().currentUser
        LaunchedEffect(Unit) {
            user?.let {
                homeViewModel.setUserStatus("online")
            }
        }
        val startDest =user?.let { "home" } ?: "signin"
        NavHost(navController = navController, startDestination = startDest) {
            composable("signin") {
                SignInScreen(navController)
            }
            composable("signup") {
                SignUpScreen(navController)
            }
            composable("home") {
                HomeScreen (navController)
            }
            composable("chat/{friendId}", arguments = listOf(navArgument("friendId") {
                type= NavType.StringType
            })) {
                    ChatScreen()
                }
        }

    }
}