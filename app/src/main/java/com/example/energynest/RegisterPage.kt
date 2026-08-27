package com.example.energynest

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.energynest.ui.theme.EnergyNestTheme
import java.io.File

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

@Composable
fun RegisterPage() {
    val context = LocalContext.current

    // --- States ---
    var fullName by remember { mutableStateOf("") }
    var icNumber by remember { mutableStateOf("") }

    // Address split into four parts – now using "Zipcode"
    var street by remember { mutableStateOf("") }
    var zipcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var showMapPicker by remember { mutableStateOf(false) }

    var supportingDocument by remember { mutableStateOf<File?>(null) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var privacyAccepted by remember { mutableStateOf(false) }

    // Visibility toggles
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Error states
    var nameError by remember { mutableStateOf(false) }
    var icError by remember { mutableStateOf(false) }
    var streetError by remember { mutableStateOf(false) }
    var zipcodeError by remember { mutableStateOf(false) }
    var cityError by remember { mutableStateOf(false) }
    var stateError by remember { mutableStateOf(false) }
    var docError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmError by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf(false) }
    var privacyError by remember { mutableStateOf(false) }

    var registerMessage by remember { mutableStateOf("") }

    // --- Launchers ---
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            supportingDocument = File(it.path ?: "")
        }
    }

    // Location permission launcher – now opens map picker when granted
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showMapPicker = true
        } else {
            registerMessage = "Location permission denied. Please enable it in settings."
        }
    }

    // --- Colors ---
    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val bgGray = Color(0xFFE2E8F0)
    val errorRed = Color(0xFFEF4444)

    // --- Validation ---
    fun validateAndRegister() {
        nameError = false
        icError = false
        streetError = false
        zipcodeError = false
        cityError = false
        stateError = false
        docError = false
        passwordError = false
        confirmError = false
        otpError = false
        privacyError = false
        registerMessage = ""

        var valid = true

        if (fullName.isBlank()) { nameError = true; valid = false }
        if (icNumber.isBlank()) { icError = true; valid = false }
        if (street.isBlank()) { streetError = true; valid = false }
        if (zipcode.isBlank()) { zipcodeError = true; valid = false }
        if (city.isBlank()) { cityError = true; valid = false }
        if (state.isBlank()) { stateError = true; valid = false }
        if (supportingDocument == null) { docError = true; valid = false }
        if (password.length < 6) { passwordError = true; valid = false }
        if (confirmPassword != password) { confirmError = true; valid = false }
        if (otp.length != 6) { otpError = true; valid = false }
        if (!privacyAccepted) { privacyError = true; valid = false }

        if (valid) {
            registerMessage = "Registration successful!"
        } else {
            registerMessage = "Please fix the errors above."
        }
    }

    // --- Scrollable Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGray)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 28.dp, vertical = 36.dp)
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

            // --- Full Name ---
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
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = if (nameError) { { Text("Name is required", color = errorRed) } } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (nameError) errorRed else primaryGreen,
                    unfocusedBorderColor = if (nameError) errorRed else Color.LightGray,
                    focusedLabelColor = if (nameError) errorRed else primaryGreen,
                    unfocusedLabelColor = textGray
                )
            )

            // --- IC Number ---
            Text(
                text = "IC Number",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textGray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
            OutlinedTextField(
                value = icNumber,
                onValueChange = { icNumber = it },
                label = { Text("Enter your IC number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = icError,
                supportingText = if (icError) { { Text("IC number is required", color = errorRed) } } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (icError) errorRed else primaryGreen,
                    unfocusedBorderColor = if (icError) errorRed else Color.LightGray,
                    focusedLabelColor = if (icError) errorRed else primaryGreen,
                    unfocusedLabelColor = textGray
                )
            )

            // --- Address: Street, Zipcode, City, State (with location button next to State) ---
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
                singleLine = false,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = streetError,
                supportingText = if (streetError) { { Text("Street is required", color = errorRed) } } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (streetError) errorRed else primaryGreen,
                    unfocusedBorderColor = if (streetError) errorRed else Color.LightGray,
                    focusedLabelColor = if (streetError) errorRed else primaryGreen,
                    unfocusedLabelColor = textGray
                )
            )

            // Zipcode and City in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = zipcode,
                    onValueChange = { zipcode = it },
                    label = { Text("Zipcode") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    isError = zipcodeError,
                    supportingText = if (zipcodeError) { { Text("Required", color = errorRed) } } else null,
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
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    isError = cityError,
                    supportingText = if (cityError) { { Text("Required", color = errorRed) } } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (cityError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (cityError) errorRed else Color.LightGray,
                        focusedLabelColor = if (cityError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )
            }

            // State with location icon on the same row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    isError = stateError,
                    supportingText = if (stateError) { { Text("Required", color = errorRed) } } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (stateError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (stateError) errorRed else Color.LightGray,
                        focusedLabelColor = if (stateError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )
                // Location button – now placed right next to State
                IconButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            showMapPicker = true
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Choose on Map",
                        tint = primaryGreen
                    )
                }
            }

            // Show the map picker as a full-screen dialog
            if (showMapPicker) {
                Dialog(
                    onDismissRequest = { showMapPicker = false },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false,
                        decorFitsSystemWindows = false
                    )
                ) {
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

            // --- Supporting Document ---
            Text(
                text = "Supporting Document",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textGray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
            OutlinedButton(
                onClick = {
                    filePickerLauncher.launch(arrayOf("*/*"))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryGreen
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null
                    )
                    Text(
                        text = if (supportingDocument != null) "Document selected: ${supportingDocument?.name}" else "Choose a file (PDF, image, etc.)",
                        fontSize = 14.sp,
                        color = if (supportingDocument != null) primaryGreen else textGray
                    )
                }
            }
            if (docError) {
                Text(
                    text = "Please select a supporting document",
                    color = errorRed,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            // --- Password ---
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
                label = { Text("Min 6 characters") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp),
                            tint = textGray
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError,
                supportingText = if (passwordError) { { Text("Password must be at least 6 characters", color = errorRed) } } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (passwordError) errorRed else primaryGreen,
                    unfocusedBorderColor = if (passwordError) errorRed else Color.LightGray,
                    focusedLabelColor = if (passwordError) errorRed else primaryGreen,
                    unfocusedLabelColor = textGray
                )
            )

            // --- Confirm Password ---
            Text(
                text = "Confirm Password",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textGray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Re-enter your password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp),
                            tint = textGray
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = confirmError,
                supportingText = if (confirmError) { { Text("Passwords do not match", color = errorRed) } } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (confirmError) errorRed else primaryGreen,
                    unfocusedBorderColor = if (confirmError) errorRed else Color.LightGray,
                    focusedLabelColor = if (confirmError) errorRed else primaryGreen,
                    unfocusedLabelColor = textGray
                )
            )

            // --- OTP ---
            Text(
                text = "One‑Time Password (OTP)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textGray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it },
                    label = { Text("Enter 6‑digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    isError = otpError,
                    supportingText = if (otpError) { { Text("OTP must be 6 digits", color = errorRed) } } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (otpError) errorRed else primaryGreen,
                        unfocusedBorderColor = if (otpError) errorRed else Color.LightGray,
                        focusedLabelColor = if (otpError) errorRed else primaryGreen,
                        unfocusedLabelColor = textGray
                    )
                )
                OutlinedButton(
                    onClick = {
                        // Simulate sending OTP
                        otp = "123456"
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send OTP")
                }
            }

            // --- Privacy Policy Checkbox ---
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
                        // Open privacy policy link or dialog
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

            // --- Register Button ---
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
                    Text("Register", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // --- Status Message ---
            if (registerMessage.isNotEmpty()) {
                Text(
                    text = registerMessage,
                    color = if (registerMessage.contains("successful") || registerMessage.contains("picked")) primaryGreen else errorRed,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // --- Already have account? Login ---
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Login",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen,
                    modifier = Modifier.clickable {
                        // Navigate to LoginPage
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    EnergyNestTheme {
        RegisterPage()
    }
}