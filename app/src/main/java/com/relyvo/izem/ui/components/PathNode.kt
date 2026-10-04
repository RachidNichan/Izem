package com.relyvo.izem.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relyvo.izem.model.curriculum.Lesson
import com.relyvo.izem.model.curriculum.LessonStatus

@Composable
fun PathNode(
    lesson: Lesson,
    status: LessonStatus,
    stars: Int,
    accentColor: Color,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (status == LessonStatus.AVAILABLE) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Floating "START" / "ابدأ" banner on active node
        if (status == LessonStatus.AVAILABLE) {
            Surface(
                color = accentColor,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .scale(pulseScale)
            ) {
                Text(
                    text = if (isArabic) "ابدأ" else "START",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        // Circular Node
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .scale(if (status == LessonStatus.AVAILABLE) pulseScale else 1f)
                .shadow(
                    elevation = if (status != LessonStatus.LOCKED) 8.dp else 2.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(
                    when (status) {
                        LessonStatus.COMPLETED -> Brush.verticalGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.85f))
                        )
                        LessonStatus.AVAILABLE -> Brush.verticalGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.9f))
                        )
                        LessonStatus.LOCKED -> Brush.verticalGradient(
                            listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                        )
                    }
                )
                .border(
                    width = 4.dp,
                    color = when (status) {
                        LessonStatus.COMPLETED -> Color(0xFFFFD54F) // Gold rim
                        LessonStatus.AVAILABLE -> Color.White.copy(alpha = 0.9f)
                        LessonStatus.LOCKED -> Color(0xFF9E9E9E)
                    },
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        ) {
            when (status) {
                LessonStatus.COMPLETED -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                LessonStatus.AVAILABLE -> {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Available",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
                LessonStatus.LOCKED -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Star rating badge below node (if completed)
        if (status == LessonStatus.COMPLETED) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                repeat(3) { starIndex ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (starIndex < stars) Color(0xFFFFB300) else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Lesson Title
        Text(
            text = if (isArabic) lesson.titleAr else lesson.titleEn,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (status == LessonStatus.LOCKED) Color.Gray else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
