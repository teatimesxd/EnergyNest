package com.example.energynest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energynest.ui.theme.EnergyNestTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.launch

class ForgotPasswordActivity : ComponentActivity() {

    private val recoveryVerified = mutableStateOf(false)

    // Supabase Client
    private val supabaseClient: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://skanmdzsnfoquwljukfk.supabase.co",
            supabaseKey = "sb_publishable_LTLKeWepLBaIi8RW3Fd23w_OVLDbLqZ"
        ) {
            install(Auth) {
                host = "reset-password"
                scheme = "energynest"
            }
            install(Postgrest)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        processRecoveryLink(intent)

        setContent {
            EnergyNestTheme {
                ForgotPasswordPage(
                    recoveryVerified = recoveryVerified.value,
                    supabaseClient = supabaseClient
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processRecoveryLink(intent)
    }

    // Process password recovery link
    private fun processRecoveryLink(intent: Intent?) {
        if (intent == null) return
        val uri = intent.data ?: return

        Log.d("RECOVERY_LINK", "Deep link received: $uri")

        try {
            supabaseClient.handleDeeplinks(intent)
            recoveryVerified.value = true
            Log.d("RECOVERY_LINK", "Recovery link received successfully")
        } catch (e: Exception) {
            recoveryVerified.value = false
            Log.e("RECOVERY_LINK", "Failed to process recovery link", e)
        }
    }
}

@Composable
fun ForgotPasswordPage(
    recoveryVerified: Boolean = false,
    supabaseClient: SupabaseClient
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    // PAGE FLOW (Enter Email, Check Email, Create New Password)
    var currentStep by remember {
        mutableStateOf(
            if (recoveryVerified) {
                2
            } else {
                0
            }
        )
    }

    // Move to password page when recovery link is verified
    LaunchedEffect(recoveryVerified) {
        if (recoveryVerified) {
            currentStep = 2
        }
    }

    var userEmail by remember { mutableStateOf("") }
    var step1Error by remember { mutableStateOf(false) }
    var step1Message by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordResetSuccess by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val bgGray = Color(0xFFE2E8F0)
    val errorRed = Color(0xFFEF4444)
    val borderGray = Color(0xFFD1D5DB)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGray)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 28.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enter Email
                if (currentStep == 0) {
                    Text(
                        text = "Forgot Password?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )

                    Text(
                        text = "Enter your email address and we will send you a password reset link.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Text Field
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = {
                            userEmail = it
                            step1Error = false
                            step1Message = ""
                        },
                        label = { Text(text = "Email Address") },
                        placeholder = {
                            Text(
                                text = "user@example.com",
                                color = Color.Gray
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = step1Error,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = if (step1Error) errorRed else primaryGreen,
                            unfocusedBorderColor = if (step1Error) errorRed else borderGray,
                            errorBorderColor = errorRed,
                            focusedLabelColor = primaryGreen,
                            unfocusedLabelColor = textGray,
                            errorLabelColor = errorRed,
                            cursorColor = primaryGreen
                        )
                    )

                    // Email Error Message
                    if (step1Message.isNotEmpty()) {
                        Text(
                            text = step1Message,
                            color = if (step1Error) errorRed else primaryGreen,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Send Reset Link Button
                    Button(
                        onClick = {
                            if (userEmail.isBlank()) {
                                step1Error = true
                                step1Message = "Please enter your email address."
                            } else if (!userEmail.contains("@") || !userEmail.contains(".")) {
                                step1Error = true
                                step1Message = "Please enter a valid email address."
                            } else {
                                step1Error = false
                                step1Message = ""
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        supabaseClient.auth.resetPasswordForEmail(
                                            email = userEmail,
                                            redirectUrl = "energynest://reset-password"
                                        )
                                        isLoading = false
                                        currentStep = 1
                                    } catch (e: Exception) {
                                        isLoading = false
                                        step1Error = true
                                        val errorMessage = e.message?.lowercase() ?: ""
                                        if (errorMessage.contains("rate limit") ||
                                            errorMessage.contains("too many requests") ||
                                            errorMessage.contains("429")
                                        ) {
                                            step1Message = "Too many password reset emails have been requested. Please wait for one hour before trying again."
                                        } else {
                                            step1Message = "Unable to send the password reset email. Please check your internet connection and try again."
                                        }
                                        Log.e("RESET_EMAIL", "Supabase error: ${e.message}", e)
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Send Reset Link",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_icon),
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    Text(
                        text = "Back to Login",
                        color = primaryGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            (context as? Activity)?.finish()
                        }
                    )
                }

                // Check Email
                else if (currentStep == 1) {
                    Text(
                        text = "Check Your Email",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )

                    Text(
                        text = "We have sent a password reset link to:",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Text(
                        text = userEmail,
                        color = primaryGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Open your email and click the password reset link. The EnergyNest app should open automatically.",
                        textAlign = TextAlign.Center,
                        color = textGray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Resend Link Button
                    Button(
                        onClick = {
                            step1Error = false
                            step1Message = ""
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    supabaseClient.auth.resetPasswordForEmail(
                                        email = userEmail,
                                        redirectUrl = "energynest://reset-password"
                                    )
                                    isLoading = false
                                    step1Error = false
                                    step1Message = "A new password reset link has been sent. Please check your email."
                                } catch (e: Exception) {
                                    isLoading = false
                                    step1Error = true
                                    val errorMessage = e.message?.lowercase() ?: ""
                                    if (errorMessage.contains("rate limit") ||
                                        errorMessage.contains("too many requests") ||
                                        errorMessage.contains("email rate limit") ||
                                        errorMessage.contains("429")
                                    ) {
                                        step1Message = "Too many password reset emails have been requested. Please wait for one hour before trying again."
                                    } else {
                                        step1Message = "Unable to resend the password reset email. Please check your internet connection and try again."
                                    }
                                    Log.e("RESEND_EMAIL", "Supabase error: ${e.message}", e)
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Resend Email")
                        }
                    }

                    if (step1Message.isNotEmpty()) {
                        Text(
                            text = step1Message,
                            color = if (step1Error) errorRed else primaryGreen,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Create New Password
                else if (currentStep == 2) {
                    Text(
                        text = "Create New Password",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )

                    Text(
                        text = "Please enter your new password.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            passwordError = false
                            passwordMessage = ""
                        },
                        label = { Text(text = "New Password") },
                        placeholder = {
                            Text(
                                text = "Enter your new password",
                                color = Color.Gray
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible) R.drawable.visibility else R.drawable.non_visibility
                                    ),
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = passwordError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = if (passwordError) errorRed else primaryGreen,
                            unfocusedBorderColor = if (passwordError) errorRed else borderGray,
                            errorBorderColor = errorRed,
                            focusedLabelColor = primaryGreen,
                            unfocusedLabelColor = textGray,
                            errorLabelColor = errorRed,
                            cursorColor = primaryGreen
                        )
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            passwordError = false
                            passwordMessage = ""
                        },
                        label = { Text(text = "Confirm Password") },
                        placeholder = {
                            Text(
                                text = "Enter your new password again",
                                color = Color.Gray
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (confirmVisible) R.drawable.visibility else R.drawable.non_visibility
                                    ),
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = passwordError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = if (passwordError) errorRed else primaryGreen,
                            unfocusedBorderColor = if (passwordError) errorRed else borderGray,
                            errorBorderColor = errorRed,
                            focusedLabelColor = primaryGreen,
                            unfocusedLabelColor = textGray,
                            errorLabelColor = errorRed,
                            cursorColor = primaryGreen
                        )
                    )

                    // Check the password is empty or not
                    if (passwordMessage.isNotEmpty()) {
                        Text(
                            text = passwordMessage,
                            color = if (passwordError) errorRed else primaryGreen,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Reset Password Button
                    Button(
                        onClick = {
                            passwordError = false
                            passwordMessage = ""

                            if (newPassword.isBlank()) {
                                passwordError = true
                                passwordMessage = "Please enter your new password."
                            } else if (confirmPassword.isBlank()) {
                                passwordError = true
                                passwordMessage = "Please confirm your new password."
                            } else if (newPassword.length < 6) {
                                passwordError = true
                                passwordMessage = "Your new password must contain at least 6 characters."
                            } else if (newPassword != confirmPassword) {
                                passwordError = true
                                passwordMessage = "The passwords do not match. Please make sure both passwords are the same."
                            } else {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        supabaseClient.auth.updateUser {
                                            password = newPassword
                                        }
                                        isLoading = false
                                        passwordError = false
                                        passwordResetSuccess = true
                                        passwordMessage = "Your password has been reset successfully!"
                                    } catch (e: Exception) {
                                        isLoading = false
                                        passwordError = true
                                        passwordMessage = "Unable to reset your password. Your reset link may have expired or is invalid. Please request a new password reset link and try again."
                                        Log.e("RESET_PASSWORD", "Supabase reset password error: ${e.message}", e)
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Reset Password",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Request new reset link
                    if (passwordError && passwordMessage.contains("expired")) {
                        Button(
                            onClick = {
                                currentStep = 0
                                passwordError = false
                                passwordMessage = ""
                                newPassword = ""
                                confirmPassword = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Request New Reset Link")
                        }
                    }

                    // Back to login page after successful
                    if (passwordResetSuccess) {
                        Button(
                            onClick = {
                                (context as? Activity)?.finish()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Back to Login")
                        }
                    }
                }
            }
        }

        // Back Button
        IconButton(
            onClick = {
                when (currentStep) {
                    1 -> {
                        currentStep = 0
                        step1Error = false
                        step1Message = ""
                    }
                    2 -> {
                        (context as? Activity)?.finish()
                    }
                    else -> {
                        (context as? Activity)?.finish()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
                .size(60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = primaryGreen,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    val previewSupabaseClient = remember {
        createSupabaseClient(
            supabaseUrl = "https://skanmdzsnfoquwljukfk.supabase.co",
            supabaseKey = "sb_publishable_LTLKeWepLBaIi8RW3Fd23w_OVLDbLqZ"
        ) {
            install(Auth) {
                host = "reset-password"
                scheme = "energynest"
            }
            install(Postgrest)
        }
    }

    EnergyNestTheme {
        ForgotPasswordPage(
            recoveryVerified = false,
            supabaseClient = previewSupabaseClient
        )
    }
}