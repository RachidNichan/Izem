package com.relyvo.izem

import android.Manifest
import android.content.Intent
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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.relyvo.izem.ui.screens.MainScreen
import com.relyvo.izem.viewmodel.AppViewModel
import java.util.Locale
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

val LocalActivity = staticCompositionLocalOf<androidx.activity.ComponentActivity> {
    error("No Activity found")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Permission Granted: Notifications allowed")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // android.util.Log.d("IzemNav", "MainActivity onNewIntent: type=${intent.getStringExtra("type")}")
        setIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        // android.util.Log.d("IzemNav", "MainActivity onCreate: type=${intent.getStringExtra("type")}")

        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        lifecycleScope.launch {
            delay(1500)
            keepSplashOnScreen = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        (application as IzemApp).scheduleReminder()

        setContent {
            val viewModel: AppViewModel = hiltViewModel()
            val isArabic by viewModel.isArabic.collectAsState()

            val direction = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
            
            val locale = if (isArabic) Locale.forLanguageTag("ar") else Locale.forLanguageTag("en")
            val configuration = android.content.res.Configuration(androidx.compose.ui.platform.LocalConfiguration.current)
            configuration.setLocale(locale)
            val context = androidx.compose.ui.platform.LocalContext.current.createConfigurationContext(configuration)

            IzemTheme {
                @Suppress("DEPRECATION")
                CompositionLocalProvider(
                    LocalActivity provides this@MainActivity,
                    LocalLayoutDirection provides direction,
                    LocalContext provides context,
                    LocalActivityResultRegistryOwner provides this@MainActivity,
                    LocalOnBackPressedDispatcherOwner provides this@MainActivity,
                    LocalViewModelStoreOwner provides this@MainActivity,
                    LocalSavedStateRegistryOwner provides this@MainActivity,
                    LocalLifecycleOwner provides this@MainActivity
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}