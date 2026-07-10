package com.relyvo.izem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relyvo.izem.R
import com.relyvo.izem.model.UserProfile
import com.relyvo.izem.ui.theme.IzemBlue
import com.relyvo.izem.ui.theme.IzemGold
import com.relyvo.izem.ui.theme.IzemOrange
import com.relyvo.izem.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val users by viewModel.leaderboardUsers.collectAsState()
    val currentUserId = viewModel.currentUserId
    val context = LocalContext.current
    val roarSuccessMessage = stringResource(R.string.leaderboard_roar_success)
    val roarErrorMessage = stringResource(R.string.leaderboard_roar_error)
    val roarWaitFormat = stringResource(R.string.leaderboard_roar_wait)

    val currentUserRankIndex = users.indexOfFirst { it.userId == currentUserId }
    val currentUserProfile = users.find { it.userId == currentUserId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_leaderboard),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = IzemBlue)
                }
            } else {
                val top3 = users.take(3)
                val remainingUsers = users.drop(3)
                val sharedPrefs = remember { context.getSharedPreferences("IzemRoarPrefs", android.content.Context.MODE_PRIVATE) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        if (top3.isNotEmpty()) {
                            PodiumSection(top3 = top3)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    itemsIndexed(remainingUsers) { index, user ->
                        val actualRank = index + 4
                        val isMe = user.userId == currentUserId
                        LeaderboardRow(
                            rank = actualRank,
                            user = user,
                            isMe = isMe,
                            onRoarClick = {
                                val lastRoarTime = sharedPrefs.getLong("last_roar_${user.userId}", 0L)
                                val oneHourInMillis = 60 * 60 * 1000L
                                val timeElapsed = System.currentTimeMillis() - lastRoarTime

                                if (timeElapsed < oneHourInMillis) {
                                    val remainingMinutes = ((oneHourInMillis - timeElapsed) / (1000 * 60)) + 1
                                    val waitMessage = roarWaitFormat.format(remainingMinutes)
                                    Toast.makeText(context, waitMessage, Toast.LENGTH_LONG).show()
                                } else {
                                    viewModel.sendRoarChallenge(user.userId) { success ->
                                        if (success) {
                                            sharedPrefs.edit().putLong("last_roar_${user.userId}", System.currentTimeMillis()).apply()
                                            Toast.makeText(context, roarSuccessMessage, Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, roarErrorMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                if (currentUserProfile != null && currentUserRankIndex != -1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        StickyUserCard(
                            rank = currentUserRankIndex + 1,
                            user = currentUserProfile
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumSection(top3: List<UserProfile>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        if (top3.size > 1) {
            PodiumItem(
                user = top3[1],
                rank = 2,
                color = Color(0xFFC0C0C0), // Silver
                height = 110.dp
            )
        }

        if (top3.isNotEmpty()) {
            PodiumItem(
                user = top3[0],
                rank = 1,
                color = IzemGold, // Gold
                height = 140.dp
            )
        }

        if (top3.size > 2) {
            PodiumItem(
                user = top3[2],
                rank = 3,
                color = Color(0xFFCD7F32), // Bronze
                height = 95.dp
            )
        }
    }
}

@Composable
fun PodiumItem(
    user: UserProfile,
    rank: Int,
    color: Color,
    height: androidx.compose.ui.unit.Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(95.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user.displayName.ifEmpty { "Izem" },
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.profile_xp_suffix, user.totalXP.toString()),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .background(
                    color = color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .border(
                    width = 1.dp,
                    color = color.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "#$rank",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = color,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    user: UserProfile,
    isMe: Boolean,
    onRoarClick: () -> Unit
) {
    val cardColor = if (isMe) IzemBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isMe) IzemBlue else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$rank",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.width(32.dp),
                color = if (isMe) IzemBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val firstChar = if (user.displayName.isNotEmpty()) user.displayName.take(1) else "I"
                Text(
                    text = firstChar.uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMe) stringResource(R.string.profile_you) else user.displayName.ifEmpty { "Izem" },
                    fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    text = "${getLocalizedLevel(user.currentLevel)} • ${stringResource(R.string.profile_active_days, user.learningDays.toString())}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isMe) {
                IconButton(
                    onClick = onRoarClick,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(36.dp)
                        .background(IzemGold.copy(alpha = 0.15f), CircleShape)
                ) {
                    Text(text = "🦁", fontSize = 18.sp)
                }
            }

            Text(
                text = stringResource(R.string.profile_xp_suffix, user.totalXP.toString()),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = IzemOrange
            )
        }
    }
}

@Composable
fun StickyUserCard(
    rank: Int,
    user: UserProfile
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                modifier = Modifier.width(45.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.profile_current_rank),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = user.displayName.ifEmpty { "Izem" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Text(
                text = stringResource(R.string.profile_xp_suffix, user.totalXP.toString()),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun getLocalizedLevel(level: String): String {
    return when (level) {
        "Izem Amezwaru" -> stringResource(R.string.level_azemwaru)
        "Izem Anlmad" -> stringResource(R.string.level_anlmad)
        "Izem Amqran" -> stringResource(R.string.level_amqran)
        "Agellid n Izmawn" -> stringResource(R.string.level_agellid)
        "Izem" -> stringResource(R.string.level_izem)
        else -> level
    }
}
