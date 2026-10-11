package com.relyvo.izem.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.relyvo.izem.R
import com.relyvo.izem.model.curriculum.ExerciseStep
import com.relyvo.izem.model.curriculum.Lesson
import com.relyvo.izem.model.curriculum.MatchPairItem
import com.relyvo.izem.utils.InterstitialAdManager
import com.relyvo.izem.utils.SmartAudioPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonRunnerScreen(
    lesson: Lesson,
    isArabic: Boolean,
    onFinish: (xpGained: Int, stars: Int) -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(lesson.id) {
        InterstitialAdManager.loadInterstitial(context, InterstitialAdManager.AD_UNIT_LESSON_END)
    }

    var stepIndex by remember { mutableStateOf(0) }
    var mistakesCount by remember { mutableStateOf(0) }
    var isLessonFinished by remember { mutableStateOf(false) }

    DisposableEffect(stepIndex) {
        onDispose {
            SmartAudioPlayer.stopAudio()
        }
    }

    val currentStep = lesson.steps.getOrNull(stepIndex)
    val progress = if (lesson.steps.isNotEmpty()) {
        (stepIndex.toFloat()) / lesson.steps.size.toFloat()
    } else 1f

    if (isLessonFinished) {
        LessonCompleteCard(
            xpGained = lesson.xpReward,
            mistakes = mistakesCount,
            isArabic = isArabic,
            onContinue = {
                val stars = when (mistakesCount) {
                    0 -> 3
                    1 -> 2
                    else -> 1
                }
                onFinish(lesson.xpReward, stars)
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        SmartAudioPlayer.stopAudio()
                        onExit()
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentStep == null) {
                LaunchedEffect(Unit) { isLessonFinished = true }
            } else {
                when (currentStep) {
                    is ExerciseStep.IntroduceCard -> {
                        IntroduceCardView(
                            step = currentStep,
                            isArabic = isArabic,
                            onNext = {
                                if (stepIndex + 1 < lesson.steps.size) {
                                    stepIndex++
                                } else {
                                    isLessonFinished = true
                                }
                            }
                        )
                    }
                    is ExerciseStep.MultipleChoice -> {
                        MultipleChoiceView(
                            step = currentStep,
                            isArabic = isArabic,
                            onAnswerChecked = { isCorrect ->
                                if (!isCorrect) mistakesCount++
                            },
                            onNext = {
                                if (stepIndex + 1 < lesson.steps.size) {
                                    stepIndex++
                                } else {
                                    isLessonFinished = true
                                }
                            }
                        )
                    }
                    is ExerciseStep.AssembleSentence -> {
                        AssembleSentenceView(
                            step = currentStep,
                            isArabic = isArabic,
                            onAnswerChecked = { isCorrect ->
                                if (!isCorrect) mistakesCount++
                            },
                            onNext = {
                                if (stepIndex + 1 < lesson.steps.size) {
                                    stepIndex++
                                } else {
                                    isLessonFinished = true
                                }
                            }
                        )
                    }
                    is ExerciseStep.Spelling -> {
                        SpellingStepView(
                            step = currentStep,
                            isArabic = isArabic,
                            onAnswerChecked = { isCorrect ->
                                if (!isCorrect) mistakesCount++
                            },
                            onNext = {
                                if (stepIndex + 1 < lesson.steps.size) {
                                    stepIndex++
                                } else {
                                    isLessonFinished = true
                                }
                            }
                        )
                    }
                    is ExerciseStep.MatchPairs -> {
                        MatchPairsStepView(
                            step = currentStep,
                            isArabic = isArabic,
                            onCompleted = {
                                if (stepIndex + 1 < lesson.steps.size) {
                                    stepIndex++
                                } else {
                                    isLessonFinished = true
                                }
                            }
                        )
                    }
                    else -> {
                        Button(onClick = {
                            if (stepIndex + 1 < lesson.steps.size) stepIndex++ else isLessonFinished = true
                        }) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 1: Introduce Word/Phrase
// -------------------------------------------------------------
@Composable
private fun IntroduceCardView(
    step: ExerciseStep.IntroduceCard,
    isArabic: Boolean,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val audioUrl = step.word?.audioUrl ?: step.phrase?.audioUrl ?: ""
    val wordId = step.word?.id ?: step.phrase?.id ?: ""

    LaunchedEffect(step.id) {
        if (audioUrl.isNotBlank()) {
            SmartAudioPlayer.playAudio(context, audioUrl, wordId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    text = if (isArabic) "كلمة جديدة" else "NEW VOCABULARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            val tifinagh = step.word?.tifinagh ?: step.phrase?.tifinagh ?: ""
            val latin = step.word?.tamazight ?: step.phrase?.tamazight ?: ""
            val translation = if (isArabic) {
                step.word?.arabic ?: step.phrase?.arabic ?: ""
            } else {
                step.word?.english ?: step.phrase?.english ?: ""
            }

            Text(
                text = tifinagh,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = latin,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = translation,
                fontSize = 20.sp,
                color = Color.Gray
            )

            if (audioUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalIconButton(
                    onClick = {
                        SmartAudioPlayer.playAudio(context, audioUrl, wordId)
                    },
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Pronounce",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            val tip = if (isArabic) step.academicTipAr else step.academicTipEn
            if (tip.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                SmartAudioPlayer.stopAudio()
                onNext()
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (isArabic) "متابعة" else "CONTINUE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------
// Step 2: Multiple Choice
// -------------------------------------------------------------
@Composable
private fun MultipleChoiceView(
    step: ExerciseStep.MultipleChoice,
    isArabic: Boolean,
    onAnswerChecked: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedOption by remember(step.id) { mutableStateOf<String?>(null) }
    var isSubmitted by remember(step.id) { mutableStateOf(false) }
    val isCorrect = selectedOption == step.correctAnswer

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = step.prompt,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (step.promptAudioUrl.isNotBlank()) {
                    IconButton(
                        onClick = {
                            SmartAudioPlayer.playAudio(context, step.promptAudioUrl, step.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
            if (step.promptSubtext.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.promptSubtext,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            step.options.forEach { option ->
                val isThisSelected = selectedOption == option
                val borderColor = when {
                    !isSubmitted && isThisSelected -> MaterialTheme.colorScheme.primary
                    isSubmitted && option == step.correctAnswer -> Color(0xFF4CAF50)
                    isSubmitted && isThisSelected && !isCorrect -> Color(0xFFE53935)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                Surface(
                    onClick = { if (!isSubmitted) selectedOption = option },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(2.dp, borderColor),
                    color = if (isThisSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = option,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Bottom Action Bar
        Column {
            if (isSubmitted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isCorrect) (if (isArabic) "إجابة صحيحة!" else "Correct!")
                            else "${if (isArabic) "إجابة خاطئة. الصحيح: " else "Incorrect. Solution: "} ${step.correctAnswer}",
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (!isSubmitted) {
                        isSubmitted = true
                        onAnswerChecked(isCorrect)
                        if (isCorrect) {
                            SmartAudioPlayer.playRawAudio(context, R.raw.correct)
                            if (step.promptAudioUrl.isNotBlank()) {
                                coroutineScope.launch {
                                    delay(400)
                                    SmartAudioPlayer.playAudio(context, step.promptAudioUrl, step.id)
                                }
                            }
                        } else {
                            SmartAudioPlayer.playRawAudio(context, R.raw.wrong)
                        }
                    } else {
                        SmartAudioPlayer.stopAudio()
                        onNext()
                    }
                },
                enabled = selectedOption != null,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (!isSubmitted) (if (isArabic) "تحقق" else "CHECK") else (if (isArabic) "متابعة" else "CONTINUE"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Step 3: Sentence Assembly
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssembleSentenceView(
    step: ExerciseStep.AssembleSentence,
    isArabic: Boolean,
    onAnswerChecked: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val selectedTokens = remember(step.id) { mutableStateListOf<String>() }
    val availableTokens = remember(step.id) { mutableStateListOf(*step.scrambledTokens.toTypedArray()) }
    var isSubmitted by remember(step.id) { mutableStateOf(false) }

    val isCorrect = selectedTokens.toList() == step.correctTokenOrder

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) "ركب هذه الجملة:" else "Translate this sentence:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) step.promptTranslationAr else step.promptTranslationEn,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (step.audioUrl.isNotBlank()) {
                    IconButton(
                        onClick = {
                            SmartAudioPlayer.playAudio(context, step.audioUrl, step.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chosen tokens box
            Surface(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 80.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedTokens.forEachIndexed { index, token ->
                        InputChip(
                            selected = true,
                            onClick = {
                                if (!isSubmitted) {
                                    selectedTokens.removeAt(index)
                                    availableTokens.add(token)
                                }
                            },
                            label = { Text(token, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Available tokens bank
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableTokens.forEachIndexed { index, token ->
                    AssistChip(
                        onClick = {
                            if (!isSubmitted) {
                                availableTokens.removeAt(index)
                                selectedTokens.add(token)
                            }
                        },
                        label = { Text(token, fontSize = 16.sp) }
                    )
                }
            }
        }

        // Bottom Action Bar
        Column {
            if (isSubmitted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isCorrect) (if (isArabic) "صحيح!" else "Correct!")
                            else "${if (isArabic) "الصحيح: " else "Solution: "} ${step.correctSentenceTamazight}",
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (!isSubmitted) {
                        isSubmitted = true
                        onAnswerChecked(isCorrect)
                        if (isCorrect) {
                            SmartAudioPlayer.playRawAudio(context, R.raw.correct)
                            if (step.audioUrl.isNotBlank()) {
                                coroutineScope.launch {
                                    delay(400)
                                    SmartAudioPlayer.playAudio(context, step.audioUrl, step.id)
                                }
                            }
                        } else {
                            SmartAudioPlayer.playRawAudio(context, R.raw.wrong)
                        }
                    } else {
                        SmartAudioPlayer.stopAudio()
                        onNext()
                    }
                },
                enabled = selectedTokens.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (!isSubmitted) (if (isArabic) "تحقق" else "CHECK") else (if (isArabic) "متابعة" else "CONTINUE"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Step 4: Spelling Step
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpellingStepView(
    step: ExerciseStep.Spelling,
    isArabic: Boolean,
    onAnswerChecked: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val guessedChars = remember(step.id) { mutableStateListOf<String>() }
    val availableChars = remember(step.id) { mutableStateListOf(*step.scrambledLetters.toTypedArray()) }
    var isSubmitted by remember(step.id) { mutableStateOf(false) }

    val formedSpelling = guessedChars.joinToString("")
    val isCorrect = formedSpelling == step.correctSpelling

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isArabic) "اكتب الكلمة بالتيفيناغ:" else "Spell the word in Tifinagh:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${step.word.tamazight} (${if (isArabic) step.word.arabic else step.word.english})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (step.word.audioUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            SmartAudioPlayer.playAudio(context, step.word.audioUrl, step.word.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Result word
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (formedSpelling.isEmpty()) "..." else formedSpelling,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Scrambled tiles
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availableChars.forEachIndexed { index, char ->
                    FilledTonalButton(
                        onClick = {
                            if (!isSubmitted) {
                                availableChars.removeAt(index)
                                guessedChars.add(char)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = char, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (guessedChars.isNotEmpty() && !isSubmitted) {
                TextButton(
                    onClick = {
                        val last = guessedChars.removeAt(guessedChars.lastIndex)
                        availableChars.add(last)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(if (isArabic) "مسح آخر حرف" else "Undo")
                }
            }
        }

        // Bottom Action Bar
        Column {
            if (isSubmitted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isCorrect) (if (isArabic) "رائع!" else "Awesome!")
                            else "${if (isArabic) "الصحيح: " else "Solution: "} ${step.correctSpelling}",
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (!isSubmitted) {
                        isSubmitted = true
                        onAnswerChecked(isCorrect)
                        if (isCorrect) {
                            SmartAudioPlayer.playRawAudio(context, R.raw.correct)
                            if (step.word.audioUrl.isNotBlank()) {
                                coroutineScope.launch {
                                    delay(400)
                                    SmartAudioPlayer.playAudio(context, step.word.audioUrl, step.word.id)
                                }
                            }
                        } else {
                            SmartAudioPlayer.playRawAudio(context, R.raw.wrong)
                        }
                    } else {
                        SmartAudioPlayer.stopAudio()
                        onNext()
                    }
                },
                enabled = guessedChars.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (!isSubmitted) (if (isArabic) "تحقق" else "CHECK") else (if (isArabic) "متابعة" else "CONTINUE"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Step 5: Match Pairs
// -------------------------------------------------------------
@Composable
private fun MatchPairsStepView(
    step: ExerciseStep.MatchPairs,
    isArabic: Boolean,
    onCompleted: () -> Unit
) {
    val context = LocalContext.current
    var selectedAmazigh by remember { mutableStateOf<String?>(null) }
    var selectedTranslation by remember { mutableStateOf<String?>(null) }
    val solvedPairIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(selectedAmazigh, selectedTranslation) {
        val am = selectedAmazigh
        val tr = selectedTranslation
        if (am != null && tr != null) {
            val match = step.pairs.find { it.amazigh == am && it.translation == tr }
            if (match != null) {
                solvedPairIds.add(match.id)
                SmartAudioPlayer.playRawAudio(context, R.raw.correct)
                if (match.audioUrl.isNotBlank()) {
                    delay(300)
                    SmartAudioPlayer.playAudio(context, match.audioUrl, match.id)
                }
            } else {
                SmartAudioPlayer.playRawAudio(context, R.raw.wrong)
            }
            selectedAmazigh = null
            selectedTranslation = null
        }
    }

    val isAllSolved = solvedPairIds.size == step.pairs.size

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isArabic) "طابق الكلمات المتقابلة:" else "Tap the matching pairs:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            step.pairs.forEach { item ->
                val isSolved = solvedPairIds.contains(item.id)
                val displayAmazigh = if (item.amazigh.contains(item.tifinagh)) item.amazigh else "${item.amazigh} (${item.tifinagh})"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left column: Amazigh
                    Surface(
                        onClick = { if (!isSolved) selectedAmazigh = item.amazigh },
                        enabled = !isSolved,
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSolved -> Color(0xFFE8F5E9)
                            selectedAmazigh == item.amazigh -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        border = if (selectedAmazigh == item.amazigh) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = displayAmazigh,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSolved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (item.audioUrl.isNotBlank() && !isSolved) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Audio",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            SmartAudioPlayer.playAudio(context, item.audioUrl, item.id)
                                        }
                                )
                            }
                        }
                    }

                    // Right column: Translation
                    Surface(
                        onClick = { if (!isSolved) selectedTranslation = item.translation },
                        enabled = !isSolved,
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSolved -> Color(0xFFE8F5E9)
                            selectedTranslation == item.translation -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        border = if (selectedTranslation == item.translation) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.translation,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSolved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                SmartAudioPlayer.stopAudio()
                onCompleted()
            },
            enabled = isAllSolved,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (isArabic) "متابعة" else "CONTINUE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------
// Celebration Screen
// -------------------------------------------------------------
@Composable
private fun LessonCompleteCard(
    xpGained: Int,
    mistakes: Int,
    isArabic: Boolean,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        SmartAudioPlayer.playRawAudio(context, R.raw.quiz_victory)
    }

    val stars = when (mistakes) {
        0 -> 3
        1 -> 2
        else -> 1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = Color(0xFFFFB300),
            modifier = Modifier.size(68.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isArabic) "أحسنت! اكتمل الدرس" else "Lesson Completed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < stars) Color(0xFFFFB300) else Color.LightGray,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "+$xpGained XP",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                SmartAudioPlayer.stopAudio()
                onContinue()
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (isArabic) "متابعة إلى المسار" else "CONTINUE TO JOURNEY",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
