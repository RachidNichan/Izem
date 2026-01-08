package com.relyvo.izem

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relyvo.izem.data.DataSource
import com.relyvo.izem.model.Category

@Composable
fun CategoryScreen(
    categoriesList: List<Category>,
    isArabic: Boolean,
    onCategoryClick: (String) -> Unit,
    onLanguageToggle: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isArabic) "أزول!" else "Azul!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83E\uDD81",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Row {
                TextButton(onClick = { onLanguageToggle() }) {
                    Text(
                        text = if (isArabic) "EN" else "AR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                IconButton(onClick = { sendFeedback(context) }) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Feedback",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(onClick = { shareApp(context, isArabic) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share App",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categoriesList) { category ->
                CategoryItem(
                    category = category,
                    isArabic = isArabic,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, isArabic: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val iconId = Utils.getDrawableId(context, category.iconName)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (iconId != 0) {
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = category.titleEn,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isArabic) category.titleAr else category.titleEn,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
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
        putExtra(Intent.EXTRA_EMAIL, arrayOf("rachid@relyvo.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Izem App Feedback")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
    }
}