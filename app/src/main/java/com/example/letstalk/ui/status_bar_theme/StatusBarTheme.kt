package com.example.letstalk.ui.status_bar_theme

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun StatusBarTheme(color:Color,darkIcon:Boolean){
    val view=LocalView.current
    val activity= LocalActivity.current

    SideEffect {
        activity?.window?.let {
            it.statusBarColor=color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(it,false)
            val insetsController=WindowInsetsControllerCompat(it,view)
            insetsController.isAppearanceLightStatusBars=darkIcon
        }
    }
}