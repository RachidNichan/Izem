package com.relyvo.izem.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.relyvo.izem.ui.theme.IzemBlue
import com.relyvo.izem.ui.theme.IzemGold
import com.relyvo.izem.ui.theme.IzemOrange
import com.relyvo.izem.viewmodel.AppViewModel

@Composable
fun MainScreen(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val categories by viewModel.categories.collectAsState()
    val currentWords by viewModel.currentWords.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.trackVisit()
    }

    val bottomNavItems = listOf(
        Screen.Categories,
        Screen.Quiz,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        val activeColor = when (screen) {
                            Screen.Categories -> IzemBlue
                            Screen.Quiz -> IzemOrange
                            Screen.Profile -> IzemGold
                            else -> MaterialTheme.colorScheme.primary
                        }

                        val indicatorColor = activeColor.copy(alpha = 0.15f)

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon!!,
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (isArabic) screen.titleAr else screen.title,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = activeColor,
                                selectedTextColor = activeColor,
                                indicatorColor = indicatorColor,
                                unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                                unselectedTextColor = Color.Gray.copy(alpha = 0.6f)
                            ),
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Categories.route,
                    enterTransition = { fadeIn() + scaleIn(initialScale = 0.98f) },
                    exitTransition = { fadeOut() }
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
                            AlphabetScreen(
                                letters = currentWords,
                                isArabic = isArabic,
                                onLetterClick = { wordId -> viewModel.onWordClicked(wordId) }
                            )
                        } else {
                            WordList(
                                wordList = currentWords,
                                isArabic = isArabic,
                                onWordClick = { wordId -> viewModel.onWordClicked(wordId) }
                            )
                        }
                    }

                    composable(Screen.Quiz.route) {
                        QuizScreen(
                            isArabic = isArabic,
                            viewModel = viewModel,
                            onBackToMenu = {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(Screen.Categories.route)
                                }
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    AdmobBanner()
                }
            }
        }
    }
}