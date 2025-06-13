package com.example.letstalk.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.letstalk.ui.status_bar_theme.StatusBarTheme

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = DarkBackground,
    background = DarkBackground,
    onBackground = LightBackground,
    surface = DarkGray,
    onSurface = LightText,
    outline = SubText,
    secondary = DarkSender,
    onSecondary = LightText,
    tertiary = LightBackground,
    onSurfaceVariant = DarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = DarkBackground,
    background = LightBackground,
    onBackground = DarkBackground,
    surface = LightReceiver,
    onSurface = DarkText,
    outline = SubText,
    secondary = LightSender,
    onSecondary = DarkText,
    tertiary = DarkBackground,
    onSurfaceVariant = LightReceiver

//    primary = LetsTalkAccent,
//    onPrimary = Lets

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun LetsTalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    StatusBarTheme(color = if(darkTheme) StatusBarColor.darkStatusBar else StatusBarColor.lightStatusBar,
        darkIcon = !darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}