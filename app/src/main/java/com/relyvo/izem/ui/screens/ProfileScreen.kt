package com.relyvo.izem.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.relyvo.izem.viewmodel.AppViewModel
import com.relyvo.izem.ui.theme.IzemGold
import com.relyvo.izem.R

@Composable
fun ProfileScreen(
    isArabic: Boolean,
    viewModel: AppViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val isAnonymous by viewModel.isUserAnonymous.collectAsState()
    var showEmailDialog by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

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
                            onSuccess = { Toast.makeText(context, if(isArabic) "مرحباً بك مجدداً! 🦁" else "Welcome back! 🦁", Toast.LENGTH_SHORT).show() },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                        )
                    } else {
                        viewModel.linkWithGoogle(token,
                            onSuccess = { Toast.makeText(context, if(isArabic) "تم حفظ تقدمك بنجاح! 🎉" else "Progress saved successfully! 🎉", Toast.LENGTH_SHORT).show() },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("IzemAuth", "Google Sign-In failed", e)
            }
        }
    }

    if (showEmailDialog) {
        EmailLinkDialog(
            isArabic = isArabic,
            isLoginMode = isLoginMode,
            onDismiss = { showEmailDialog = false },
            onConfirm = { email, pass ->
                if (isLoginMode) {
                    viewModel.signInWithEmail(
                        email = email,
                        pass = pass,
                        onSuccess = {
                            showEmailDialog = false
                            Toast.makeText(context, if(isArabic) "مرحباً بك مجدداً! 🦁" else "Welcome back! 🦁", Toast.LENGTH_SHORT).show()
                        },
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_LONG).show() }
                    )
                } else {
                    viewModel.linkWithEmail(
                        email = email,
                        pass = pass,
                        onSuccess = {
                            showEmailDialog = false
                            Toast.makeText(context, if(isArabic) "تم ربط الحساب بنجاح! 🎉" else "Account linked successfully! 🎉", Toast.LENGTH_SHORT).show()
                        },
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_LONG).show() }
                    )
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

        if (isAnonymous) {
            AccountUpgradeCard(
                isArabic = isArabic,
                isLoginMode = isLoginMode,
                onGoogleClick = { authLauncher.launch(googleSignInClient.signInIntent) },
                onEmailClick = { showEmailDialog = true },
                onLoginInstead = { isLoginMode = !isLoginMode }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- 🦁 Avatar ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .shadow(20.dp, CircleShape, ambientColor = IzemGold, spotColor = IzemGold)
                .background(
                    brush = Brush.linearGradient(colors = listOf(IzemGold, IzemGold.copy(alpha = 0.5f))),
                    shape = CircleShape
                )
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(90.dp), tint = IzemGold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = userProfile.currentLevel ?: "Izem Amezwaru",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-1).sp
        )

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
                        Text(text = if(isArabic) "مجموع النقاط" else "Total Experience", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(text = "${userProfile.totalXP} XP", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = IzemGold)
                    }
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(52.dp), tint = IzemGold)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatMiniBox(label = if(isArabic) "كلمات مكتشفة" else "Words Found", value = userProfile.learnedWords.size.toString(), color = MaterialTheme.colorScheme.primary)
                    StatMiniBox(label = if(isArabic) "أيام التعلم" else "Learning Days", value = userProfile.learningDays.toString(), color = Color(0xFF4CAF50))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if(isArabic) "استمر في التعلم لتصبح ملك الأسود!" else "Keep learning to become the King of Lions!",
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
                Text(if(isArabic) "تسجيل الخروج" else "Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
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
fun AccountUpgradeCard(
    isArabic: Boolean,
    isLoginMode: Boolean,
    onGoogleClick: () -> Unit,
    onEmailClick: () -> Unit,
    onLoginInstead: () -> Unit // 🔹 New parameter
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, IzemGold.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AccountCircle, null, tint = IzemGold, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if(isLoginMode) (if(isArabic) "مرحباً بك" else "Welcome Back") else (if(isArabic) "احفظ تقدمك" else "Save Your Progress"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )

            Text(
                text = if(isArabic) "اربط حسابك لضمان عدم ضياع نقاطك ومستواك." else "Link your account to keep your XP and Ranks forever.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onGoogleClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF757575)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (isArabic) "متابعة باستخدام Google" else "Continue with Google",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onEmailClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Text(
                    text = if (isLoginMode) {
                        if (isArabic) "تسجيل الدخول بالإيميل" else "Sign in with Email"
                    } else {
                        if (isArabic) "حفظ التقدم بالإيميل" else "Sign up with Email"
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(onClick = onLoginInstead) {
                Text(
                    text = if(isLoginMode) (if(isArabic) "ليس لديك حساب؟" else "New here? Save progress") else (if(isArabic) "لديك حساب؟ سجل دخولك" else "Already have an account? Sign in"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }
    }
}

@Composable
fun EmailLinkDialog(isArabic: Boolean, isLoginMode: Boolean, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if(isLoginMode) (if(isArabic) "تسجيل الدخول" else "Sign In") else (if(isArabic) "حفظ التقدم عبر البريد" else "Save progress via Email"), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if(isArabic) "البريد الإلكتروني" else "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if(isArabic) "كلمة السر (6 أحرف +)" else "Password (6+ chars)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(email, password) }) {
                Text(if(isLoginMode) (if(isArabic) "تأكيد" else "Confirm") else (if(isArabic) "حفظ الحساب" else "Save Account"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(if(isArabic) "إلغاء" else "Cancel") }
        }
    )
}