package com.relyvo.izem.ui.screens

import android.app.Activity
import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val allWords by viewModel.allWords.collectAsState()

    val quizWords = remember(allWords) {
        allWords.filter { it.categoryId != "alphabet" }
    }

    if (quizWords.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val scrollState = rememberScrollState()

    var currentWord by remember { mutableStateOf(quizWords.random()) }

    var options by remember(currentWord) {
        mutableStateOf(
            (listOf(currentWord) + quizWords.filter { it != currentWord }.shuffled().take(3))
                .shuffled()
        )
    }

    var score by rememberSaveable { mutableIntStateOf(0) }
    var questionCount by rememberSaveable { mutableIntStateOf(1) }
    val totalQuestions = 10
    var isGameOver by rememberSaveable { mutableStateOf(false) }

    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf(false) }

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

    fun shareResult(context: android.content.Context, score: Int, isArabic: Boolean) {
        val appLink = "https://play.google.com/store/apps/details?id=com.relyvo.izem"

        val message = if (isArabic) {
            "لقد حصلت على ${score} نقطة في تطبيق إيزم (Izem) لتعلم اللغة الأمازيغية! 🦁✨\nحمل التطبيق من هنا: $appLink"
        } else {
            "I just scored ${score} XP in Izem App! 🦁 Learning Tamazight is fun. Check it out: $appLink"
        }

        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, message)
        }

        context.startActivity(android.content.Intent.createChooser(intent, if(isArabic) "مشاركة النتيجة" else "Share Result"))
    }

    if (isGameOver) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isArabic) "انتهت اللعبة! \uD83C\uDF89" else "Game Over! \uD83C\uDF89",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isArabic) "النتيجة النهائية" else "Final Score",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "$score / ${totalQuestions * 10}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = if (score > 50) Color(0xFF4CAF50) else Color(0xFFF44336)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { shareResult(context, score, isArabic) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isArabic) "مشاركة النتيجة" else "Share Result")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    score = 0
                    questionCount = 1
                    isGameOver = false
                    currentWord = quizWords.random()
                    selectedAnswer = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isArabic) "إلعب مجدداً" else "Play Again")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { onBackToMenu() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isArabic) "خروج" else "Exit")
            }
        }

        LaunchedEffect(Unit) {
            viewModel.finishQuizSession(score)

            showReviewDialog()
        }

    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if(isArabic) "السؤال: $questionCount/$totalQuestions" else "Question: $questionCount/$totalQuestions",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if(isArabic) "النقاط: $score" else "Score: $score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isArabic) "كيف نقول:" else "How do you say:",
                        fontSize = 18.sp,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        style = LocalTextStyle.current.copy(textDirection = TextDirection.Content)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isArabic) currentWord.arabic else currentWord.english,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (isArabic && currentWord.arabic.length > 10) 32.sp else 45.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            options.forEach { option ->
                val isSelected = (selectedAnswer == option)
                val isTarget = (option == currentWord)

                val buttonColor = when {
                    selectedAnswer == null -> MaterialTheme.colorScheme.primaryContainer
                    isTarget -> Color(0xFF4CAF50)
                    isSelected && !isCorrect -> Color(0xFFF44336)
                    else -> Color.LightGray
                }

                Button(
                    onClick = {
                        if (selectedAnswer == null) {
                            selectedAnswer = option
                            isCorrect = (option == currentWord)

                            val soundId = if (isCorrect) Utils.getAudioId(context, "correct") else Utils.getAudioId(context, "wrong")
                            if (soundId != 0) {
                                val mediaPlayer = MediaPlayer.create(context, soundId)
                                mediaPlayer.start()
                                mediaPlayer.setOnCompletionListener { mp -> mp.release() }
                            }

                            if (isCorrect) {
                                score += 10
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = "${option.tamazight}  (${option.tifinagh})",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedAnswer != null) {
                Button(
                    onClick = {
                        if (questionCount < totalQuestions) {
                            questionCount++
                            currentWord = quizWords.random()
                            selectedAnswer = null
                            isCorrect = false

                            options = (listOf(currentWord) + quizWords.filter { it != currentWord }.shuffled().take(3)).shuffled()

                        } else {
                            val activity = context as? Activity
                            activity?.let { act ->
                                InterstitialAdManager.showInterstitial(act) {
                                    isGameOver = true
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        text = if (questionCount < totalQuestions)
                            (if (isArabic) "السؤال التالي ⬅" else "Next Question ➡")
                        else
                            (if (isArabic) "إنهاء 🏁" else "Finish 🏁")
                    )
                }
            }
        }
    }
}