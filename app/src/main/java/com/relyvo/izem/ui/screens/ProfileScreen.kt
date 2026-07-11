package com.relyvo.izem.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.relyvo.izem.viewmodel.AppViewModel
import com.relyvo.izem.ui.theme.IzemGold
import com.relyvo.izem.ui.modal.AuthBottomSheet
import com.relyvo.izem.ui.modal.ProfileAuthCTA
import com.relyvo.izem.R
import com.relyvo.izem.ui.modal.AvatarSelectionSheet
import com.relyvo.izem.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isArabic: Boolean,
    viewModel: AppViewModel = hiltViewModel(),
    onLeaderboardClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isAnonymous by viewModel.isUserAnonymous.collectAsStateWithLifecycle()
    var showAuthSheet by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showAvatarSheet by remember { mutableStateOf(false) }
    var tempName by remember(userProfile.displayName) { mutableStateOf(userProfile.displayName) }

    val gso = remember {
        com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("360506682366-4tftl6brl4n5g8bgcbrdcnv2qdemkra6.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso) }

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                account.idToken?.let { token ->
                    if (isLoginMode) {
                        viewModel.signInWithGoogle(token,
                            onSuccess = {
                                showAuthSheet = false
                                Toast.makeText(context, R.string.auth_welcome_back, Toast.LENGTH_SHORT).show()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                        )
                    } else {
                        viewModel.linkWithGoogle(token,
                            onSuccess = {
                                showAuthSheet = false
                                Toast.makeText(context, R.string.auth_progress_saved, Toast.LENGTH_SHORT).show()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("IzemAuth", "Google Sign-In failed", e)
            }
        }
    }

    if (showAuthSheet) {
        AuthBottomSheet(
            isArabic = isArabic,
            onDismiss = { showAuthSheet = false },
            onGoogleClick = { loginMode ->
                isLoginMode = loginMode
                authLauncher.launch(googleSignInClient.signInIntent)
            },
            onEmailSubmit = { email, pass, loginMode ->
                if (loginMode) {
                    viewModel.signInWithEmail(
                        email = email,
                        pass = pass,
                        onSuccess = {
                            showAuthSheet = false
                            Toast.makeText(context, R.string.auth_welcome_back, Toast.LENGTH_SHORT).show()
                        },
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_LONG).show() }
                    )
                } else {
                    viewModel.linkWithEmail(
                        email = email,
                        pass = pass,
                        onSuccess = {
                            showAuthSheet = false
                            Toast.makeText(context, R.string.auth_account_linked, Toast.LENGTH_SHORT).show()
                        },
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_LONG).show() }
                    )
                }
            }
        )
    }

    if (showAvatarSheet) {
        AvatarSelectionSheet(
            currentAvatarId = userProfile.avatarId,
            isArabic = isArabic,
            onDismiss = { showAvatarSheet = false },
            onAvatarSelected = { selectedId ->
                viewModel.updateAvatarId(selectedId) { success ->
                    if (success) {
                        showAvatarSheet = false
                        Toast.makeText(context, if (isArabic) "تم تحديث الرمز التعبيري بنجاح!" else "Avatar updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        AnimatedVisibility(visible = isAnonymous) {
            ProfileAuthCTA(onOpenAuth = { showAuthSheet = true })
        }

        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                title = { Text(text = if (isArabic) "تعديل الاسم" else "Edit Name") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { if (it.length <= 15) tempName = it },
                            label = { Text(text = if (isArabic) "الاسم المستعار" else "Nickname") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                viewModel.updateDisplayName(tempName.trim()) { success ->
                                    if (success) {
                                        showEditNameDialog = false
                                        Toast.makeText(context, if (isArabic) "تم تحديث الاسم بنجاح" else "Name updated successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error updating name", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IzemGold)
                    ) {
                        Text(text = if (isArabic) "حفظ" else "Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text(text = if (isArabic) "إلغاء" else "Cancel", color = Color.Gray)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- Avatar Box with Floating Edit Badge (Unclipped) ---
        Box(
            modifier = Modifier.size(150.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(20.dp, CircleShape, ambientColor = IzemGold, spotColor = IzemGold)
                    .background(
                        brush = Brush.linearGradient(colors = listOf(IzemGold, IzemGold.copy(alpha = 0.5f))),
                        shape = CircleShape
                    )
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .clickable { showAvatarSheet = true }
            ) {
                val avatarRes = Utils.getAvatarResource(userProfile.avatarId)
                if (avatarRes != 0) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(12.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val firstChar = if (userProfile.displayName.isNotEmpty()) userProfile.displayName.take(1) else "I"
                        Text(
                            text = firstChar.uppercase(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(36.dp)
                    .background(IzemGold, CircleShape)
                    .border(2.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clickable { showAvatarSheet = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(
                text = userProfile.displayName.ifEmpty { "Izem" },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-1).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { showEditNameDialog = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Name",
                    tint = IzemGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Surface(
            color = IzemGold.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 8.dp),
            border = BorderStroke(1.dp, IzemGold.copy(alpha = 0.2f))
        ) {
            Text(
                text = getLocalizedLevel(userProfile.currentLevel),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = IzemGold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Statistics Card ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(32.dp), spotColor = IzemGold.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, IzemGold.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = stringResource(R.string.profile_total_xp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(text = "${userProfile.totalXP} XP", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = IzemGold)
                    }
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(52.dp), tint = IzemGold)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatMiniBox(label = stringResource(R.string.profile_words_found), value = userProfile.learnedWords.size.toString(), color = MaterialTheme.colorScheme.primary)
                    StatMiniBox(label = stringResource(R.string.profile_learning_days), value = userProfile.learningDays.toString(), color = Color(0xFF4CAF50))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLeaderboardClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = IzemGold.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = IzemGold,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isArabic) "عرض لوحة المتصدرين" else "View Leaderboard",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.profile_motivation),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isAnonymous) {
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = { viewModel.logout { } }) {
                Text(stringResource(R.string.profile_logout), color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatMiniBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
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