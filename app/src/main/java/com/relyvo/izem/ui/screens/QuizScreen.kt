package com.relyvo.izem.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.play.core.review.ReviewManagerFactory
import com.relyvo.izem.LocalActivity
import com.relyvo.izem.R
import com.relyvo.izem.model.QuizQuestion
import com.relyvo.izem.model.IndexedLetter
import com.relyvo.izem.ui.components.QuizOption
import com.relyvo.izem.ui.components.QuizResultUI
import com.relyvo.izem.ui.components.shareResult
import com.relyvo.izem.utils.InterstitialAdManager
import com.relyvo.izem.utils.SmartAudioPlayer
import com.relyvo.izem.utils.Utils
import com.relyvo.izem.viewmodel.AppViewModel
import com.relyvo.izem.ui.theme.IzemBlue
import com.relyvo.izem.ui.theme.IzemGold
import com.relyvo.izem.ui.theme.IzemOrange
import kotlin.collections.map

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizScreen(
    isArabic: Boolean,
    viewModel: AppViewModel = hiltViewModel(),
    onBackToMenu: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val quizWordsBase by viewModel.allWords.collectAsStateWithLifecycle()
    val quizState by viewModel.quizUiState.collectAsStateWithLifecycle()

    val totalQuestions = quizState.questions.size.coerceAtLeast(1)
    val progress by animateFloatAsState(
        targetValue = if (quizState.questions.isNotEmpty()) {
            (quizState.currentIndex + 1).toFloat() / totalQuestions
        } else {
            0f
        }
    )

    val reviewManager = remember { ReviewManagerFactory.create(context) }

    fun showReviewDialog() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val activity = Utils.findActivity(context)
                activity?.let {
                    reviewManager.launchReviewFlow(it, reviewInfo)
                }
            }
        }
    }

    LaunchedEffect(quizWordsBase) {
        if (quizWordsBase.isNotEmpty() && quizState.questions.isEmpty()) {
            viewModel.startNewQuiz()
            InterstitialAdManager.loadInterstitial(context)
        }
    }

    LaunchedEffect(quizState.isAnswerChecked) {
        if (quizState.isAnswerChecked && quizState.questions.isNotEmpty()) {
            val currentQuestion = quizState.questions[quizState.currentIndex]
            if (quizState.isCorrect) {
                val word = when (currentQuestion) {
                    is QuizQuestion.TextQuestion -> currentQuestion.word
                    is QuizQuestion.ImageQuestion -> currentQuestion.word
                    is QuizQuestion.SpellingQuestion -> currentQuestion.word
                }
                Utils.getAudioId(context, "correct").takeIf { it != 0 }?.let {
                    SmartAudioPlayer.playRawAudio(context, it)
                }

                kotlinx.coroutines.delay(300)
                SmartAudioPlayer.playAudio(context, word.audioUrl, word.id)
            } else {
                Utils.getAudioId(context, "wrong").takeIf { it != 0 }?.let {
                    SmartAudioPlayer.playRawAudio(context, it)
                }
            }
        }
    }

    if (quizState.questions.isEmpty() && !quizState.isCompleted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(strokeWidth = 5.dp, color = IzemBlue)
        }
        return
    }

    if (quizState.isCompleted) {
        QuizResultUI(
            score = quizState.score * 10,
            total = totalQuestions * 10,
            onShare = { shareResult(context, quizState.score * 10, isArabic) },
            onPlayAgain = {
                viewModel.startNewQuiz()
            },
            onExit = onBackToMenu
        )

        LaunchedEffect(Unit) {
            Utils.getAudioId(context, "quiz_victory").takeIf { it != 0 }?.let {
                SmartAudioPlayer.playRawAudio(context, it)
            }

            showReviewDialog()
        }
    } else {
        val currentQuestion = quizState.questions[quizState.currentIndex]

        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.quiz_question_count, (quizState.currentIndex + 1).toString()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.quiz_xp, (quizState.score * 10).toString()),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when (currentQuestion) {
                    is QuizQuestion.TextQuestion -> {
                        TextQuestionContent(question = currentQuestion, isArabic = isArabic)

                        Spacer(modifier = Modifier.height(24.dp))

                        // الخيارات
                        currentQuestion.options.forEach { option ->
                            val isSelected = quizState.selectedAnswer == option
                            val isCorrectTarget = option == currentQuestion.correctAnswer

                            QuizOption(
                                option = option,
                                isSelected = isSelected,
                                isCorrect = isCorrectTarget,
                                reveal = quizState.isAnswerChecked,
                                onClick = {
                                    viewModel.selectOption(option)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    is QuizQuestion.ImageQuestion -> {
                        ImageQuestionContent(question = currentQuestion, isArabic = isArabic)

                        Spacer(modifier = Modifier.height(24.dp))

                        currentQuestion.options.forEach { option ->
                            val isSelected = quizState.selectedAnswer == option
                            val isCorrectTarget = option == currentQuestion.correctAnswer

                            QuizOption(
                                option = option,
                                isSelected = isSelected,
                                isCorrect = isCorrectTarget,
                                reveal = quizState.isAnswerChecked,
                                onClick = {
                                    viewModel.selectOption(option)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    is QuizQuestion.SpellingQuestion -> {
                        SpellingQuestionContent(
                            question = currentQuestion,
                            guessedLetters = quizState.spellingGuessedLetters,
                            isAnswerChecked = quizState.isAnswerChecked,
                            isCorrect = quizState.isCorrect,
                            onLetterClick = { id, char ->
                                viewModel.addLetterToSpelling(id, char)
                            },
                            onRemoveLetter = { indexedLetter ->
                                viewModel.removeLetterFromSpelling(indexedLetter)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(30.dp))

                val hasSelected = quizState.selectedAnswer.isNotEmpty()

                Button(
                    onClick = {
                        if (!quizState.isAnswerChecked) {
                            viewModel.checkAnswer()
                        } else {
                            val isLastQuestion = quizState.currentIndex == totalQuestions - 1
                            if (isLastQuestion) {
                                InterstitialAdManager.showInterstitial(activity) {
                                    viewModel.moveToNextQuestion()
                                }
                            } else {
                                viewModel.moveToNextQuestion()
                            }
                        }
                    },
                    enabled = hasSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!quizState.isAnswerChecked) IzemBlue else IzemOrange
                    )
                ) {
                    Text(
                        text = when {
                            !quizState.isAnswerChecked -> if (isArabic) "تحقق" else "Check"
                            quizState.currentIndex < totalQuestions - 1 -> if (isArabic) "التالي" else "Next"
                            else -> if (isArabic) "إنهاء" else "Finish"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TextQuestionContent(question: QuizQuestion.TextQuestion, isArabic: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(12.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isArabic) "كيف نقول بالأمازيغية؟" else "How to say?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isArabic) question.word.arabic else question.word.english,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ImageQuestionContent(question: QuizQuestion.ImageQuestion, isArabic: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (isArabic) "ماذا تلاحظ في الصورة؟" else "What is in the image?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .size(220.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(3.dp, IzemGold)
        ) {
            AsyncImage(
                model = question.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpellingQuestionContent(
    question: QuizQuestion.SpellingQuestion,
    guessedLetters: List<IndexedLetter>,
    isAnswerChecked: Boolean,
    isCorrect: Boolean,
    onLetterClick: (Int, String) -> Unit,
    onRemoveLetter: (IndexedLetter) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .size(140.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, IzemGold)
        ) {
            AsyncImage(
                model = question.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val expectedLength = question.correctAnswer.length

            for (i in 0 until expectedLength) {
                val indexedLetter = guessedLetters.getOrNull(i)
                val boxBorderColor = when {
                    !isAnswerChecked && indexedLetter != null -> IzemBlue
                    isAnswerChecked && isCorrect -> Color(0xFF4CAF50)
                    isAnswerChecked && !isCorrect -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (indexedLetter != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .border(1.5.dp, boxBorderColor, RoundedCornerShape(8.dp))
                        .clickable(enabled = indexedLetter != null && !isAnswerChecked) {
                            if (indexedLetter != null) onRemoveLetter(indexedLetter)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = indexedLetter?.char ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        val usedIds = guessedLetters.map { it.id }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 6
        ) {
            question.scrambledLetters.forEachIndexed { index, letter ->
                val isUsed = index in usedIds

                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(46.dp)
                        .shadow(if (isUsed) 0.dp else 4.dp, CircleShape)
                        .clickable(enabled = !isUsed && !isAnswerChecked) {
                            onLetterClick(index, letter)
                        },
                    shape = CircleShape,
                    color = if (isUsed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else IzemGold,
                    contentColor = if (isUsed) Color.Gray.copy(alpha = 0.5f) else Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}