package com.relyvo.izem.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.relyvo.izem.viewmodel.AppViewModel

@Composable
fun MainScreen(viewModel: AppViewModel = viewModel()) {

    val navController = rememberNavController()

    val categories by viewModel.categories.collectAsState()
    val currentWords by viewModel.currentWords.collectAsState()

    val bottomNavItems = listOf(
        Screen.Categories,
        Screen.Quiz,
        Screen.Profile
    )

    val isArabic by viewModel.isArabic.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = null) },
                        label = {
                            Text(
                                text = if (isArabic) screen.titleAr else screen.title,
                                fontWeight = FontWeight.Bold
                            )
                        },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Categories.route
                ) {

                    composable(Screen.Categories.route) {
                        CategoryScreen(
                            categoriesList = categories,
                            isArabic = isArabic,
                            onCategoryClick = { categoryId ->
                                viewModel.listenWordsByCategory(categoryId)
                                navController.navigate("word_list/$categoryId")
                            },
                            onLanguageToggle = { viewModel.toggleLanguage() }
                        )
                    }

                    composable("word_list/{categoryId}") { backStackEntry ->
                        val categoryId = backStackEntry.arguments?.getString("categoryId")

                        if (categoryId == "alphabet") {
                            val alphabetLetters = currentWords
                            AlphabetScreen(
                                letters = currentWords,
                                isArabic = isArabic
                            )
                        } else {
                            WordList(wordList = currentWords, isArabic = isArabic)
                        }
                    }

                    composable(Screen.Quiz.route) {
                        QuizScreen(
                            isArabic = isArabic,
                            viewModel = viewModel,
                            onBackToMenu = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            isArabic = isArabic,
                            viewModel = viewModel
                        )
                    }
                }
            }

            AdmobBanner()
        }
    }
}