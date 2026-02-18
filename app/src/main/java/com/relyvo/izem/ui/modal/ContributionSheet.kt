package com.relyvo.izem.ui.modal

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.relyvo.izem.model.Suggestion
import com.relyvo.izem.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionSheet(
    isArabic: Boolean,
    categoryId: String,
    onDismiss: () -> Unit,
    viewModel: AppViewModel
) {
    val context = LocalContext.current

    var en by remember { mutableStateOf("") }
    var ar by remember { mutableStateOf("") }
    var tmz by remember { mutableStateOf("") }
    var tif by remember { mutableStateOf("") }
    var dialect by remember { mutableStateOf("Standard (IRCAM)") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> audioUri = uri }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if(isArabic) "ساهم في إغناء إيزم 🦁" else "Contribute to Izem 🦁",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = tif,
            onValueChange = { tif = it },
            label = { Text("Tifinagh") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = tmz,
            onValueChange = { tmz = it },
            label = { Text("Tamazight (Latin)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = ar,
            onValueChange = { ar = it },
            label = { Text("Arabic") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = en,
            onValueChange = { en = it },
            label = { Text("English") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if(isArabic) "اختر اللهجة / المنطقة" else "Select Dialect",
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
            listOf("Standard", "Souss", "Atlas", "Rif").forEach { d ->
                FilterChip(
                    selected = dialect == d,
                    onClick = { dialect = d },
                    label = { Text(d) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { imageLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(imageUri != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(if(imageUri != null) "✅ Image" else "📷 Photo")
            }
            Button(
                onClick = { audioLauncher.launch("audio/*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(audioUri != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(if(audioUri != null) "✅ Audio" else "🎤 Voice")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                isUploading = true
                val suggestion = Suggestion(
                    type = "NEW",
                    categoryId = categoryId,
                    dialect = dialect,
                    english = en,
                    arabic = ar,
                    tamazight = tmz,
                    tifinagh = tif
                )
                viewModel.submitSuggestion(
                    suggestion = suggestion,
                    imageUri = imageUri,
                    audioUri = audioUri,
                    onSuccess = {
                        isUploading = false
                        Toast.makeText(context, "Thank you for your contribution! 🦁", Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    onError = { error ->
                        isUploading = false
                        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                )
            },
            enabled = !isUploading && tif.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(if(isArabic) "إرسال للمراجعة" else "Submit for Review")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}