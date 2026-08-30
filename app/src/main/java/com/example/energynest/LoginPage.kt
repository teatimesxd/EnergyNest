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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.energynest.ui.theme.EnergyNestTheme


@Composable
fun LoginPage() {

    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginMessage by remember { mutableStateOf("") }

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

        // Logo
        Image(
            painter = painterResource(id = R.drawable.energynest_icon_1),
            contentDescription = "App logo",
            modifier = Modifier.size(150.dp)
        )

        // App Name
        Text(
            text = "EnergyNest",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        // Subtitle
        Text(
            text = "Your Smart Portal to Clean & Affordable\nEnergy in Malaysia",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Email / TNB Label
        Text(
            text = "Email Address / TNB Account Number",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF505F76),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 8.dp)
        )

        // Email / TNB TextField
        InputRow(
            label = "Enter your email or TNB #",
            value = account,
            onValueChange = {
                account = it
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Password Label + Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF505F76)
            )

            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = Color(0xFF006C49),
                modifier = Modifier.clickable {
                    // Handle Forgot Password
                }
            )
        }

        // Password TextField
        PasswordInputRow(
            value = password,
            onValueChange = {
                password = it
            },
            visible = passwordVisible,
            onVisibilityToggle = {
                passwordVisible = !passwordVisible
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Login Button
        Button(
            onClick = {
                loginMessage = "Login successful"
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
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

                Text(
                    text = "Login",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Login Success Message
        if (loginMessage.isNotEmpty()) {

            Text(
                text = loginMessage,
                color = Color(0xFF10B981),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // OR Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.LightGray,
                thickness = 1.dp
            )

            Text(
                text = "or",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.LightGray,
                thickness = 1.dp
            )
        }

        // Link with TNB Account Button
        OutlinedButton(
            onClick = {
                // Handle Link with TNB Account
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            shape = RoundedCornerShape(12.dp),

            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF10B981)
            )
        ) {

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.lightning_icon
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Link with TNB Account",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Sign Up Link
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
                color = Color(0xFF10B981),
                modifier = Modifier.clickable {
                    // Handle Sign Up
                }
            )
        }
    }
}


/*
 * Email / TNB Account TextField
 */
@Composable
fun InputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp)
) {

    OutlinedTextField(
        value = value,

        onValueChange = {
            onValueChange(it)
        },

        placeholder = {
            Text(
                text = label,
                fontSize = 20.sp,
                color = Color(0xFF6B7280)
            )
        },

        singleLine = true,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),

        shape = shape,

        colors = OutlinedTextFieldDefaults.colors(

            // White background
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            // Light gray border
            focusedBorderColor = Color(0xFFD1D5DB),
            unfocusedBorderColor = Color(0xFFD1D5DB),

            // Cursor color
            cursorColor = Color(0xFF10B981)
        ),

        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    )
}


/*
 * Password TextField
 */
@Composable
fun PasswordInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp)
) {

    OutlinedTextField(
        value = value,

        onValueChange = {
            onValueChange(it)
        },

        placeholder = {
            Text(
                text = "Enter your password",
                fontSize = 20.sp,
                color = Color(0xFF6B7280)
            )
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),

        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },

        trailingIcon = {

            IconButton(
                onClick = onVisibilityToggle
            ) {

                Icon(
                    painter = painterResource(
                        id = if (visible) {
                            R.drawable.visibility
                        } else {
                            R.drawable.non_visibility
                        }
                    ),

                    contentDescription = if (visible) {
                        "Hide password"
                    } else {
                        "Show password"
                    }
                )
            }
        },

        singleLine = true,

        shape = shape,

        colors = OutlinedTextFieldDefaults.colors(

            // White background
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            // Light gray border
            focusedBorderColor = Color(0xFFD1D5DB),
            unfocusedBorderColor = Color(0xFFD1D5DB),

            // Cursor color
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