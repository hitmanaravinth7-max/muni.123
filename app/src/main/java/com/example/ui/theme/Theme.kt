package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = RedLight,
    onPrimary = RedDark,
    primaryContainer = RedDark,
    onPrimaryContainer = RedLight,
    secondary = AmberLight,
    onSecondary = AmberUrgent,
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF242424),
    onBackground = Color(0xFFECEFF1),
    onSurface = Color(0xFFECEFF1),
    error = RedCritical,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RedEmergency,
    onPrimary = White,
    primaryContainer = RedSoftBackground,
    onPrimaryContainer = RedDark,
    secondary = AmberUrgent,
    onSecondary = White,
    secondaryContainer = AmberLight,
    onSecondaryContainer = AmberUrgent,
    background = GrayBackground,
    surface = GraySurface,
    onBackground = GrayTextPrimary,
    onSurface = GrayTextPrimary,
    outline = GrayOutline,
    error = RedCritical,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep brand consistency with Red-and-White emergency theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

