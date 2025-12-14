package com.relyvo.izem

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.relyvo.izem.data.DataSource
import com.relyvo.izem.ui.theme.IzemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        lifecycleScope.launch {
            delay(1500)
            keepSplashOnScreen = false
        }

        super.onCreate(savedInstanceState)
        setContent {
            IzemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WordList(
                        wordList = DataSource.words,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}