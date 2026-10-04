package com.relyvo.izem.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Phrase
import com.relyvo.izem.model.Word
import com.relyvo.izem.data.CurriculumData
import com.relyvo.izem.model.curriculum.*
import com.relyvo.izem.ui.components.GuidebookSheet
import com.relyvo.izem.ui.components.PathNode
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningPathScreen(
    isArabic: Boolean,
    totalXP: Int = 0,
    streakDays: Int = 0,
    categories: List<Category> = emptyList(),
    allWords: List<Word> = emptyList(),
    phrases: List<Phrase> = emptyList(),
    completedLessons: Map<String, Int> = emptyMap(),
    onStartLesson: (Lesson) -> Unit = {}
) {
    val course = remember(categories, allWords, phrases, isArabic) {
        CurriculumData.buildCourseFromFirestore(categories, allWords, phrases, isArabic)
    }

    val progress = remember(completedLessons) {
        UserCurriculumProgress(completedLessons = completedLessons)
    }

    var selectedGuidebook by remember { mutableStateOf<UnitGuidebook?>(null) }
    var selectedLessonForPreview by remember { mutableStateOf<Lesson?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatBadge(
                            icon = Icons.Filled.LocalFireDepartment,
                            iconTint = Color(0xFFFF6D00),
                            valueText = "$streakDays",
                            textColor = Color(0xFFFF6D00)
                        )

                        StatBadge(
                            icon = Icons.Filled.Star,
                            iconTint = Color(0xFFFFB300),
                            valueText = "$totalXP XP",
                            textColor = Color(0xFFFFB300)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            course.units.forEach { unit ->
                val unitColor = Color(unit.themeColorHex)

                // Unit Header Card
                item(key = "header_${unit.id}") {
                    UnitHeaderCard(
                        unit = unit,
                        unitColor = unitColor,
                        isArabic = isArabic,
                        onOpenGuidebook = { selectedGuidebook = unit.guidebook }
                    )
                }

                // Winding Path Lessons
                itemsIndexed(
                    items = unit.lessons,
                    key = { _, lesson -> lesson.id }
                ) { index, lesson ->
                    val isCompleted = progress.isLessonCompleted(lesson.id)
                    val isUnlocked = progress.isLessonUnlocked(lesson.id, unit.lessons)
                    val status = when {
                        isCompleted -> LessonStatus.COMPLETED
                        isUnlocked -> LessonStatus.AVAILABLE
                        else -> LessonStatus.LOCKED
                    }
                    val stars = progress.getLessonStars(lesson.id)

                    // Sinusoidal winding road offset (-60dp to +60dp)
                    val horizontalOffset = (sin(index * 1.1) * 60).dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PathNode(
                            lesson = lesson,
                            status = status,
                            stars = stars,
                            accentColor = unitColor,
                            isArabic = isArabic,
                            onClick = {
                                if (isUnlocked) {
                                    selectedLessonForPreview = lesson
                                }
                            },
                            modifier = Modifier.offset(x = horizontalOffset)
                        )
                    }
                }

                // Unit Milestone / Treasure Chest
                item(key = "chest_${unit.id}") {
                    MilestoneChest(unitColor = unitColor, isArabic = isArabic)
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Guidebook Modal Sheet
    selectedGuidebook?.let { guidebook ->
        GuidebookSheet(
            guidebook = guidebook,
            isArabic = isArabic,
            onDismiss = { selectedGuidebook = null }
        )
    }

    // Lesson Preview / Start Modal
    selectedLessonForPreview?.let { lesson ->
        LessonPreviewDialog(
            lesson = lesson,
            isArabic = isArabic,
            onDismiss = { selectedLessonForPreview = null },
            onStart = {
                val targetLesson = lesson
                selectedLessonForPreview = null
                onStartLesson(targetLesson)
            }
        )
    }
}

@Composable
private fun UnitHeaderCard(
    unit: LearningUnit,
    unitColor: Color,
    isArabic: Boolean,
    onOpenGuidebook: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = unitColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) unit.titleAr else unit.titleEn,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) unit.descriptionAr else unit.descriptionEn,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }

                // Guidebook button
                if (unit.guidebook != null) {
                    FilledTonalButton(
                        onClick = onOpenGuidebook,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White.copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Guidebook",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "الدليل" else "Notes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneChest(
    unitColor: Color,
    isArabic: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                    )
                )
                .shadow(elevation = 4.dp, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = "Milestone",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isArabic) "مكافأة الوحدة" else "Unit Reward",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}

@Composable
private fun LessonPreviewDialog(
    lesson: Lesson,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onStart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isArabic) lesson.titleAr else lesson.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isArabic) lesson.descriptionAr else lesson.descriptionEn,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "+${lesson.xpReward} XP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isArabic) "ابدأ الدرس (+${lesson.xpReward} XP)" else "Start Lesson (+${lesson.xpReward} XP)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isArabic) "إلغاء" else "Cancel")
            }
        }
    )
}

@Composable
private fun StatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    valueText: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = valueText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textColor
            )
        }
    }
}

