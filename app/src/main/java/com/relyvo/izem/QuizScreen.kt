package com.relyvo.izem

import android.app.Activity
import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.play.core.review.ReviewManagerFactory
import com.relyvo.izem.data.DataSource
import com.relyvo.izem.model.Word
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun QuizScreen(isArabic: Boolean) {
    val context = LocalContext.current

    val reviewManager = remember { ReviewManagerFactory.create(context) }

    var currentWord by remember { mutableStateOf(DataSource.allWords.random()) }
    var options by remember(currentWord) {
        mutableStateOf(
            (listOf(currentWord) + DataSource.allWords.filter { it != currentWord }.shuffled().take(3))
                .shuffled()
        )
    }

    var score by rememberSaveable { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isArabic) "النقاط: $score" else "Score: $score",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = LocalTextStyle.current.copy(textDirection = androidx.compose.ui.text.style.TextDirection.Content)
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
                            if (score == 50) {
                                showReviewDialog()
                            }
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
                    currentWord = DataSource.allWords.random()
                    selectedAnswer = null
                    isCorrect = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(if (isArabic) "السؤال التالي ⬅" else "Next Question ➡")
            }
        }
    }
}