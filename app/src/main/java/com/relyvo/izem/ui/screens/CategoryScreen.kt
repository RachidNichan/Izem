package com.relyvo.izem.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.relyvo.izem.model.Category
import com.relyvo.izem.ui.theme.IzemBlue // 🔹 استيراد اللون الأزرق المخصص

@Composable
fun CategoryScreen(
    categoriesList: List<Category>,
    isArabic: Boolean,
    onCategoryClick: (String) -> Unit,
    onLanguageToggle: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CategoryHeader(
                isArabic = isArabic,
                onLanguageToggle = onLanguageToggle,
                onFeedbackClick = { sendFeedback(context) },
                onShareClick = { shareApp(context, isArabic) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categoriesList) { category ->
                    CategoryItemUpgrade(
                        category = category,
                        isArabic = isArabic,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(
    isArabic: Boolean,
    onLanguageToggle: () -> Unit,
    onFeedbackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
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
                    text = if (isArabic) "أزول!" else "Azul!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = IzemBlue,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = if (isArabic) "تعلم تمازيغت" else "Learn Tamazight",
                    style = MaterialTheme.typography.bodySmall,
                    color = IzemBlue.copy(alpha = 0.6f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                FilledTonalIconButton(
                    onClick = onLanguageToggle,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = IzemBlue.copy(alpha = 0.1f),
                        contentColor = IzemBlue
                    )
                ) {
                    Text(
                        text = if (isArabic) "EN" else "AR",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onFeedbackClick) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Feedback",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                IconButton(onClick = onShareClick) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = IzemBlue
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItemUpgrade(category: Category, isArabic: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val errorPainter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.List)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.LightGray
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,

        border = BorderStroke(1.dp, IzemBlue.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                IzemBlue.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.surface
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(category.iconUrl)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = null,
                    error = errorPainter,
                    modifier = Modifier.size(52.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isArabic) category.titleAr else category.titleEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

fun shareApp(context: Context, isArabic: Boolean) {
    val appPackageName = context.packageName
    val message = if (isArabic) {
        "أزول! أنا أتعلم الأمازيغية مع تطبيق إيزم. حمله من هنا:\nhttps://play.google.com/store/apps/details?id=$appPackageName"
    } else {
        "Azul! I'm learning Tamazight with Izem app. Check it out:\nhttps://play.google.com/store/apps/details?id=$appPackageName"
    }
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, if (isArabic) "مشاركة إيزم عبر" else "Share Izem via")
    context.startActivity(shareIntent)
}

fun sendFeedback(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("izem@relyvo.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Izem App Feedback")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle case where no email app is installed
    }
}