package com.example.energynest

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SupabaseClient
import com.example.energynest.UserSession
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// <-- adjust column name if your table doesn't call it "password"
@Serializable
private data class PasswordCheckRecord(
    val password: String
)

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {},
    onResetSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var oldPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var isResetting by remember { mutableStateOf(false) }

    fun validateOldPassword(value: String) {
        oldPassword = value

        oldPasswordError = when {
            value.isBlank() ->
                "Old password is required"

            value.length < 6 ->
                "Password must be at least 6 characters"

            else ->
                null
        }

        if (newPassword.isNotBlank()) {
            newPasswordError = when {
                newPassword.length < 6 ->
                    "Password must be at least 6 characters"

                newPassword == oldPassword ->
                    "New password cannot be same as old password"

                else ->
                    null
            }
        }
    }

    fun validateNewPassword(value: String) {
        newPassword = value

        newPasswordError = when {
            value.isBlank() ->
                "New password is required"

            value.length < 6 ->
                "Password must be at least 6 characters"

            value == oldPassword ->
                "New password cannot be same as old password"

            else ->
                null
        }

        if (confirmPassword.isNotBlank()) {
            confirmPasswordError = when {
                confirmPassword != newPassword ->
                    "Passwords do not match"

                else ->
                    null
            }
        }
    }

    fun validateConfirmPassword(value: String) {
        confirmPassword = value

        confirmPasswordError = when {
            value.isBlank() ->
                "Please confirm your password"

            value != newPassword ->
                "Passwords do not match"

            else ->
                null
        }
    }

    fun validateAll(): Boolean {

        validateOldPassword(oldPassword)
        validateNewPassword(newPassword)
        validateConfirmPassword(confirmPassword)

        return oldPassword.isNotBlank() &&
                newPassword.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                oldPasswordError == null &&
                newPasswordError == null &&
                confirmPasswordError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Change Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Box(
                modifier = Modifier.size(40.dp)
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // LOGO ANIMATION
            val infiniteTransition =
                rememberInfiniteTransition(
                    label = "logo_pulse"
                )

            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.04f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "logo_scale"
            )

            Icon(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.energynest_icon_1
                ),
                contentDescription = "EnergyNest Logo",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "EnergyNest",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00B87C),
                letterSpacing = 0.5.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Change your password",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // OLD PASSWORD
            OutlinedTextField(
                value = oldPassword,
                onValueChange = {
                    validateOldPassword(it)
                },
                label = {
                    Text("Old Password")
                },
                placeholder = {
                    Text("Enter your old password")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                isError = oldPasswordError != null,
                visualTransformation =
                    if (oldPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {

                    IconButton(
                        onClick = {
                            oldPasswordVisible =
                                !oldPasswordVisible
                        },
                        modifier = Modifier.size(36.dp)
                    ) {

                        Icon(
                            imageVector =
                                if (oldPasswordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                            contentDescription =
                                if (oldPasswordVisible)
                                    "Hide password"
                                else
                                    "Show password",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                supportingText = {

                    if (oldPasswordError != null) {
                        Text(
                            text = oldPasswordError!!,
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }
                },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor =
                            if (oldPasswordError != null)
                                Color.Red
                            else
                                Color(0xFF4CAF50),

                        unfocusedBorderColor =
                            Color(0xFFD0D0D0),

                        focusedLabelColor =
                            if (oldPasswordError != null)
                                Color.Red
                            else
                                Color(0xFF4CAF50),

                        errorBorderColor =
                            Color.Red,

                        errorLabelColor =
                            Color.Red,

                        focusedTextColor =
                            Color.Black,

                        unfocusedTextColor =
                            Color.Black,

                        cursorColor =
                            Color(0xFF4CAF50),

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // NEW PASSWORD
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    validateNewPassword(it)
                },
                label = {
                    Text("New Password")
                },
                placeholder = {
                    Text("Enter your new password")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                isError = newPasswordError != null,
                visualTransformation =
                    if (newPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {

                    IconButton(
                        onClick = {
                            newPasswordVisible =
                                !newPasswordVisible
                        },
                        modifier = Modifier.size(36.dp)
                    ) {

                        Icon(
                            imageVector =
                                if (newPasswordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                            contentDescription =
                                if (newPasswordVisible)
                                    "Hide password"
                                else
                                    "Show password",
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
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor =
                            if (newPasswordError != null)
                                Color.Red
                            else
                                Color(0xFF4CAF50),

                        unfocusedBorderColor =
                            Color(0xFFD0D0D0),

                        focusedLabelColor =
                            if (newPasswordError != null)
                                Color.Red
                            else
                                Color(0xFF4CAF50),

                        errorBorderColor =
                            Color.Red,

                        errorLabelColor =
                            Color.Red,

                        focusedTextColor =
                            Color.Black,

                        unfocusedTextColor =
                            Color.Black,

                        cursorColor =
                            Color(0xFF4CAF50),

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // CONFIRM PASSWORD
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    validateConfirmPassword(it)
                },
                label = {
                    Text("Confirm Password")
                },
                placeholder = {
                    Text("Re-enter your new password")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                isError = confirmPasswordError != null,
                visualTransformation =
                    if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {

                    IconButton(
                        onClick = {
                            confirmPasswordVisible =
                                !confirmPasswordVisible
                        },
                        modifier = Modifier.size(36.dp)
                    ) {

                        Icon(
                            imageVector =
                                if (confirmPasswordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                            contentDescription =
                                if (confirmPasswordVisible)
                                    "Hide password"
                                else
                                    "Show password",
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
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor =
                            if (confirmPasswordError != null)
                                Color.Red
                            else
                                Color(0xFF4CAF50),

                        unfocusedBorderColor =
                            Color(0xFFD0D0D0),

                        focusedLabelColor =
                            if (confirmPasswordError != null)
                                Color.Red
                            else
                                Color(0xFF4CAF50),

                        errorBorderColor =
                            Color.Red,

                        errorLabelColor =
                            Color.Red,

                        focusedTextColor =
                            Color.Black,

                        unfocusedTextColor =
                            Color.Black,

                        cursorColor =
                            Color(0xFF4CAF50),

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    )
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            val isFormValid =
                oldPassword.isNotBlank() &&
                        newPassword.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        oldPasswordError == null &&
                        newPasswordError == null &&
                        confirmPasswordError == null

            // RESET BUTTON
            Button(
                onClick = {

                    if (!validateAll()) {
                        return@Button
                    }

                    isResetting = true

                    coroutineScope.launch {

                        try {

                            /*
                             * IMPORTANT:
                             *
                             * This app bypasses Supabase Auth entirely —
                             * login is done manually via UserSession +
                             * a Postgrest lookup, so there is never a
                             * real Supabase Auth session to check here.
                             *
                             * Instead we verify the old password and
                             * update it directly against the "users"
                             * table, keyed by the logged-in IC number.
                             */

                            val icNumber = UserSession.icNumber

                            if (icNumber.isBlank()) {

                                Toast.makeText(
                                    context,
                                    "You are not logged in. Please login again.",
                                    Toast.LENGTH_LONG
                                ).show()

                                isResetting = false
                                return@launch
                            }

                            // 1. Fetch current password for this user
                            //    to verify the old password is correct.
                            //    <-- adjust table/column names as needed
                            val currentRecord = SupabaseClient.client
                                .from("User")
                                .select {
                                    filter { eq("ic_number", icNumber) }
                                }
                                .decodeSingleOrNull<PasswordCheckRecord>()

                            if (currentRecord == null) {

                                Toast.makeText(
                                    context,
                                    "Could not find your account. Please login again.",
                                    Toast.LENGTH_LONG
                                ).show()

                                isResetting = false
                                return@launch
                            }

                            if (currentRecord.password != oldPassword) {

                                oldPasswordError = "Old password is incorrect"

                                Toast.makeText(
                                    context,
                                    "Old password is incorrect",
                                    Toast.LENGTH_LONG
                                ).show()

                                isResetting = false
                                return@launch
                            }

                            // 2. Update to the new password.
                            //    <-- adjust table/column names as needed
                            SupabaseClient.client
                                .from("User")
                                .update(
                                    mapOf("password" to newPassword)
                                ) {
                                    filter { eq("ic_number", icNumber) }
                                }

                            Toast.makeText(
                                context,
                                "✅ Password changed successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                            onResetSuccess()

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                "Password change failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()

                        } finally {

                            isResetting = false
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                enabled =
                    isFormValid &&
                            !isResetting,

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (isFormValid)
                                Color(0xFF00B87C)
                            else
                                Color(0xFFA8D5B0),

                        disabledContainerColor =
                            Color(0xFFA8D5B0)
                    ),

                shape =
                    RoundedCornerShape(10.dp)
            ) {

                if (isResetting) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp),

                        color =
                            Color.White,

                        strokeWidth =
                            2.dp
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
        }
    }
}