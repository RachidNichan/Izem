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
            IzemTheme {
                MainScreen()
            }
        }
    }
}