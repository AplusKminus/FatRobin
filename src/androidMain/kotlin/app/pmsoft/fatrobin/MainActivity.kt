package app.pmsoft.fatrobin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import app.pmsoft.fatrobin.ui.theme.AndroidFatRobinTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Enable edge-to-edge display
    enableEdgeToEdge()

    setContent {
      AndroidFatRobinTheme {
        val isLightTheme = !isSystemInDarkTheme()
        // Configure status bar appearance based on theme
        SideEffect {
          val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
          windowInsetsController.isAppearanceLightStatusBars = isLightTheme
        }

        FatRobinApp()
      }
    }
  }
}
