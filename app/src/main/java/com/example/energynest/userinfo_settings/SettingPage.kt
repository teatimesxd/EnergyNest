package com.example.energynest.userinfo_settings

import com.example.energynest.backend_models.UserSession
import com.example.energynest.R

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.energynest.ui.theme.EnergyNestTheme

@Composable
fun SettingPage(
    onBackClick: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    onNavigateToFeedback: () -> Unit = {},
    onLogoutConfirmed: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences(
        "EnergyNestPrefs",
        Context.MODE_PRIVATE
    )

    val userProfile = UserSession.user
    val userName = userProfile?.name ?: "Loading..."
    val userEmail = userProfile?.email ?: "loading@example.com"

    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val backgroundGray = Color(0xFFE2E8F0)
    val dividerColor = Color(0xFFE5E7EB)
    val deleteRed = Color(0xFFEF4444)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToProfile() },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0FDF4)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = primaryGreen
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.person_icon),
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userEmail,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle(title = "Account")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsItem(
                icon = R.drawable.person_icon,
                title = "Profile",
                subtitle = "View and update your profile",
                iconColor = primaryGreen,
                onClick = onNavigateToProfile
            )

            SettingsDivider(color = dividerColor)

            SettingsItem(
                icon = R.drawable.lock_icon,
                title = "Change Password",
                subtitle = "Update your account password",
                iconColor = primaryGreen,
                onClick = onNavigateToChangePassword
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle(title = "Support & Privacy")
            Spacer(modifier = Modifier.height(8.dp))

            // Privacy Policy
            SettingsItem(
                icon = R.drawable.privacy_tip_icon,
                title = "Privacy Policy",
                subtitle = "View our privacy policy",
                iconColor = primaryGreen,
                onClick = onNavigateToPrivacyPolicy
            )

            SettingsDivider(color = dividerColor)

            SettingsItem(
                icon = R.drawable.description_icon,
                title = "Terms & Conditions",
                subtitle = "Read our terms and conditions",
                iconColor = primaryGreen,
                onClick = onNavigateToTerms
            )

            SettingsDivider(color = dividerColor)

            // Feedback
            SettingsItem(
                icon = R.drawable.feedback_icon,
                title = "Feedback",
                subtitle = "Share your feedback with us",
                iconColor = primaryGreen,
                onClick = onNavigateToFeedback
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Delete Account
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFEF2F2)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_icon),
                        contentDescription = "Delete Account",
                        tint = deleteRed
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Delete Account",
                        color = deleteRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Log Out Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFEF2F2)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout_icon),
                        contentDescription = "Log out",
                        tint = deleteRed
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Log out",
                        color = deleteRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "EnergyNest Version 1.0.0",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 26.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = primaryGreen
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Confirm Log out", fontWeight = FontWeight.Bold) },
                text = { Text(text = "Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            sharedPreferences.edit().clear().apply()

                            // Clear current user session
                            UserSession.user = null
                            showLogoutDialog = false

                            // Navigate back to Login Page
                            onLogoutConfirmed()
                        }
                    ) {
                        Text(text = "Log out", color = deleteRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(text = "Cancel", color = primaryGreen, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF505F76),
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: Int,
    title: String,
    subtitle: String,
    iconColor: Color,
    titleColor: Color = Color(0xFF1E293B),
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF0FDF4)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF505F76)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.keyboard_arrow_right),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun SettingsDivider(color: Color) {
    HorizontalDivider(thickness = 1.dp, color = color)
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    EnergyNestTheme {
        SettingPage()
    }
}