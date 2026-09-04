package com.example.energynest.userinfo_settings

import com.example.energynest.backend_models.SupabaseClient
import com.example.energynest.backend_models.UserSession
import com.example.energynest.backend_models.User
import com.example.energynest.routes.Screen
import com.example.energynest.R

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Duplicate import removed
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    userProfile: User? = null
) {
    val context = LocalContext.current

    val safeProfile = userProfile ?: User(
        icNumber = "000000000000",
        name = "Loading...",
        email = "loading@example.com",
        phoneNumber = "+60 00 000 0000",
        houseNo = "",
        street = "",
        zipCode = 0.0,
        city = "",
        state = "",
        password = "",
        accountId = null,
        accountStatus = "Active"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ---- Top App Bar ----
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "Back",
                        tint = Color(0xFF191C1E) // TextDark
                    )
                }

                Text(
                    text = "Profile",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191C1E) // TextDark
                )

                // ---- Edit Icon ----
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF191C1E) // TextDark
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE2E8F0)) // BorderLight
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val initials = safeProfile.name.trim().split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")

            Box(
                modifier = Modifier
                    .size(72.dp)
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
                    text = if (initials.isEmpty()) "JS" else initials,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = safeProfile.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = safeProfile.email,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                Toast.makeText(context, "Account Number cannot be edited", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Account Number", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(safeProfile.accountId ?: "N/A", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Lock, "Locked", tint = Color.Gray, modifier = Modifier.size(14.dp))
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
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Account Status", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (safeProfile.accountStatus == "Active") Color(0xFF00B87C) else Color.Red))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(safeProfile.accountStatus, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (safeProfile.accountStatus == "Active") Color(0xFF00B87C) else Color.Red)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Lock, "Locked", tint = Color.Gray, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Account Settings",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, start = 4.dp)
            )

            ProfileItem(Icons.Default.Lock, "Change Password", onClick = onChangePasswordClick)
            ProfileItem(Icons.Default.ReceiptLong, "Payment History", onClick = onPaymentHistoryClick)
            ProfileItem(Icons.Default.Logout, "Log Out", iconColor = Color(0xFFFF5722), textColor = Color(0xFFFF5722), onClick = onLogOutClick)
            ProfileItem(Icons.Default.Delete, "Delete Account", iconColor = Color.Red, textColor = Color.Red, onClick = onDeleteAccountClick)
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    title: String,
    iconColor: Color = Color(0xFF4CAF50),
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
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
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, title, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = textColor)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
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
                Text("Are you sure want to log out?", fontSize = 15.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text("You will need to login again to access your account.", fontSize = 13.sp, color = Color.Gray)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Log out", fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
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
    userIc: String,
    onBackToHome: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {},
    onLogoutConfirm: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(
        "EnergyNestPrefs",
        Context.MODE_PRIVATE
    )
    val currentIc = when {
        userIc.isNotEmpty() -> userIc

        UserSession.user?.icNumber?.isNotEmpty() == true ->
            UserSession.user!!.icNumber

        else ->
            sharedPreferences.getString("USER_IC", "") ?: ""
    }
    val coroutineScope = rememberCoroutineScope()
    var showEditProfile by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf<User?>(UserSession.user) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentIc) {

        if (currentIc.isNotEmpty()) {

            try {

                val result = withContext(Dispatchers.IO) {

                    SupabaseClient.client
                        .from("User")
                        .select {
                            filter {
                                eq("ic_number", currentIc)
                            }
                        }
                        .decodeSingleOrNull<User>()
                }

                if (result != null) {

                    // Restore user after app restart
                    UserSession.user = result

                    // Display user profile
                    userProfile = result
                }

            } catch (e: Exception) {

                Toast.makeText(
                    context,
                    "Error loading profile: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            } finally {

                isLoading = false
            }

        } else {

            isLoading = false
        }
    }

    if (showEditProfile && userProfile != null) {
        EditProfileScreen(
            initialProfile = userProfile!!,
            onSave = { updatedProfile ->
                coroutineScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            SupabaseClient.client.from("User")
                                .update({
                                    set("name", updatedProfile.name)
                                    set("email", updatedProfile.email)
                                    set("phone_number", updatedProfile.phoneNumber)
                                    set("house_no", updatedProfile.houseNo)
                                    set("street", updatedProfile.street)
                                }) {
                                    filter { eq("ic_number", currentIc) }
                                }
                        }
                        userProfile = updatedProfile
                        UserSession.user = updatedProfile
                        showEditProfile = false
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Update failed: \${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onBack = { showEditProfile = false }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                ProfileScreen(
                    onBack = onBackToHome,
                    onEditClick = { showEditProfile = true },
                    onChangePasswordClick = onChangePasswordClick,
                    onPaymentHistoryClick = onPaymentHistoryClick,
                    onLogOutClick = { showLogOutDialog = true },
                    onDeleteAccountClick = { showDeleteDialog = true },
                    userProfile = userProfile
                )
            }

            if (showDeleteDialog) {
                DeleteAccountDialog(
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                withContext(Dispatchers.IO) {
                                    // 1. Delete all related data first
                                    val tables = listOf("Booking", "Property", "Home", "Electric_usage", "Smart_Sell", "Feedback")
                                    tables.forEach { tableName ->
                                        try {
                                            SupabaseClient.client.from(tableName).delete {
                                                filter { eq("ic_number", currentIc) }
                                            }
                                        } catch (e: Exception) {}
                                    }

                                    // 2. Delete the User record itself
                                    SupabaseClient.client.from("User").delete {
                                        filter {
                                            eq("ic_number", currentIc)
                                        }
                                    }
                                }

                                UserSession.user = null
                                showDeleteDialog = false
                                onLogoutConfirm()
                                Toast.makeText(context, "Account successfully deleted", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Delete failed: \${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )
            }

            if (showLogOutDialog) {
                LogOutDialog(
                    onDismiss = {
                        showLogOutDialog = false
                    },
                    onConfirm = {
                        sharedPreferences.edit().clear().apply()
                        UserSession.user = null
                        showLogOutDialog = false
                        onLogoutConfirm()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}