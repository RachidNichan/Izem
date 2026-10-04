package com.relyvo.izem.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    var searchQuery by remember { mutableStateOf("") }

    val filteredCategories = remember(categoriesList, searchQuery) {
        if (searchQuery.isBlank()) {
            categoriesList
        } else {
            categoriesList.filter {
                it.titleEn.contains(searchQuery, ignoreCase = true) ||
                        it.titleAr.contains(searchQuery, ignoreCase = true) ||
                        it.id.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF12141A),
                Color(0xFF1A1D26)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF7F9FC),
                Color(0xFFEEF2F6)
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Search Bar (Full Width Span)
                item(span = { GridItemSpan(2) }) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (isArabic) "ابحث في الأقسام..." else "Search categories...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = if (isDark) Color.LightGray else IzemBlue
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IzemBlue,
                            unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f),
                            focusedContainerColor = if (isDark) Color(0xFF1E212B) else Color.White,
                            unfocusedContainerColor = if (isDark) Color(0xFF1E212B) else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                // 2. Dictionary Mascot Welcome Card (Full Width Span)
                item(span = { GridItemSpan(2) }) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF1F2533) else Color(0xFFE8F1FC)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFD0E2FB)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDark) Color(0xFF2B3347) else Color.White,
                                modifier = Modifier.size(52.dp),
                                shadowElevation = 2.dp
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.izem_mascot),
                                    contentDescription = "Izem Mascot",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "قاموس تمازيغت المصوّر" else "Tamazight Illustrated Dictionary",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isDark) Color.White else Color(0xFF0D47A1)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isArabic) "تصفح المفردات واستمع للنطق الأصلي في أي وقت." else "Browse vocabulary and listen to native audio pronunciations.",
                                    fontSize = 12.sp,
                                    color = if (isDark) Color.LightGray else Color(0xFF283593),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // 3. Category Grid Cards
                items(
                    items = filteredCategories,
                    key = { it.id }
                ) { category ->
                    CategoryGridCard(
                        category = category,
                        isArabic = isArabic,
                        onClick = { onCategoryClick(category.id) }
                    )
                }

                // 4. Empty State if search finds nothing
                if (filteredCategories.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isArabic) "لم يتم العثور على أقسام مطابقة" else "No matching categories found",
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryGridCard(
    category: Category,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val accentColor = remember(category.id) {
        getCategoryAccentColor(category.id)
    }

    val fallbackIcon = remember(category.id) {
        getCategoryFallbackIcon(category.id)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E222D) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Container
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = if (isDark) 0.18f else 0.12f),
                modifier = Modifier.size(62.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (category.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(category.iconUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            error = rememberVectorPainter(fallbackIcon),
                            modifier = Modifier.size(38.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = fallbackIcon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            // Titles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val primaryTitle = if (isArabic) category.titleAr.ifEmpty { category.titleEn } else category.titleEn.ifEmpty { category.titleAr }
                val secondaryTitle = if (isArabic) category.titleEn else category.titleAr

                Text(
                    text = primaryTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1A2138),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                if (secondaryTitle.isNotBlank() && secondaryTitle != primaryTitle) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = secondaryTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom action pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = if (isDark) 0.15f else 0.10f),
                modifier = Modifier.height(26.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = accentColor
                    )
                    Text(
                        text = if (isArabic) "تصفح" else "Explore",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }
    }
}

private fun getCategoryAccentColor(categoryId: String): Color {
    return when (categoryId.lowercase()) {
        "alphabet" -> Color(0xFF1E88E5) // Blue
        "animals" -> Color(0xFF8D6E63)  // Brown
        "clothes" -> Color(0xFF546E7A)  // Slate
        "colors" -> Color(0xFF8E24AA)   // Purple
        "family" -> Color(0xFFD81B60)   // Rose Pink
        "food" -> Color(0xFFF4511E)     // Warm Orange
        "greetings" -> Color(0xFF2E7D32) // Forest Green
        "months" -> Color(0xFF5E35B1)   // Violet
        "nature" -> Color(0xFF00897B)   // Teal / Cyan
        "numbers" -> Color(0xFFFB8C00)  // Amber
        "pronouns" -> Color(0xFF00ACC1) // Aqua Cyan
        "week" -> Color(0xFF039BE5)     // Sky Blue
        else -> IzemBlue
    }
}

private fun getCategoryFallbackIcon(categoryId: String): ImageVector {
    return when (categoryId.lowercase()) {
        "alphabet" -> Icons.Default.Translate
        "animals" -> Icons.Default.Pets
        "clothes" -> Icons.Default.Checkroom
        "colors" -> Icons.Default.Palette
        "family" -> Icons.Default.People
        "food" -> Icons.Default.Restaurant
        "greetings" -> Icons.Default.WavingHand
        "months" -> Icons.Default.CalendarMonth
        "nature" -> Icons.Default.Terrain
        "numbers" -> Icons.Default.Pin
        "pronouns" -> Icons.Default.RecordVoiceOver
        "week" -> Icons.Default.DateRange
        else -> Icons.Default.Category
    }
}

@Composable
fun CategoryHeader(
    onLanguageToggle: () -> Unit,
    onFeedbackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color(0xFF1A237E)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
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
    } catch (_: Exception) {
    }
}

fun sendFeedback(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:izem@relyvo.com?subject=${Uri.encode(context.getString(R.string.email_subject))}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
