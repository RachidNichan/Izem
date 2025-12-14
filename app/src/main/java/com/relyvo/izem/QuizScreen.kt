package com.relyvo.izem

import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relyvo.izem.data.DataSource
import com.relyvo.izem.model.Word

@Composable
fun QuizScreen() {

    val context = LocalContext.current

    var currentWord by remember { mutableStateOf(DataSource.words.random()) }

    var options by remember(currentWord) {
        mutableStateOf(
            (listOf(currentWord) + DataSource.words.filter { it != currentWord }.shuffled().take(3))
                .shuffled()
        )
    }

    var score by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Word?>(null) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Score: $score",
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
                Text(text = "How do you say:", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentWord.english,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            val isSelected = (selectedAnswer == option)
            val isTarget = (option == currentWord)

            val buttonColor = when {
                selectedAnswer == null -> MaterialTheme.colorScheme.primaryContainer
                isTarget -> Color(0xFF4CAF50) // Green
                isSelected && !isCorrect -> Color(0xFFF44336) // Red
                else -> Color.LightGray
            }

            Button(
                onClick = {
                    if (selectedAnswer == null) {
                        selectedAnswer = option
                        isCorrect = (option == currentWord)

                        val soundId = if (isCorrect) R.raw.correct else R.raw.wrong
                        val mediaPlayer = MediaPlayer.create(context, soundId)
                        mediaPlayer.start()
                        mediaPlayer.setOnCompletionListener { mp -> mp.release() }

                        if (isCorrect) score += 10
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
                    // هنا فقط نعيد تعيين اللعبة (Reset)
                    currentWord = DataSource.words.random()
                    selectedAnswer = null
                    isCorrect = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Next Question ➡\uFE0F")
            }
        }
    }
}