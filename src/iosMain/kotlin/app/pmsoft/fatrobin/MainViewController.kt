package app.pmsoft.fatrobin

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
  return ComposeUIViewController { FatRobinApp() }
}
