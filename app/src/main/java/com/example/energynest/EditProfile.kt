package com.example.energynest

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    initialProfile: UserProfile = UserProfile(),
    onSave: (UserProfile) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf(initialProfile.name) }
    var email by remember { mutableStateOf(initialProfile.email) }
    var phone by remember { mutableStateOf(initialProfile.phone) }
    var address by remember { mutableStateOf(initialProfile.address) }

    var editingField by remember { mutableStateOf<String?>(null) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    val nameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val phoneFocus = remember { FocusRequester() }
    val addressFocus = remember { FocusRequester() }

    val scrollState = rememberScrollState()

    fun validateName(value: String): String? {
        return when {
            value.isBlank() -> "Name is required"
            !value.all { it.isLetter() || it.isWhitespace() } -> "Name can only contain letters"
            value.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    fun validateEmail(value: String): String? {
        val v = value.trim()
        return when {
            v.isBlank() -> "Email is required"
            !v.contains("@") -> "Email must contain @ (e.g., name@gmail.com)"
            !v.endsWith(".com") -> "Email must end with .com (e.g., name@gmail.com)"
            v.contains("@gmail.com") || v.contains("@yahoo.com") -> null
            else -> "Only Gmail or Yahoo allowed (name@gmail.com or name@yahoo.com)"
        }
    }

    fun validatePhone(value: String): String? {
        val v = value.trim()
        return when {
            v.isBlank() -> "Phone is required"
            !v.all { it.isDigit() || it == '+' || it == ' ' || it == '-' } ->
                "Phone can only contain numbers, +, space, or -"
            v.replace(Regex("[^0-9]"), "").length < 10 -> "Phone must be at least 10 digits"
            else -> null
        }
    }

    fun validateAddress(value: String): String? {
        return when {
            value.isBlank() -> "Address is required"
            value.length < 5 -> "Address must be at least 5 characters"
            else -> null
        }
    }

    fun hasErrors() = nameError != null || emailError != null || phoneError != null || addressError != null

    fun closeOthers(except: String? = null) {
        if (except != "name" && editingField == "name") nameError = validateName(name)
        if (except != "email" && editingField == "email") emailError = validateEmail(email)
        if (except != "phone" && editingField == "phone") phoneError = validatePhone(phone)
        if (except != "address" && editingField == "address") addressError = validateAddress(address)
        editingField = except
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
            IconButton(onClick = { onBack() }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF00B87C), modifier = Modifier.size(28.dp))
            }
            Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(40.dp))
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00B87C)),
                contentAlignment = Alignment.Center
            ) {
                val initials = name.trim().split(" ")
                    .filter { it.isNotEmpty() }
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .joinToString("")

                Text(
                    text = if (initials.isEmpty()) "JS" else initials,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                    EditField(
                        "Full Name", name, editingField == "name",
                        onEditToggle = {
                            if (editingField == "name") closeOthers(null)
                            else { closeOthers("name"); coroutineScope.launch { delay(100); nameFocus.requestFocus() } }
                        },
                        onValueChange = { name = it; nameError = validateName(it) },
                        Icons.Default.Person, nameFocus, nameError, "Enter your full name"
                    )
                    HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 10.dp))

                    EditField(
                        "Email Address", email, editingField == "email",
                        onEditToggle = {
                            if (editingField == "email") closeOthers(null)
                            else { closeOthers("email"); coroutineScope.launch { delay(100); emailFocus.requestFocus() } }
                        },
                        onValueChange = { email = it; emailError = validateEmail(it) },
                        Icons.Default.Email, emailFocus, emailError, "Enter your email"
                    )
                    HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 10.dp))

                    EditField(
                        "Phone Number", phone, editingField == "phone",
                        onEditToggle = {
                            if (editingField == "phone") closeOthers(null)
                            else { closeOthers("phone"); coroutineScope.launch { delay(100); phoneFocus.requestFocus() } }
                        },
                        onValueChange = { phone = it; phoneError = validatePhone(it) },
                        Icons.Default.Phone, phoneFocus, phoneError, "Enter your phone number"
                    )
                    HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 10.dp))

                    EditField(
                        "Home Address", address, editingField == "address",
                        onEditToggle = {
                            if (editingField == "address") closeOthers(null)
                            else { closeOthers("address"); coroutineScope.launch { delay(100); addressFocus.requestFocus() } }
                        },
                        onValueChange = { address = it; addressError = validateAddress(it) },
                        Icons.Default.Home, addressFocus, addressError, "Enter your home address"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val hasErr = hasErrors()
            Button(
                onClick = {
                    nameError = validateName(name)
                    emailError = validateEmail(email)
                    phoneError = validatePhone(phone)
                    addressError = validateAddress(address)
                    if (!hasErrors()) {
                        onSave(UserProfile(name, email, phone, address))
                        Toast.makeText(context, "✅ Profile updated!", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasErr) Color.Gray else Color(0xFF00B87C),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !hasErr
            ) {
                Text(if (hasErr) "Fix Errors to Save" else "Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            if (hasErr) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Please fix all errors before saving", fontSize = 13.sp, color = Color.Red, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    focusRequester: FocusRequester,
    error: String?,
    placeholder: String = ""
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = if (error != null && isEditing) Color.Red else Color(0xFF00B87C), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, color = if (error != null && isEditing) Color.Red else Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            if (isEditing) {
                OutlinedTextField(
                    value, onValueChange,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    isError = error != null,
                    placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray) },
                    supportingText = { if (error != null) Text(error, fontSize = 12.sp, color = Color.Red, modifier = Modifier.padding(top = 4.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (error != null) Color.Red else Color(0xFF4CAF50),
                        unfocusedBorderColor = if (error != null) Color.Red else Color(0xFF4CAF50),
                        focusedLabelColor = if (error != null) Color.Red else Color(0xFF4CAF50),
                        errorBorderColor = Color.Red, errorLabelColor = Color.Red,
                        focusedTextColor = if (error != null) Color.Red else Color.Black,
                        unfocusedTextColor = if (error != null) Color.Red else Color.Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                )
            } else {
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
        val isError = error != null && isEditing
        IconButton(onClick = { if (!isError) onEditToggle() }, modifier = Modifier.size(40.dp), enabled = !isError) {
            Icon(if (isEditing) Icons.Default.Check else Icons.Default.Edit, if (isEditing) "Save" else "Edit",
                tint = if (isError) Color.Gray else if (isEditing) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.size(22.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditProfile() {
    MaterialTheme { EditProfileScreen() }
}