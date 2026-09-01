package com.example.energynest

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class UserProfile(
    val name: String = "John Smith",
    val email: String = "johnsmith@yahoo.com",
    val phone: String = "+60 12 345 6789",
    val address: String = "Kuala Lumpur, Malaysia"
)

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onResetPasswordClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    userProfile: UserProfile = UserProfile()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF00B87C), modifier = Modifier.size(28.dp))
            }
            Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onEditClick, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Edit, "Edit", tint = Color(0xFF00B87C), modifier = Modifier.size(28.dp))
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val initials = userProfile.name.trim().split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00B87C))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        Toast.makeText(context, "Change photo", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (initials.isEmpty()) "JS" else initials,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                userProfile.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                userProfile.email,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                Toast.makeText(context, "⚠️ TnB Account Number cannot be edited", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TnB Account Number", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("9000 1234 5678", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Lock, "Locked", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), color = Color.LightGray)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                Toast.makeText(context, "⚠️ Account Status cannot be edited", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Account Status", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00B87C)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Active", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00B87C))
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Lock, "Locked", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Account Settings",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 4.dp)
            )

            ProfileItem(Icons.Default.Lock, "Reset Password", onClick = onResetPasswordClick)
            ProfileItem(Icons.Default.Logout, "Log Out", iconColor = Color(0xFFFF5722), textColor = Color(0xFFFF5722), onClick = onLogOutClick)
            ProfileItem(Icons.Default.Delete, "Delete Account", iconColor = Color.Red, textColor = Color.Red, onClick = onDeleteAccountClick)
        }
    }
}

@Composable
fun ProfileItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    iconColor: Color = Color(0xFF4CAF50),
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, title, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red) },
        text = {
            Column {
                Text("Are you sure you want to delete your account?", fontSize = 15.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text("This action cannot be undone.", fontSize = 13.sp, color = Color.Gray)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", fontWeight = FontWeight.Bold, color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Medium, color = Color(0xFF4CAF50))
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun LogOutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Out", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5722)) },
        text = {
            Column {
                Text("Are you sure you want to log out?", fontSize = 15.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text("You will need to sign in again to access your account.", fontSize = 13.sp, color = Color.Gray)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Log Out", fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Medium, color = Color(0xFF4CAF50))
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun ProfileScreenWrapper(
    onBackToHome: () -> Unit = {}
) {
    val context = LocalContext.current
    var showEditProfile by remember { mutableStateOf(false) }
    var showResetPassword by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }
    var userProfile by remember {
        mutableStateOf(
            UserProfile(
                name = "John Smith",
                email = "johnsmith@yahoo.com",
                phone = "+60 12 345 6789",
                address = "Kuala Lumpur, Malaysia"
            )
        )
    }

    when {
        showResetPassword -> {
            Toast.makeText(context, "Reset Password Screen", Toast.LENGTH_SHORT).show()
            showResetPassword = false
        }
        showEditProfile -> {
            EditProfileScreen(
                initialProfile = userProfile,
                onSave = { updatedProfile ->
                    userProfile = updatedProfile
                    showEditProfile = false
                },
                onBack = { showEditProfile = false }
            )
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ProfileScreen(
                    onBack = onBackToHome,
                    onEditClick = { showEditProfile = true },
                    onResetPasswordClick = { showResetPassword = true },
                    onLogOutClick = { showLogOutDialog = true },
                    onDeleteAccountClick = { showDeleteDialog = true },
                    userProfile = userProfile
                )

                if (showDeleteDialog) {
                    DeleteAccountDialog(
                        onDismiss = { showDeleteDialog = false },
                        onConfirm = {
                            showDeleteDialog = false
                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (showLogOutDialog) {
                    LogOutDialog(
                        onDismiss = { showLogOutDialog = false },
                        onConfirm = {
                            showLogOutDialog = false
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}