package com.relyvo.izem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.relyvo.izem.data.DataSource // تأكد من استدعاء ملف البيانات
import com.relyvo.izem.ui.theme.IzemTheme // قد يكون الاسم مختلفاً قليلاً حسب اسم مشروعك

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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