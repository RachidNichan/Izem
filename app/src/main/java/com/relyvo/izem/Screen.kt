package com.relyvo.izem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val titleAr: String, val icon: ImageVector?) {

    object Categories : Screen("categories", "Learn", "تَعلُّم", Icons.Filled.Home)

    object WordList : Screen("word_list/{categoryId}", "Words", "كلمات", null)

    object Quiz : Screen("quiz", "Quiz", "اختبار", Icons.Filled.Star)
}