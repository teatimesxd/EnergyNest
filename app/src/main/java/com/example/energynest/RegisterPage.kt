package com.example.energynest

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.energynest.ui.theme.EnergyNestTheme

// =====================================================
// REGISTER ACTIVITY
// =====================================================

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnergyNestTheme {
                RegisterPage()
            }
        }
    }
}

// =====================================================
// IC NUMBER VISUAL TRANSFORMATION
//
// User enters:
// 000000000000
//
// Display:
// 000000-00-0000
//
// Only digits are stored.
// =====================================================

class IcNumberVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
            .filter { it.isDigit() }
            .take(12)

        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                if (index == 6 || index == 8) {
                    append("-")
                }
                append(char)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 6 -> offset
                    offset <= 8 -> offset + 1
                    else -> offset + 2
                }.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 6 -> offset
                    offset == 7 -> 6
                    offset <= 9 -> offset - 1
                    else -> offset - 2
                }.coerceIn(0, digits.length)
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping
        )
    }
}

// =====================================================
// MALAYSIAN PHONE NUMBER VISUAL TRANSFORMATION
//
// Actual stored value:
// 166597894
//
// Displayed value:
// +60 16 659 7894
//
// IMPORTANT:
// +60 is NOT part of the editable value.
// It is displayed using prefix = { }.
// =====================================================

class MalaysianPhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
            .filter { it.isDigit() }
            .take(10)

        // Format:
        // 16
        // 16 6
        // 16 659
        // 16 659 7
        // 16 659 7894

        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 5) {
                    append(" ")
                }
                append(char)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset + 1
                    else -> offset + 2
                }.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 6 -> offset - 1
                    else -> offset - 2
                }.coerceIn(0, digits.length)
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping
        )
    }
}

// =====================================================
// EMAIL VALIDATION
// =====================================================

fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    return email.matches(emailPattern.toRegex())
}

// =====================================================
// REGISTER PAGE
// =====================================================

