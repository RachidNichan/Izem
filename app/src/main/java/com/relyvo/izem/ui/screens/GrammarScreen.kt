package com.relyvo.izem.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.relyvo.izem.model.Phrase
import com.relyvo.izem.model.Verb
import com.relyvo.izem.utils.SmartAudioPlayer
import com.relyvo.izem.viewmodel.AppViewModel

// 🟢 استيراد الألوان
import com.relyvo.izem.ui.theme.IzemGreen
import com.relyvo.izem.ui.theme.GrammarContainer
import com.relyvo.izem.ui.theme.GrammarContainerDark
import com.relyvo.izem.ui.theme.IzemGreenDark
import com.relyvo.izem.ui.theme.OnGrammarContainer
import com.relyvo.izem.ui.theme.OnGrammarContainerDark

@Composable
fun GrammarScreen(
    isArabic: Boolean,
    viewModel: AppViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()
    val dynamicIzemGreen = if (isDark) IzemGreenDark else IzemGreen

    val verbs by viewModel.verbs.collectAsState()
    val phrases by viewModel.phrases.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = if (isArabic) listOf("الأفعال", "العبارات") else listOf("Verbs", "Phrases")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = dynamicIzemGreen // 🟢 لون التبويب يتغير حسب الوضع
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                )
            }
        }

        if (selectedTabIndex == 0) {
            VerbsList(verbs = verbs, isArabic = isArabic)
        } else {
            PhrasesList(phrases = phrases, isArabic = isArabic)
        }
    }
}

@Composable
fun VerbsList(verbs: List<Verb>, isArabic: Boolean) {
    if (verbs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (isArabic) "جاري تحميل الأفعال..." else "Loading verbs...", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(verbs) { verb ->
                VerbCard(verb = verb, isArabic = isArabic)
            }
        }
    }
}

@Composable
fun VerbCard(verb: Verb, isArabic: Boolean) {
    val context = LocalContext.current
    var selectedTense by remember { mutableStateOf("past") }

    // 🟢 الألوان الديناميكية للبطاقة
    val isDark = isSystemInDarkTheme()
    val dynamicGreen = if (isDark) IzemGreenDark else IzemGreen
    val dynamicContainer = if (isDark) GrammarContainerDark else GrammarContainer
    val dynamicOnContainer = if (isDark) OnGrammarContainerDark else OnGrammarContainer

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = dynamicContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = verb.infinitiveTifinagh,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = dynamicGreen
                    )
                    Text(
                        text = "${verb.infinitiveTamazight} • ${if (isArabic) verb.infinitiveAr else verb.infinitiveEn}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = dynamicOnContainer
                    )
                }

                if (verb.audioUrl.isNotEmpty()) {
                    IconButton(
                        onClick = { SmartAudioPlayer.playAudio(context, verb.audioUrl, verb.id) },
                        modifier = Modifier.background(dynamicGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = dynamicGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TenseButton(
                    text = if (isArabic) "الماضي" else "Past",
                    isSelected = selectedTense == "past",
                    modifier = Modifier.weight(1f)
                ) { selectedTense = "past" }

                TenseButton(
                    text = if (isArabic) "المضارع" else "Present",
                    isSelected = selectedTense == "present",
                    modifier = Modifier.weight(1f)
                ) { selectedTense = "present" }

                TenseButton(
                    text = if (isArabic) "المستقبل" else "Future",
                    isSelected = selectedTense == "future",
                    modifier = Modifier.weight(1f)
                ) { selectedTense = "future" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val currentMapToDisplay = when (selectedTense) {
                "past" -> verb.pastTense
                "present" -> verb.presentTense
                else -> verb.futureTense
            }

            if (currentMapToDisplay.isEmpty()) {
                Text(
                    text = if (isArabic) "لم يتم إضافة التصريف بعد" else "Conjugation not added yet",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                val sortedConjugations = currentMapToDisplay.entries.sortedBy { it.key }

                sortedConjugations.forEach { (rawPronoun, conjugation) ->
                    val displayPronoun = parsePronoun(rawPronoun, isArabic)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = displayPronoun, fontWeight = FontWeight.Bold, color = dynamicGreen)
                        Text(text = conjugation, fontWeight = FontWeight.Medium, color = dynamicOnContainer)
                    }
                    HorizontalDivider(color = dynamicGreen.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
fun TenseButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val dynamicGreen = if (isDark) IzemGreenDark else IzemGreen

    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) dynamicGreen else Color.Transparent,
            contentColor = if (isSelected) Color.White else dynamicGreen
        ),
        border = if (!isSelected) BorderStroke(1.dp, dynamicGreen) else null
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PhrasesList(phrases: List<Phrase>, isArabic: Boolean) {
    if (phrases.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (isArabic) "جاري تحميل العبارات..." else "Loading phrases...", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(phrases) { phrase ->
                PhraseCard(phrase = phrase, isArabic = isArabic)
            }
        }
    }
}

@Composable
fun PhraseCard(phrase: Phrase, isArabic: Boolean) {
    val context = LocalContext.current

    // 🟢 الألوان الديناميكية للبطاقة
    val isDark = isSystemInDarkTheme()
    val dynamicGreen = if (isDark) IzemGreenDark else IzemGreen
    val dynamicContainer = if (isDark) GrammarContainerDark else GrammarContainer
    val dynamicOnContainer = if (isDark) OnGrammarContainerDark else OnGrammarContainer

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = dynamicContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // --- Header: Tifinagh & Audio ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = phrase.tifinagh,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = dynamicGreen,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                }

                if (phrase.audioUrl.isNotEmpty()) {
                    IconButton(
                        onClick = { SmartAudioPlayer.playAudio(context, phrase.audioUrl, phrase.id) },
                        modifier = Modifier
                            .background(dynamicGreen.copy(alpha = 0.1f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = dynamicGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = phrase.tamazight,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = dynamicOnContainer
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isArabic) phrase.arabic else phrase.english,
                style = MaterialTheme.typography.bodyLarge,
                color = dynamicOnContainer.copy(alpha = 0.8f)
            )

            val breakdown = if (isArabic) phrase.breakdownAr else phrase.breakdownEn
            if (breakdown.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = dynamicGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = breakdown,
                        modifier = Modifier.padding(12.dp),
                        color = dynamicOnContainer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = if (isArabic) TextDirection.Rtl else TextDirection.Ltr
                        )
                    )
                }
            }
        }
    }
}

fun parsePronoun(rawKey: String, isArabic: Boolean): String {
    val cleanKey = rawKey.substringAfter(". ").trim()

    if (!cleanKey.contains("(") || !cleanKey.contains(")")) {
        return cleanKey
    }

    return try {
        if (isArabic) {
            val startIndex = cleanKey.indexOf("(") + 1
            val endIndex = cleanKey.indexOf(")")
            cleanKey.substring(startIndex, endIndex).trim()
        } else {
            val endIndex = cleanKey.indexOf("(")
            cleanKey.substring(0, endIndex).trim()
        }
    } catch (_: Exception) {
        cleanKey
    }
}