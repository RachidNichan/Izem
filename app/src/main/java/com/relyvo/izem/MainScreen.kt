package com.relyvo.izem

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.relyvo.izem.data.DataSource

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(Screen.Categories, Screen.Quiz)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Categories.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Categories.route) {
                CategoryScreen(
                    onCategoryClick = { categoryId ->
                        navController.navigate("word_list/$categoryId")
                    }
                )
            }

            composable("word_list/{categoryId}") { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")

                if (categoryId == "alphabet") {
                    AlphabetScreen(letters = DataSource.alphabetList)
                } else {
                    val words = DataSource.getWordsByCategory(categoryId ?: "")
                    WordList(wordList = words)
                }
            }

            composable(Screen.Quiz.route) {
                QuizScreen()
            }
        }
    }
}