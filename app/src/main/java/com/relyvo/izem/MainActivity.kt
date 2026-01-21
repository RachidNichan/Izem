package com.relyvo.izem

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.relyvo.izem.ui.theme.IzemTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.ui.screens.MainScreen
import com.relyvo.izem.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Permission Granted: Notifications allowed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        lifecycleScope.launch {
            delay(1500)
            keepSplashOnScreen = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val viewModel: AppViewModel = viewModel()
            val isArabic by viewModel.isArabic.collectAsState()

            val direction = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            IzemTheme {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {

                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}