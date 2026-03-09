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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                (if(isArabic) "تصحيح الكلمة ✏️" else "Correct Word ✏️")
            else (if(isArabic) "إضافة كلمة جديدة 🦁" else "Add New Word 🦁"),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = tif,
            onValueChange = { tif = it },
            label = {
                Text(if (isArabic) "كتابة تيفيناغ" else "Tifinagh Script")
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
                Text(if (isArabic) "تمازيغت (باللاتينية)" else "Tamazight (Latin)")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = ar,
            onValueChange = { ar = it },
            label = { Text(if(isArabic) "الترجمة العربية" else "Arabic Translation") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = en,
            onValueChange = { en = it },
            label = { Text(if(isArabic) "الترجمة الإنجليزية" else "English Translation") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if(isArabic) "المنطقة / التنوع اللغوي" else "Variety / Region",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val dialects = listOf(
                "Standard",  // (IRCAM)
                "Souss",     // (Tachelhit)
                "Atlas",     // (Tamaziɣt)
                "Rif",       // (Tarifit)
                "Kabyle",    // (Taqbaylit)
                "Chaoui",    // (Tacawit)
                "Mozabite",  // (Tumzabt)
                "Tuareg",    // (Tamahaq)
                "Tunisian"   // (Chelha)
            )

            dialects.forEach { d ->
                FilterChip(
                    selected = dialect == d,
                    onClick = { dialect = d },
                    label = {
                        Text(
                            text = when(d) {
                                "Standard" -> if(isArabic) "معيارية" else "Standard"
                                "Souss" -> if(isArabic) "سوس" else "Souss"
                                "Atlas" -> if(isArabic) "أطلس" else "Atlas"
                                "Rif" -> if(isArabic) "ريف" else "Rif"
                                "Kabyle" -> if(isArabic) "قبائلية" else "Kabyle"
                                "Chaoui" -> if(isArabic) "شاوية" else "Chaoui"
                                "Mozabite" -> if(isArabic) "ميزابية" else "Mozabite"
                                "Tuareg" -> if(isArabic) "طوارقية" else "Tuareg"
                                "Tunisian" -> if(isArabic) "تونسية (شلحية)" else "Tunisian (Chelha)"
                                else -> d
                            }
                        )
                    }
                )
            }
        }

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
                val imageLabel = if (isArabic) "صورة" else "Photo"
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
                val audioLabel = if (isArabic) "صوت" else "Voice"
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
                text = if (isArabic) "⚠️ يرجى إضافة تسجيل صوتي للكلمة" else "⚠️ Please add an audio recording",
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
                        Toast.makeText(context, if(isArabic) "شكراً لمساهمتك! سيتم مراجعة اقتراحك. 🦁" else "Thank you! Your suggestion is under review. 🦁", Toast.LENGTH_LONG).show()
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
                Text(if(isArabic) "إرسال للمراجعة" else "Submit for Review", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}