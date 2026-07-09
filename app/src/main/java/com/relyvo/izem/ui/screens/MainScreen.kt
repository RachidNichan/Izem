package com.relyvo.izem.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.relyvo.izem.model.Word
import com.relyvo.izem.ui.modal.ContributionSheet
import com.relyvo.izem.ui.theme.IzemBlue
import com.relyvo.izem.ui.theme.IzemGold
import com.relyvo.izem.ui.theme.IzemGreen
import com.relyvo.izem.ui.theme.IzemGreenDark
import com.relyvo.izem.ui.theme.IzemOrange
import com.relyvo.izem.viewmodel.AppViewModel

import com.relyvo.izem.LocalActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AppViewModel = hiltViewModel()) {
    val activity = LocalActivity.current
    
    // Determine the initial screen from the intent to avoid "jumping"
    val initialRoute = remember {
        val intent = activity.intent
        if (intent?.getStringExtra("type") == "roar" || intent?.getStringExtra("navigate_to") == "leaderboard") {
            Screen.Leaderboard.route
        } else {
            Screen.Categories.route
        }
    }

    val navController = rememberNavController()
    val categories by viewModel.categories.collectAsState()
    val currentWords by viewModel.currentWords.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()

    // 1. Listen for new intents while the app is running (Background to Foreground)
    DisposableEffect(activity) {
        val listener = androidx.core.util.Consumer<android.content.Intent> { intent ->
            val type = intent.getStringExtra("type")
            val navigateTo = intent.getStringExtra("navigate_to")
            android.util.Log.d("IzemNav", "onNewIntent received: type=$type, navigateTo=$navigateTo")
            
            if (type == "roar" || navigateTo == "leaderboard") {
                try {
                    navController.navigate(Screen.Leaderboard.route) {
                        launchSingleTop = true
                    }
                } catch (e: Exception) {
                    android.util.Log.e("IzemNav", "Navigation error in onNewIntent: ${e.message}")
                }
            }
        }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }

    // 2. Clear initial intent extras after handling them via initialRoute
    LaunchedEffect(Unit) {
        val intent = activity.intent
        if (intent?.getStringExtra("type") == "roar" || intent?.getStringExtra("navigate_to") == "leaderboard") {
            intent.removeExtra("type")
            intent.removeExtra("navigate_to")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.trackVisit()
    }

    val bottomNavItems = listOf(
        Screen.Categories,
        Screen.Quiz,
        Screen.Grammar,
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
                    val currentRoute = currentDestination?.route

                    bottomNavItems.forEach { screen ->

                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true ||
                                (screen == Screen.Profile && currentRoute == Screen.Leaderboard.route) ||
                                (screen == Screen.Categories && currentRoute?.startsWith("word_list") == true)

                        val activeColor = when (screen) {
                            Screen.Categories -> IzemBlue
                            Screen.Quiz -> IzemOrange
                            Screen.Profile -> IzemGold
                            Screen.Grammar -> if (androidx.compose.foundation.isSystemInDarkTheme()) IzemGreenDark else IzemGreen
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
                                    text = stringResource(id = screen.titleRes),
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
                                if (currentRoute == Screen.Leaderboard.route && screen == Screen.Profile) {
                                    navController.popBackStack(Screen.Profile.route, inclusive = false)
                                } else if (currentRoute?.startsWith("word_list") == true && screen == Screen.Categories) {
                                    navController.popBackStack(Screen.Categories.route, inclusive = false)
                                } else {
                                    val isLearnTab = screen == Screen.Categories

                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = !isLearnTab
                                        }

                                        launchSingleTop = true
                                        restoreState = !isLearnTab
                                    }
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
                    startDestination = initialRoute,
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
                        val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                        var selectedWordForCorrection by remember { mutableStateOf<Word?>(null) }
                        var showSheet by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.fillMaxSize()) {
                            if (categoryId == "alphabet") {
                                AlphabetScreen(
                                    letters = currentWords,
                                    isArabic = isArabic,
                                    onLetterClick = { wordId -> viewModel.onWordClicked(wordId) }
                                )
                            } else {
                                WordList(
                                    categoryId = categoryId,
                                    wordList = currentWords,
                                    isArabic = isArabic,
                                    onWordClick = { wordId -> viewModel.onWordClicked(wordId) },
                                    viewModel = viewModel
                                )
                            }

                            if (showSheet) {
                                ModalBottomSheet(
                                    onDismissRequest = {
                                        showSheet = false
                                        selectedWordForCorrection = null
                                    },
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                ) {
                                    ContributionSheet(
                                        isArabic = isArabic,
                                        categoryId = categoryId,
                                        existingWord = selectedWordForCorrection,
                                        onDismiss = { showSheet = false },
                                        viewModel = viewModel
                                    )
                                }
                            }
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

                    composable(Screen.Grammar.route) {
                        GrammarScreen(
                            isArabic = isArabic,
                            viewModel = viewModel
                        )
                    }

                    composable(Screen.Leaderboard.route) {
                        LeaderboardScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            isArabic = isArabic,
                            viewModel = viewModel,
                            onLeaderboardClick = {
                                navController.navigate(Screen.Leaderboard.route)
                            }
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