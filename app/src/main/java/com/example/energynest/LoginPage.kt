package com.example.energynest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Check Email Format
fun isValidLoginEmail(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    return email.matches(emailPattern.toRegex())
}

@Composable
fun LoginPage(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoginError by remember { mutableStateOf(false) }

    var accountError by remember { mutableStateOf(false) }
    var accountErrorMessage by remember { mutableStateOf("") }

    var passwordError by remember { mutableStateOf(false) }
    var passwordErrorMessage by remember { mutableStateOf("") }

    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val errorRed = Color(0xFFEF4444)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
            .padding(20.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.energynest_icon_1),
            contentDescription = "EnergyNest logo",
            modifier = Modifier.size(150.dp)
        )

        Text(
            text = "EnergyNest",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textDark
        )

        Text(
            text = "Your Smart Portal to Clean & Affordable\nEnergy in Malaysia",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Email Address",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textGray,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 8.dp)
        )

        InputRow(
            label = "Enter your email address",
            value = account,
            onValueChange = {
                account = it
                accountError = false
                accountErrorMessage = ""
                loginMessage = ""
                isLoginError = false
            },
            isError = accountError,
            modifier = Modifier.fillMaxWidth()
        )

        if (accountError) {
            Text(
                text = accountErrorMessage,
                color = errorRed,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textGray
            )

            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = Color(0xFF006C49),
                modifier = Modifier.clickable {
                    onNavigateToForgotPassword()
                }
            )
        }

        PasswordInputRow(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
                passwordErrorMessage = ""
                loginMessage = ""
                isLoginError = false
            },
            visible = passwordVisible,
            onVisibilityToggle = {
                passwordVisible = !passwordVisible
            },
            isError = passwordError,
            modifier = Modifier.fillMaxWidth()
        )

        if (passwordError) {
            Text(
                text = passwordErrorMessage,
                color = errorRed,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                accountError = false
                passwordError = false
                accountErrorMessage = ""
                passwordErrorMessage = ""
                loginMessage = ""
                isLoginError = false

                var valid = true

                // Check Email Empty
                if (account.isBlank()) {
                    accountError = true
                    accountErrorMessage = "Please enter your email address."
                    valid = false
                }
                // Check Email Format
                else if (!isValidLoginEmail(account)) {
                    accountError = true
                    accountErrorMessage = "Please enter a valid email address."
                    valid = false
                }

                // Check Password Empty
                if (password.isBlank()) {
                    passwordError = true
                    passwordErrorMessage = "Please enter your password."
                    valid = false
                }
                // Check Password Minimum Length
                else if (password.length < 6) {
                    passwordError = true
                    passwordErrorMessage = "Password must be at least 6 characters."
                    valid = false
                }

                // Login Successful
                if (valid) {
                    coroutineScope.launch {
                        try {
                            isLoading = true
                            loginMessage = "Checking credentials..."
                            
                            val cleanEmail = account.trim()
                            val cleanPassword = password.trim()

                            // 1. Query the User table for a match
                            val result = withContext(Dispatchers.IO) {
                                SupabaseClient.client.from("User")
                                    .select {
                                        filter {
                                            eq("email", cleanEmail)
                                            eq("password", cleanPassword)
                                        }
                                    }
                                    .decodeSingleOrNull<User>()
                            }

                            if (result != null) {
                                UserSession.user = result
                                loginMessage = "Login successful!"
                                isLoginError = false
                                onLoginSuccess()
                            } else {
                                loginMessage = "Invalid email or password."
                                isLoginError = true
                            }
                        } catch (e: Exception) {
                            loginMessage = "Login failed: ${e.message}"
                            isLoginError = true
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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.arrow_icon),
                        contentDescription = "Login",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
        }

        if (loginMessage.isNotEmpty()) {
            Text(
                text = loginMessage,
                color = if (isLoginError) errorRed else primaryGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "Sign Up",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen,
                modifier = Modifier.clickable {
                    onNavigateToRegister()
                }
            )
        }
    }
}

@Composable
fun InputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = label,
                fontSize = 20.sp,
                color = Color(0xFF6B7280)
            )
        },
        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
            color = Color.Black
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        shape = shape,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            errorTextColor = Color.Black,
            disabledTextColor = Color.Black,
            focusedPlaceholderColor = Color(0xFF6B7280),
            unfocusedPlaceholderColor = Color(0xFF6B7280),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            focusedBorderColor = if (isError) Color(0xFFEF4444) else Color(0xFF10B981),
            unfocusedBorderColor = if (isError) Color(0xFFEF4444) else Color(0xFFD1D5DB),
            errorBorderColor = Color(0xFFEF4444),
            cursorColor = Color(0xFF10B981)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    )
}

@Composable
fun PasswordInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityToggle: () -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Enter your password",
                fontSize = 20.sp,
                color = Color(0xFF6B7280)
            )
        },
        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
            color = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    painter = painterResource(
                        id = if (visible) R.drawable.visibility else R.drawable.non_visibility
                    ),
                    contentDescription = if (visible) "Hide password" else "Show password",
                    tint = Color(0xFF505F76),
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        singleLine = true,
        shape = shape,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            errorTextColor = Color.Black,
            disabledTextColor = Color.Black,
            focusedPlaceholderColor = Color(0xFF6B7280),
            unfocusedPlaceholderColor = Color(0xFF6B7280),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            focusedBorderColor = if (isError) Color(0xFFEF4444) else Color(0xFF10B981),
            unfocusedBorderColor = if (isError) Color(0xFFEF4444) else Color(0xFFD1D5DB),
            errorBorderColor = Color(0xFFEF4444),
            cursorColor = Color(0xFF10B981)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EnergyNestTheme {
        LoginPage()
    }
}