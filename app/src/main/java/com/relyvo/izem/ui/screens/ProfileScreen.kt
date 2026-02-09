package com.relyvo.izem.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.relyvo.izem.viewmodel.AppViewModel
import com.relyvo.izem.ui.theme.IzemGold // 🔹 استيراد اللون الذهبي المخصص

@Composable
fun ProfileScreen(
    isArabic: Boolean,
    viewModel: AppViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // --- 🦁 Avatar with Gold Gradient "Halo" ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .shadow(20.dp, CircleShape, ambientColor = IzemGold, spotColor = IzemGold)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            IzemGold,
                            IzemGold.copy(alpha = 0.5f)
                        )
                    ),
                    shape = CircleShape
                )
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(90.dp),
                tint = IzemGold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Rank Title in Gold ---
        Text(
            text = userProfile.currentLevel ?: "Izem Amezwaru",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-1).sp
        )

        // --- Current Rank Badge ---
        Surface(
            color = IzemGold.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 8.dp),
            border = BorderStroke(1.dp, IzemGold.copy(alpha = 0.2f))
        ) {
            Text(
                text = if(isArabic) "رتبتك الحالية" else "YOUR CURRENT RANK",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = IzemGold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- Statistics Card (The Jewel of the UI) ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(32.dp), spotColor = IzemGold.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, IzemGold.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if(isArabic) "مجموع النقاط" else "Total Experience",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${userProfile.totalXP} XP",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = IzemGold
                        )
                    }

                    // Iconic Star in Solid Gold
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = IzemGold
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatMiniBox(
                        label = if(isArabic) "كلمات مكتشفة" else "Words Found",
                        value = "67",
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatMiniBox(
                        label = if(isArabic) "أيام التعلم" else "Learning Days",
                        value = "1",
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Motivational Text with Gold Accent
        Text(
            text = if(isArabic) "استمر في التعلم لتصبح ملك الأسود!" else "Keep learning to become the King of Lions!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun StatMiniBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
    }
}