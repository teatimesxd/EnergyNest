package com.example.energynest

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SupabaseClient
import com.example.energynest.ui.theme.EnergyNestTheme
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
private data class ForgotPasswordEmailRecord(
    val email: String
)

@Composable
fun ForgotPasswordPage(
    onBackToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var currentStep by remember { mutableStateOf(0) }

    var userEmail by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    // Validate Email Format
    fun validateEmail(value: String) {
        userEmail = value
        emailError = when {
            value.isBlank() -> "Email address is required"
            !value.contains("@") -> "Please enter a valid email address"
            !value.contains(".") -> "Please enter a valid email address"
            else -> null
        }
    }

    // Validate Password Format
    fun validateNewPassword(value: String) {
        newPassword = value
        newPasswordError = when {
            value.isBlank() -> "New password is required"
            value.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        // Check Confirm Password
        if (confirmPassword.isNotBlank()) {
            confirmPasswordError = if (confirmPassword != newPassword) {
                "Passwords do not match"
            } else {
                null
            }
        }
    }

    // Validate Confirm Password
    fun validateConfirmPassword(value: String) {
        confirmPassword = value
        confirmPasswordError = when {
            value.isBlank() -> "Please confirm your password"
            value != newPassword -> "Passwords do not match"
            else -> null
        }
    }

    fun validatePasswordForm(): Boolean {
        validateNewPassword(newPassword)
        validateConfirmPassword(confirmPassword)
        return newPassword.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                newPasswordError == null &&
                confirmPasswordError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK BUTTON
            IconButton(
                onClick = {
                    if (currentStep == 1) {
                        currentStep = 0
                        newPassword = ""
                        confirmPassword = ""
                        newPasswordError = null
                        confirmPasswordError = null
                    } else {
                        onBackToLogin()
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            // TITLE
            Text(
                text = if (currentStep == 0) "Forgot Password" else "Reset Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // EMPTY SPACE - KEEP TITLE CENTERED
            Box(modifier = Modifier.size(40.dp))
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.04f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "logo_scale"
            )

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.energynest_icon_1),
                contentDescription = "EnergyNest Logo",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "EnergyNest",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00B87C),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Email Verification Part
            if (currentStep == 0) {
                Text(
                    text = "Reset your password",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Please enter your email in the field",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // EMAIL FIELD
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { validateEmail(it) },
                    label = { Text("Email Address") },
                    placeholder = { Text("Enter your email address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(
                                text = emailError!!,
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (emailError != null) Color.Red else Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFD0D0D0),
                        focusedLabelColor = if (emailError != null) Color.Red else Color(0xFF4CAF50),
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF4CAF50),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Verify Email Button
                val isEmailValid = userEmail.isNotBlank() && emailError == null

                Button(
                    onClick = {
                        validateEmail(userEmail)
                        if (userEmail.isBlank()) return@Button
                        if (!userEmail.contains("@") || !userEmail.contains(".")) return@Button

                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val emailToCheck = userEmail.trim().lowercase()

                                // Validate email from supabase
                                val existingUser = SupabaseClient
                                    .client
                                    .from("User")
                                    .select {
                                        filter { eq("email", emailToCheck) }
                                    }
                                    .decodeList<ForgotPasswordEmailRecord>()

                                // If email not found
                                if (existingUser.isEmpty()) {
                                    emailError = "This email is not registered."
                                    Toast.makeText(
                                        context,
                                        "This email is not registered.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isLoading = false
                                    return@launch
                                }

                                emailError = null
                                Toast.makeText(
                                    context,
                                    "Email verified successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                currentStep = 1

                            } catch (e: Exception) {
                                Log.e("FORGOT_PASSWORD", "Email verification error: ${e.message}", e)
                                emailError = "Unable to verify email. Please try again."
                                Toast.makeText(
                                    context,
                                    "Unable to verify email: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isEmailValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEmailValid) Color(0xFF00B87C) else Color(0xFFA8D5B0),
                        disabledContainerColor = Color(0xFFA8D5B0)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Verify Email",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Back
                Text(
                    text = "Back to Login",
                    color = Color(0xFF00B87C),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }

            // Create New Password
            else {
                Text(
                    text = "Create a new password",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { validateNewPassword(it) },
                    label = { Text("New Password") },
                    placeholder = { Text("Enter your new password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = newPasswordError != null,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { newPasswordVisible = !newPasswordVisible },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (newPasswordVisible) "Hide password" else "Show password",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    supportingText = {
                        if (newPasswordError != null) {
                            Text(
                                text = newPasswordError!!,
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                        } else {
                            Text(
                                text = "Minimum 6 characters required",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (newPasswordError != null) Color.Red else Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFD0D0D0),
                        focusedLabelColor = if (newPasswordError != null) Color.Red else Color(0xFF4CAF50),
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF4CAF50),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { validateConfirmPassword(it) },
                    label = { Text("Confirm Password") },
                    placeholder = { Text("Re-enter your new password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = confirmPasswordError != null,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    supportingText = {
                        if (confirmPasswordError != null) {
                            Text(
                                text = confirmPasswordError!!,
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (confirmPasswordError != null) Color.Red else Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFD0D0D0),
                        focusedLabelColor = if (confirmPasswordError != null) Color.Red else Color(0xFF4CAF50),
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF4CAF50),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                val isPasswordFormValid =
                    newPassword.isNotBlank() &&
                            confirmPassword.isNotBlank() &&
                            newPasswordError == null &&
                            confirmPasswordError == null

                // Reset Password Button
                Button(
                    onClick = {
                        if (!validatePasswordForm()) return@Button
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val emailToUpdate = userEmail.trim().lowercase()

                                // Update password in table
                                SupabaseClient
                                    .client
                                    .from("User")
                                    .update(
                                        mapOf("password" to newPassword)
                                    ) {
                                        filter { eq("email", emailToUpdate) }
                                    }

                                Toast.makeText(
                                    context,
                                    "Password reset successfully!",
                                    Toast.LENGTH_LONG
                                ).show()

                                // Back
                                onBackToLogin()

                            } catch (e: Exception) {
                                Log.e("FORGOT_PASSWORD", "Password reset error: ${e.message}", e)
                                Toast.makeText(
                                    context,
                                    "Password reset failed: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isPasswordFormValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPasswordFormValid) Color(0xFF00B87C) else Color(0xFFA8D5B0),
                        disabledContainerColor = Color(0xFFA8D5B0)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Reset Password",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Back
                Text(
                    text = "Back to Login",
                    color = Color(0xFF00B87C),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    EnergyNestTheme {
        ForgotPasswordPage()
    }
}