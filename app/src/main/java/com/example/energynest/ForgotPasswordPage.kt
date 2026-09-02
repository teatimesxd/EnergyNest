package com.example.energynest

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SupabaseClient
import com.example.energynest.ui.theme.EnergyNestTheme
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordPage(
    recoveryVerified: Boolean = false,
    onBackToLogin: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    // Workflow = Enter Email, Check Email, Create New Password
    var currentStep by remember { mutableStateOf(if (recoveryVerified) 2 else 0) }
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

    // Recovery Verify
    LaunchedEffect(recoveryVerified) {
        if (recoveryVerified) {
            currentStep = 2
            passwordError = false
            passwordMessage = ""
            passwordResetSuccess = false
        }
    }

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

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = {
                            userEmail = it
                            step1Error = false
                            step1Message = ""
                        },
                        label = { Text("Email Address") },
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

                    // Send Reset Link
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
                                        SupabaseClient.client.auth.resetPasswordForEmail(
                                            email = userEmail,
                                            redirectUrl = "energynest://reset-password"
                                        )
                                        currentStep = 1
                                    } catch (e: Exception) {
                                        step1Error = true
                                        step1Message = "Unable to send the password reset email. Please try again."
                                        Log.e("RESET_EMAIL", "Supabase error: ${e.message}", e)
                                    } finally {
                                        isLoading = false
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
                        modifier = Modifier.clickable { onBackToLogin() }
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

                    Button(
                        onClick = {
                            step1Error = false
                            step1Message = ""
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    SupabaseClient.client.auth.resetPasswordForEmail(
                                        email = userEmail,
                                        redirectUrl = "energynest://reset-password"
                                    )
                                    step1Error = false
                                    step1Message = "A new password reset link has been sent. Please check your email."
                                } catch (e: Exception) {
                                    step1Error = true
                                    step1Message = "Unable to resend the password reset email."
                                    Log.e("RESEND_EMAIL", "Supabase error: ${e.message}", e)
                                } finally {
                                    isLoading = false
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

                    // NEW PASSWORD
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            passwordError = false
                            passwordMessage = ""
                        },
                        label = { Text("New Password") },
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
                        label = { Text("Confirm Password") },
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

                    if (passwordMessage.isNotEmpty()) {
                        Text(
                            text = passwordMessage,
                            color = if (passwordError) errorRed else primaryGreen,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Reset Password
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
                                passwordMessage = "Your password must contain at least 6 characters."
                            } else if (newPassword != confirmPassword) {
                                passwordError = true
                                passwordMessage = "The passwords do not match."
                            } else {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val session = SupabaseClient.client.auth.currentSessionOrNull()
                                        if (session == null) {
                                            passwordError = true
                                            passwordMessage = "No password recovery session found. Please request a new reset link."
                                            return@launch
                                        }
                                        SupabaseClient.client.auth.updateUser {
                                            password = newPassword
                                        }
                                        passwordError = false
                                        passwordResetSuccess = true
                                        passwordMessage = "Your password has been reset successfully!"
                                    } catch (e: Exception) {
                                        passwordError = true
                                        passwordMessage = "Unable to reset password: ${e.message}"
                                        Log.e("RESET_PASSWORD", "Supabase error: ${e.message}", e)
                                    } finally {
                                        isLoading = false
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

                    // Request for new reset password link
                    if (passwordError) {
                        Button(
                            onClick = {
                                currentStep = 0
                                passwordError = false
                                passwordMessage = ""
                                newPassword = ""
                                confirmPassword = ""
                                passwordResetSuccess = false
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

                    if (passwordResetSuccess) {
                        Button(
                            onClick = { onBackToLogin() },
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
                    else -> {
                        onBackToLogin()
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
    EnergyNestTheme {
        ForgotPasswordPage(recoveryVerified = false)
    }
}