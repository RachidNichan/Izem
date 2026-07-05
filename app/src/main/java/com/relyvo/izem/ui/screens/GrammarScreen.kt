package com.relyvo.izem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.relyvo.izem.R
import com.relyvo.izem.model.Phrase
import com.relyvo.izem.model.Verb
import com.relyvo.izem.ui.theme.IzemGreen
import com.relyvo.izem.ui.theme.IzemGreenDark
import com.relyvo.izem.utils.SmartAudioPlayer
import com.relyvo.izem.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarScreen(
    isArabic: Boolean,
    viewModel: AppViewModel = hiltViewModel()
) {
    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) IzemGreenDark else IzemGreen

    val verbs by viewModel.verbs.collectAsStateWithLifecycle()
    val phrases by viewModel.phrases.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.grammar_verbs), stringResource(R.string.grammar_phrases))

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedVerb by remember { mutableStateOf<Verb?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = accentColor,
            divider = { HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant) },
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = accentColor
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        if (selectedTabIndex == 0) {
            VerbsList(
                verbs = verbs,
                isArabic = isArabic,
                accentColor = accentColor,
                onVerbClick = { verb ->
                    selectedVerb = verb
                    showBottomSheet = true
                }
            )
        } else {
            PhrasesList(phrases = phrases, isArabic = isArabic, accentColor = accentColor)
        }
    }

    if (showBottomSheet && selectedVerb != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedVerb = null
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            VerbDetailContent(
                verb = selectedVerb!!,
                isArabic = isArabic,
                accentColor = accentColor
            )
        }
    }
}

@Composable
fun VerbsList(
    verbs: List<Verb>,
    isArabic: Boolean,
    accentColor: Color,
    onVerbClick: (Verb) -> Unit
) {
    if (verbs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = accentColor)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(verbs) { verb ->
                VerbListItem(verb = verb, isArabic = isArabic, accentColor = accentColor, onClick = { onVerbClick(verb) })
            }
        }
    }
}

@Composable
fun VerbListItem(verb: Verb, isArabic: Boolean, accentColor: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = verb.infinitiveTifinagh,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = verb.infinitiveTamazight,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " • ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = if (isArabic) verb.infinitiveAr else verb.infinitiveEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun VerbDetailContent(verb: Verb, isArabic: Boolean, accentColor: Color) {
    val context = LocalContext.current
    var selectedTense by remember { mutableStateOf("past") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = verb.infinitiveTifinagh,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = verb.infinitiveTamazight,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isArabic) verb.infinitiveAr else verb.infinitiveEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (verb.audioUrl.isNotEmpty()) {
                FilledIconButton(
                    onClick = { SmartAudioPlayer.playAudio(context, verb.audioUrl, verb.id) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = accentColor.copy(alpha = 0.1f),
                        contentColor = accentColor
                    ),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TenseTab(
                    text = stringResource(R.string.grammar_past),
                    isSelected = selectedTense == "past",
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                ) { selectedTense = "past" }

                TenseTab(
                    text = stringResource(R.string.grammar_present),
                    isSelected = selectedTense == "present",
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                ) { selectedTense = "present" }

                TenseTab(
                    text = stringResource(R.string.grammar_future),
                    isSelected = selectedTense == "future",
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                ) { selectedTense = "future" }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val currentMapToDisplay = when (selectedTense) {
            "past" -> verb.pastTense
            "present" -> verb.presentTense
            else -> verb.futureTense
        }

        if (currentMapToDisplay.isEmpty()) {
            Text(
                text = stringResource(R.string.grammar_no_conjugation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            val sortedConjugations = currentMapToDisplay.entries.sortedBy { it.key }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                sortedConjugations.forEachIndexed { index, (rawPronoun, conjugation) ->
                    val displayPronoun = parsePronoun(rawPronoun, isArabic)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayPronoun,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = conjugation,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.6f)
                        )
                    }
                    if (index < sortedConjugations.size - 1) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TenseTab(
    text: String,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) accentColor else Color.Transparent,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun PhrasesList(phrases: List<Phrase>, isArabic: Boolean, accentColor: Color) {
    if (phrases.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = accentColor)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(phrases) { phrase ->
                PhraseCard(phrase = phrase, isArabic = isArabic, accentColor = accentColor)
            }
        }
    }
}

@Composable
fun PhraseCard(phrase: Phrase, isArabic: Boolean, accentColor: Color) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = phrase.tifinagh,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = phrase.tamazight,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (phrase.audioUrl.isNotEmpty()) {
                    FilledIconButton(
                        onClick = { SmartAudioPlayer.playAudio(context, phrase.audioUrl, phrase.id) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = accentColor.copy(alpha = 0.1f),
                            contentColor = accentColor
                        ),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isArabic) phrase.arabic else phrase.english,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val breakdown = if (isArabic) phrase.breakdownAr else phrase.breakdownEn
            if (breakdown.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = breakdown,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface,
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
