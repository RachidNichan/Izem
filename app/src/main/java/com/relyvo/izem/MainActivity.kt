package com.relyvo.izem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.ui.theme.IzemTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        lifecycleScope.launch {
            delay(1500)
            keepSplashOnScreen = false
        }

        /*
        lifecycleScope.launch {
            val repo = FirestoreRepo()

            repo.uploadDataToFirestore()
        }*/

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