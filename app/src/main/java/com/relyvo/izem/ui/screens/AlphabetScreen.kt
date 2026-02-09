package com.relyvo.izem.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relyvo.izem.model.Word
import com.relyvo.izem.ui.theme.IzemBlue // 🔹 استيراد اللون الأزرق الموحد لقسم التعلم
import com.relyvo.izem.utils.SmartAudioPlayer

@Composable
fun AlphabetScreen(
    letters: List<Word>,
    isArabic: Boolean
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(letters, key = { it.id }) { letter ->
                AlphabetItemUpgrade(
                    letter = letter,
                    isArabic = isArabic,
                    onPlaySound = {
                        SmartAudioPlayer.playAudio(context, letter.audioUrl, letter.id)
                    }
                )
            }
        }
    }
}

@Composable
fun AlphabetItemUpgrade(
    letter: Word,
    isArabic: Boolean,
    onPlaySound: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.LightGray
            )
            .clickable { onPlaySound() },
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, IzemBlue.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                IzemBlue.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.surface
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.tifinagh,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = IzemBlue,
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = IzemBlue.copy(alpha = 0.05f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, IzemBlue.copy(alpha = 0.1f))
            ) {
                Text(
                    text = if (isArabic) letter.arabic else letter.tamazight,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = IzemBlue.copy(alpha = 0.8f)
                )
            }
        }
    }
}