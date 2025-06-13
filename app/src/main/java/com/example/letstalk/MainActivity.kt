package com.example.letstalk

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.letstalk.ui.theme.LetsTalkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window,true)
//        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
//            val systemInsets = insets.getInsets(
//                WindowInsetsCompat.Type.systemBars() or
//                        WindowInsetsCompat.Type.ime() // include keyboard
//            )
//            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom)
//            insets
//        }
        setContent {
            LetsTalkTheme {
                MainApp()
            }
        }
    }
}