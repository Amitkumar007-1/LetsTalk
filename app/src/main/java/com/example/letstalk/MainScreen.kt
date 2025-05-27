package com.example.letstalk

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.letstalk.presentation.screens.chats.ChatScreen
import com.example.letstalk.presentation.screens.home.HomeScreen
import com.example.letstalk.presentation.screens.sign_in.SignInScreen
import com.example.letstalk.presentation.screens.sign_up.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp() {
    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        val startDest = FirebaseAuth.getInstance().currentUser?.let { "home" } ?: "signin"
        NavHost(navController = navController, startDestination = startDest) {
            composable("signin") {
                SignInScreen(navController)
            }
            composable("signup") {
                SignUpScreen(navController)
            }
            composable("home") {
                HomeScreen { friendId ->
                    navController.navigate("chat/${friendId}")
                }
            }
            composable("chat/{friendId}", arguments = listOf(navArgument("friendId") {
                type= NavType.StringType
            })) {
//               val friendId= it.arguments?.getString("friendId")
//                if (friendId != null) {
//
////                }
                    ChatScreen()
                }
        }

    }
}