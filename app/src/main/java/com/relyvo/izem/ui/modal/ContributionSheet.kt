package com.relyvo.izem.ui.modal

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.relyvo.izem.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relyvo.izem.model.Suggestion
import com.relyvo.izem.model.Word
import com.relyvo.izem.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionSheet(
    isArabic: Boolean,
    categoryId: String,
    existingWord: Word? = null,
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    val context = LocalContext.current

    var en by remember { mutableStateOf(existingWord?.english ?: "") }
    var ar by remember { mutableStateOf(existingWord?.arabic ?: "") }
    var tmz by remember { mutableStateOf(existingWord?.tamazight ?: "") }
    var tif by remember { mutableStateOf(existingWord?.tifinagh ?: "") }
    var dialect by remember { mutableStateOf("Standard") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { audioUri = it }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (existingWord != null)
                stringResource(R.string.contribution_correct_word)
            else stringResource(R.string.contribution_add_word),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = tif,
            onValueChange = { tif = it },
            label = {
                Text(stringResource(R.string.contribution_tifinagh))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = tmz,
            onValueChange = { tmz = it },
            label = {
                Text(stringResource(R.string.contribution_tamazight))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = ar,
            onValueChange = { ar = it },
            label = { Text(stringResource(R.string.contribution_arabic_trans)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = en,
            onValueChange = { en = it },
            label = { Text(stringResource(R.string.contribution_english_trans)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { imageLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(imageUri != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary
                )
            ) {
                val imageLabel = stringResource(R.string.contribution_photo)
                Text(
                    text = if(imageUri != null) "✅ $imageLabel" else "📷 $imageLabel",
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = { audioLauncher.launch("audio/*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(audioUri != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary
                )
            ) {
                val audioLabel = stringResource(R.string.contribution_voice)
                Text(
                    text = if(audioUri != null) "✅ $audioLabel" else "🎤 $audioLabel",
                    fontSize = 14.sp
                )
            }

        }

        Spacer(modifier = Modifier.height(32.dp))

        val isAudioValid = audioUri != null

        if (!isAudioValid) {
            Text(
                text = stringResource(R.string.contribution_audio_required),
                color = Color.Red,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                isUploading = true
                val suggestion = Suggestion(
                    type = if (existingWord != null) "CORRECTION" else "NEW",
                    wordId = existingWord?.id ?: "",
                    categoryId = categoryId,
                    dialect = dialect,
                    english = en,
                    arabic = ar,
                    tamazight = tmz,
                    tifinagh = tif,
                    imageUrl = existingWord?.imageUrl ?: "",
                    audioUrl = ""
                )
                viewModel.submitSuggestion(
                    suggestion = suggestion,
                    imageUri = imageUri,
                    audioUri = audioUri,
                    onSuccess = {
                        isUploading = false
                        Toast.makeText(context, context.getString(R.string.contribution_success), Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    onError = { error ->
                        isUploading = false
                        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                )
            },
            enabled = !isUploading && tif.isNotEmpty() && isAudioValid,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(stringResource(R.string.contribution_submit), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}