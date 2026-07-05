package com.relyvo.izem.ui.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.relyvo.izem.R

sealed class Screen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector?) {

    object Categories : Screen("categories", R.string.nav_learn, Icons.Filled.Home)

    object WordList : Screen("word_list/{categoryId}", R.string.nav_words, null)

    object Quiz : Screen("quiz", R.string.nav_quiz, Icons.Filled.Star)

    object Grammar : Screen("grammar", R.string.nav_grammar, Icons.AutoMirrored.Filled.MenuBook)

    object Profile : Screen("profile", R.string.nav_profile, Icons.Filled.Person)

    object Leaderboard : Screen("leaderboard", R.string.nav_leaderboard, null)

}
