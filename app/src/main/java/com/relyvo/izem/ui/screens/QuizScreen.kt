package com.relyvo.izem.ui.screens

import android.app.Activity
import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.play.core.review.ReviewManagerFactory
import com.relyvo.izem.model.Word
import com.relyvo.izem.utils.InterstitialAdManager
import com.relyvo.izem.utils.Utils
import com.relyvo.izem.viewmodel.AppViewModel

@Composable
fun QuizScreen(
    isArabic: Boolean,
    viewModel: AppViewModel = viewModel(),
    onBackToMenu: () -> Unit = {}
) {
    val context = LocalContext.current

    // Loading State
    val quizWordsBase by viewModel.allWords.collectAsState()

    if (quizWordsBase.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(strokeWidth = 5.dp)
        }
        return
    }

    val totalQuestions = 10

    var quizSessionWords by remember {
        mutableStateOf(quizWordsBase.shuffled().take(totalQuestions))
    }

    var score by rememberSaveable { mutableIntStateOf(0) }
    var questionCount by rememberSaveable { mutableIntStateOf(1) }
    var isGameOver by rememberSaveable { mutableStateOf(false) }

    var currentWord by remember(questionCount, quizSessionWords) {
        mutableStateOf(quizSessionWords[questionCount - 1])
    }

    var options by remember(currentWord) {
        mutableStateOf(
            (listOf(currentWord) + quizWordsBase.filter { it != currentWord }.shuffled().take(3))
                .shuffled()
        )
    }

    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val progress by animateFloatAsState(targetValue = questionCount.toFloat() / totalQuestions)

    val reviewManager = remember { ReviewManagerFactory.create(context) }

    fun showReviewDialog() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val activity = context as? Activity
                activity?.let {
                    reviewManager.launchReviewFlow(it, reviewInfo)
                }
            }
        }
    }

    LaunchedEffect(selectedAnswer) {
        if (selectedAnswer != null && isCorrect) {
            kotlinx.coroutines.delay(1500) // Delay auto-next on correct answer

            if (questionCount < totalQuestions) {
                questionCount++
                selectedAnswer = null
                isCorrect = false
            } else {
                (context as? Activity)?.let { InterstitialAdManager.showInterstitial(it) { isGameOver = true } }
            }
        }
    }

    if (isGameOver) {
        QuizResultUI(
            score = score,
            total = totalQuestions * 10,
            isArabic = isArabic,
            onShare = { shareResult(context, score, isArabic) },
            onPlayAgain = {
                quizSessionWords = quizWordsBase.shuffled().take(totalQuestions)
                score = 0
                questionCount = 1
                isGameOver = false
                selectedAnswer = null
                isCorrect = false
            },
            onExit = onBackToMenu
        )

        LaunchedEffect(Unit) {
            viewModel.finishQuizSession(score)
            showReviewDialog()
        }
    } else {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if(isArabic) "السؤال $questionCount" else "Question $questionCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "XP: $score", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Question Card
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).shadow(12.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isArabic) "كيف نقول:" else "How do you say:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isArabic) currentWord.arabic else currentWord.english,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Options
                options.forEach { option ->
                    val isSelected = selectedAnswer == option
                    val isTarget = option == currentWord

                    QuizOption(
                        option = option,
                        isSelected = isSelected,
                        isCorrect = isTarget,
                        reveal = selectedAnswer != null,
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = option
                                isCorrect = (option == currentWord)

                                if (isCorrect) {
                                    score += 10
                                    com.relyvo.izem.utils.SmartAudioPlayer.playAudio(context, option.audioUrl, option.id)
                                } else {
                                    Utils.getAudioId(context, "wrong").takeIf { it != 0 }?.let { MediaPlayer.create(context, it).start() }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                // Next Button
                AnimatedVisibility(
                    visible = selectedAnswer != null && (!isCorrect || questionCount == totalQuestions),
                    enter = scaleIn() + fadeIn()
                ) {
                    Button(
                        onClick = {
                            if (questionCount < totalQuestions) {
                                questionCount++
                                selectedAnswer = null
                            } else {
                                (context as? Activity)?.let { InterstitialAdManager.showInterstitial(it) { isGameOver = true } }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(if (questionCount < totalQuestions) (if (isArabic) "التالي ⬅" else "Next Question ➡") else (if (isArabic) "إنهاء 🏁" else "Finish 🏁"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuizOption(option: Word, isSelected: Boolean, isCorrect: Boolean, reveal: Boolean, onClick: () -> Unit) {
    val bgColor = when {
        reveal && isCorrect -> Color(0xFF4CAF50)
        reveal && isSelected && !isCorrect -> Color(0xFFF44336)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = if (reveal && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !reveal) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(2.dp, if (isSelected && !reveal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${option.tamazight.uppercase()} (${option.tifinagh})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            if (reveal) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun QuizResultUI(score: Int, total: Int, isArabic: Boolean, onShare: () -> Unit, onPlayAgain: () -> Unit, onExit: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (isArabic) "أحسنت! 🦁" else "Well Done! 🦁", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (isArabic) "مجموع النقاط" else "Total XP Gained", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    Text(text = "$score / $total", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black, color = if (score >= total / 2) Color(0xFF4CAF50) else Color(0xFFF44336))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = onShare, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                Icon(Icons.Default.Share, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isArabic) "شارك النتيجة" else "Share Result")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
                Text(if (isArabic) "إلعب مجدداً" else "Play Again")
            }
            TextButton(onClick = onExit, modifier = Modifier.padding(top = 8.dp)) {
                Text(if (isArabic) "خروج" else "Exit", color = Color.Gray)
            }
        }
    }
}

fun shareResult(context: android.content.Context, score: Int, isArabic: Boolean) {
    val appLink = "https://play.google.com/store/apps/details?id=com.relyvo.izem"
    val message = if (isArabic) "لقد حصلت على ${score} نقطة في تطبيق إيزم (Izem)! 🦁✨\n$appLink" else "I just scored ${score} XP in Izem App! 🦁\n$appLink"
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_TEXT, message)
    }
    context.startActivity(android.content.Intent.createChooser(intent, "Share Result"))
}