@Composable
fun RegisterPage() {
    val context = LocalContext.current

    // =================================================
    // USER INPUT STATES
    // =================================================

    var fullName by remember { mutableStateOf("") }
    var icNumber by remember { mutableStateOf("") }        // stores ONLY digits
    var email by remember { mutableStateOf("") }

    // Phone number – stores only digits (e.g. "166597894")
    var phoneNumber by remember { mutableStateOf("") }

    // Address states
    var street by remember { mutableStateOf("") }
    var zipcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var showMapPicker by remember { mutableStateOf(false) }

    // Password states
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Privacy
    var privacyAccepted by remember { mutableStateOf(false) }

    // Password visibility
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Error states
    var nameError by remember { mutableStateOf(false) }
    var icError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var streetError by remember { mutableStateOf(false) }
    var zipcodeError by remember { mutableStateOf(false) }
    var cityError by remember { mutableStateOf(false) }
    var stateError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmError by remember { mutableStateOf(false) }
    var privacyError by remember { mutableStateOf(false) }

    var registerMessage by remember { mutableStateOf("") }

    // =================================================
    // LOCATION PERMISSION
    // =================================================

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showMapPicker = true
        } else {
            registerMessage = "Location permission denied. Please enable it in settings."
        }
    }

    // =================================================
    // COLORS
    // =================================================

    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val bgGray = Color(0xFFE2E8F0)
    val errorRed = Color(0xFFEF4444)

    // =================================================
    // VALIDATION
    // =================================================

    fun validateAndRegister() {
        // Reset errors
        nameError = false
        icError = false
        emailError = false
        phoneError = false
        streetError = false
        zipcodeError = false
        cityError = false
        stateError = false
        passwordError = false
        confirmError = false
        privacyError = false
        registerMessage = ""

        var valid = true

        // Full Name
        if (fullName.isBlank()) {
            nameError = true
            valid = false
        }

        // IC Number – exactly 12 digits
        if (icNumber.length != 12) {
            icError = true
            valid = false
        }

        // Email
        if (!isValidEmail(email)) {
            emailError = true
            valid = false
        }

        // Phone – Malaysian mobile number: 9 or 10 digits after +60
        if (phoneNumber.length !in 9..10) {
            phoneError = true
            valid = false
        }

        // Street
        if (street.isBlank()) {
            streetError = true
            valid = false
        }

        // Zipcode
        if (zipcode.isBlank()) {
            zipcodeError = true
            valid = false
        }

        // City
        if (city.isBlank()) {
            cityError = true
            valid = false
        }

        // State
        if (state.isBlank()) {
            stateError = true
            valid = false
        }

        // Password
        if (password.length < 6) {
            passwordError = true
            valid = false
        }

        // Confirm Password
        if (confirmPassword != password) {
            confirmError = true
            valid = false
        }

        // Privacy Policy
        if (!privacyAccepted) {
            privacyError = true
            valid = false
        }

        // Final result
        if (valid) {
            val databasePhone = "+60$phoneNumber"
            registerMessage = "Registration successful!"
        } else {
            registerMessage = "Please fix the errors above."
        }
    }

    // =================================================
    // MAIN SCREEN
    // =================================================
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
                    .padding(
                        horizontal = 28.dp,
                        vertical = 36.dp
                    )
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.energynest_icon_1),
                    contentDescription = "App logo",
                    modifier = Modifier.size(150.dp)
                )

                // App name
                Text(
                    text = "EnergyNest",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )

                // Subtitle
                Text(
                    text = "Create your account to start saving energy",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Full Name
                Text(
                    text = "Full Name",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Enter your full name") },
                    placeholder = { Text("Example: John Tan") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Name is required", color = errorRed) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (nameError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (nameError) errorRed else Color.LightGray,
                        focusedLabelColor = if (nameError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )

                // IC Number
                Text(
                    text = "IC Number",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = icNumber,
                    onValueChange = { input ->
                        icNumber = input.filter { it.isDigit() }.take(12)
                    },
                    label = { Text("Enter your IC number") },
                    placeholder = { Text("Example: 000000-00-0000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = IcNumberVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = icError,
                    supportingText = if (icError) {
                        { Text("Enter IC number in format 000000-00-0000", color = errorRed) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (icError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (icError) errorRed else Color.LightGray,
                        focusedLabelColor = if (icError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )

                // Email
                Text(
                    text = "Email",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter your email address") },
                    placeholder = { Text("Example: user@gmail.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError,
                    supportingText = if (emailError) {
                        { Text("Please enter a valid email address", color = errorRed) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (emailError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (emailError) errorRed else Color.LightGray,
                        focusedLabelColor = if (emailError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )

                // Phone Number
                Text(
                    text = "Phone Number",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        // Accept only digits
                        phoneNumber = input.filter { it.isDigit() }.take(10)
                    },
                    label = { Text("Enter your phone number") },
                    placeholder = { Text("16 659 7894") },
                    prefix = {
                        Text(
                            text = "+60 ",
                            fontWeight = FontWeight.Medium,
                            color = textGray
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = MalaysianPhoneVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError,
                    supportingText = if (phoneError) {
                        { Text("Enter a valid Malaysian phone number", color = errorRed) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (phoneError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (phoneError) errorRed else Color.LightGray,
                        focusedLabelColor = if (phoneError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )

                // Address
                Text(
                    text = "Address",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )

                // Street
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street") },
                    placeholder = { Text("Example: Jalan Ampang") },
                    singleLine = false,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = streetError,
                    supportingText = if (streetError) {
                        { Text("Street is required", color = errorRed) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (streetError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (streetError) errorRed else Color.LightGray,
                        focusedLabelColor = if (streetError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )

                // Zipcode + City
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = zipcode,
                        onValueChange = { zipcode = it.filter { char -> char.isDigit() } },
                        label = { Text("Zipcode") },
                        placeholder = { Text("Example: 50450") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        isError = zipcodeError,
                        supportingText = if (zipcodeError) {
                            { Text("Required", color = errorRed) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (zipcodeError) errorRed else primaryGreen,
                            unfocusedBorderColor = if (zipcodeError) errorRed else Color.LightGray,
                            focusedLabelColor = if (zipcodeError) errorRed else primaryGreen,
                            unfocusedLabelColor = textGray
                        )
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        placeholder = { Text("Example: Kuala Lumpur") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        isError = cityError,
                        supportingText = if (cityError) {
                            { Text("Required", color = errorRed) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (cityError) errorRed else primaryGreen,
                            unfocusedBorderColor = if (cityError) errorRed else Color.LightGray,
                            focusedLabelColor = if (cityError) errorRed else primaryGreen,
                            unfocusedLabelColor = textGray
                        )
                    )
                }

                // State + Map button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        placeholder = { Text("Example: Selangor") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        isError = stateError,
                        supportingText = if (stateError) {
                            { Text("Required", color = errorRed) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (stateError) errorRed else primaryGreen,
                            unfocusedBorderColor = if (stateError) errorRed else Color.LightGray,
                            focusedLabelColor = if (stateError) errorRed else primaryGreen,
                            unfocusedLabelColor = textGray
                        )
                    )

                    IconButton(
                        onClick = {
                            val fineLocationGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            val coarseLocationGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (fineLocationGranted || coarseLocationGranted) {
                                showMapPicker = true
                            } else {
                                locationPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Select location",
                            tint = primaryGreen
                        )
                    }
                }

                // Map Picker Dialog
                if (showMapPicker) {
                    Dialog(
                        onDismissRequest = { showMapPicker = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            MapPicker(
                                onAddressSelected = { addressResult ->
                                    street = addressResult.street
                                    zipcode = addressResult.zipcode
                                    city = addressResult.city
                                    state = addressResult.state
                                    registerMessage = "Address picked from map"
                                },
                                onDismiss = { showMapPicker = false }
                            )
                        }
                    }
                }

                // Password
                Text(
                    text = "Password",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter your password") },
                    placeholder = { Text("Example: MyPassword123") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Outlined.Visibility
                                } else {
                                    Icons.Outlined.VisibilityOff
                                },
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = textGray
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError,
                    supportingText = if (passwordError) {
                        { Text("Password must be at least 6 characters", color = errorRed) }
                    } else {
                        { Text("Use at least 6 characters", color = textGray) }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (passwordError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (passwordError) errorRed else Color.LightGray,
                        focusedLabelColor = if (passwordError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )

                // Confirm Password
                Text(
                    text = "Confirm Password",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm your password") },
                    placeholder = { Text("Enter the same password again") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) {
                                    Icons.Outlined.Visibility
                                } else {
                                    Icons.Outlined.VisibilityOff
                                },
                                contentDescription = "Show or hide password",
                                tint = textGray
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmError,
                    supportingText = if (confirmError) {
                        { Text("Passwords do not match", color = errorRed) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (confirmError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (confirmError) errorRed else Color.LightGray,
                        focusedLabelColor = if (confirmError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )

                // Privacy Policy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = privacyAccepted,
                        onCheckedChange = { privacyAccepted = it },
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = "I agree to the ",
                        fontSize = 14.sp,
                        color = textGray
                    )

                    Text(
                        text = "Privacy Policy",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen,
                        modifier = Modifier.clickable {
                            // Open Privacy Policy
                        }
                    )
                }

                if (privacyError) {
                    Text(
                        text = "You must accept the Privacy Policy",
                        color = errorRed,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Register Button
                Button(
                    onClick = { validateAndRegister() },
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
                        Text(
                            text = "Register",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Status Message
                if (registerMessage.isNotEmpty()) {
                    Text(
                        text = registerMessage,
                        color = if (registerMessage.contains("successful") ||
                            registerMessage.contains("picked")
                        ) primaryGreen else errorRed,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // -------------------- BACK BUTTON --------------------
        IconButton(
            onClick = {
                // Close this activity → go back to the Login page
                (context as? Activity)?.finish()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 30.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color(0xFF1E293B)
            )
        }
    }
}

// =====================================================
// PREVIEW
// =====================================================

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    EnergyNestTheme {
        RegisterPage()
    }
}