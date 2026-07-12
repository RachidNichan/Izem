package com.relyvo.izem.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.relyvo.izem.R
import com.relyvo.izem.model.Category
import com.relyvo.izem.ui.theme.IzemBlue
import com.relyvo.izem.ui.theme.LearnContainer

@Composable
fun CategoryScreen(
    categoriesList: List<Category>,
    isArabic: Boolean,
    onCategoryClick: (String) -> Unit,
    onLanguageToggle: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF121212),
                Color(0xFF1E1E1E)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                LearnContainer,
                Color.White
            )
        )
    }

    Scaffold(
        topBar = {
            CategoryHeader(
                onLanguageToggle = onLanguageToggle,
                onFeedbackClick = { sendFeedback(context) },
                onShareClick = { shareApp(context) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(brush = backgroundBrush)
        ) {
            // Decorative elements: Trees/Plants
            Icon(
                imageVector = Icons.Default.Nature,
                contentDescription = null,
                tint = if (isDark) Color.White.copy(alpha = 0.05f) else IzemBlue.copy(alpha = 0.05f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(120.dp)
                    .offset(x = (-20).dp, y = 20.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 32.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(categoriesList) { index, category ->
                    val offset = when (index % 4) {
                        0 -> 0.dp
                        1 -> 40.dp
                        2 -> 0.dp
                        3 -> (-40).dp
                        else -> 0.dp
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CategoryPathItem(
                            category = category,
                            isArabic = isArabic,
                            modifier = Modifier.offset(x = offset),
                            onClick = { onCategoryClick(category.id) }
                        )
                    }
                }
            }

            // Mascot area at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                MascotSpeechBubble()
            }
        }
    }
}

@Composable
fun MascotSpeechBubble() {
    val isDark = isSystemInDarkTheme()
    Row(verticalAlignment = Alignment.Bottom) {
        Surface(
            shape = CircleShape,
            color = if (isDark) Color(0xFF333333) else Color.White,
            modifier = Modifier.size(80.dp).shadow(4.dp, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.izem_mascot),
                contentDescription = "Izem Mascot",
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(8.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp),
            color = if (isDark) Color(0xFF444444) else Color.White,
            modifier = Modifier
                .padding(start = 4.dp, bottom = 40.dp)
                .shadow(4.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
        ) {
            Text(
                text = stringResource(R.string.mascot_greeting),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun CategoryPathItem(
    category: Category,
    isArabic: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val errorPainter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.List)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(80.dp)
                .shadow(8.dp, CircleShape)
                .clickable { onClick() },
            shape = CircleShape,
            color = if (isDark) Color(0xFF2C2C2C) else Color.White,
            border = BorderStroke(3.dp, if (isDark) IzemBlue.copy(alpha = 0.5f) else Color(0xFFE0E0E0))
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(category.iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    error = errorPainter,
                    modifier = Modifier.size(44.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) IzemBlue.copy(alpha = 0.8f) else IzemBlue,
            modifier = Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
        ) {
            Text(
                text = if (isArabic) category.titleAr else category.titleEn,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryHeader(
    onLanguageToggle: () -> Unit,
    onFeedbackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color(0xFF1A237E) // Deep Blue

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = contentColor,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = stringResource(R.string.learn_tamazight),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = onLanguageToggle,
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (isDark) Color.White.copy(alpha = 0.1f) else IzemBlue.copy(alpha = 0.1f),
                        contentColor = contentColor
                    )
                ) {
                    Text(
                        text = stringResource(R.string.lang_toggle),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onFeedbackClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(R.string.feedback),
                        tint = contentColor
                    )
                }

                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = contentColor
                    )
                }
            }
        }
    }
}

fun shareApp(context: Context) {
    val appPackageName = context.packageName
    val message = context.getString(R.string.share_message, appPackageName)
    
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }
    
    val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_chooser_title)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    
    try {
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        // Fallback for sharing if chooser fails
    }
}

fun sendFeedback(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:izem@relyvo.com?subject=${Uri.encode(context.getString(R.string.email_subject))}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle case where no email app is installed
    }
}
