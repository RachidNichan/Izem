package com.relyvo.izem.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.relyvo.izem.model.Word
import com.relyvo.izem.ui.theme.IzemBlue
import com.relyvo.izem.ui.modal.ContributionSheet
import com.relyvo.izem.utils.SmartAudioPlayer
import com.relyvo.izem.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordList(
    categoryId: String,
    wordList: List<Word>,
    isArabic: Boolean,
    onWordClick: (String) -> Unit,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }
    var selectedWordForCorrection by remember { mutableStateOf<Word?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedWordForCorrection = null
                    showSheet = true
                },
                containerColor = IzemBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, "Add Suggestion", modifier = Modifier.size(30.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(wordList, key = { it.id }) { currentWord ->
                    WordItemUpgrade(
                        word = currentWord,
                        isArabic = isArabic,
                        onWordClick = onWordClick,
                        onCorrectClick = { word ->
                            selectedWordForCorrection = word
                            showSheet = true
                        }
                    )
                }
            }

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showSheet = false
                        selectedWordForCorrection = null
                    },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    ContributionSheet(
                        isArabic = isArabic,
                        categoryId = categoryId,
                        existingWord = selectedWordForCorrection,
                        onDismiss = { showSheet = false },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun WordItemUpgrade(
    word: Word,
    isArabic: Boolean,
    onWordClick: (String) -> Unit,
    onCorrectClick: (Word) -> Unit
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(28.dp), ambientColor = Color.LightGray)
            .clickable {
                SmartAudioPlayer.playAudio(context, word.audioUrl, word.id)
                onWordClick(word.id)
            },
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, IzemBlue.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { onCorrectClick(word) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Correct",
                    modifier = Modifier.size(18.dp),
                    tint = IzemBlue.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            if (word.imageUrl.isNotEmpty()) {
                Box(modifier = Modifier.size(70.dp).background(brush = Brush.linearGradient(colors = listOf(IzemBlue.copy(alpha = 0.15f), MaterialTheme.colorScheme.surface)), shape = RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    AsyncImage(model = ImageRequest.Builder(context).data(word.imageUrl).crossfade(true).build(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            } else {
                Box(modifier = Modifier.size(70.dp).background(IzemBlue.copy(alpha = 0.1f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    Text(text = word.tifinagh.take(1), style = MaterialTheme.typography.headlineSmall, color = IzemBlue, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(text = word.tifinagh, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.tamazight.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        color = IzemBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = IzemBlue.copy(alpha = 0.6f))
                }
            }

            Surface(color = IzemBlue.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp)) {
                Text(
                    text = if (isArabic) word.arabic else word.english.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = IzemBlue
                )
            }
        }
    }
}