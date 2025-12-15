package com.relyvo.izem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Categories : Screen("categories", "Learn", Icons.Filled.Home)

    object WordList : Screen("word_list/{categoryId}", "Words", null)

    object Quiz : Screen("quiz", "Quiz", Icons.Filled.Star)
}