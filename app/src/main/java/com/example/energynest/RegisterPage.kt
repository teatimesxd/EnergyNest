package com.example.energynest

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.SupabaseClient
import com.example.energynest.ui.theme.EnergyNestTheme
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.saveable.rememberSaveable
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val PrimaryGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)
val BorderGray = Color(0xFFD1D5DB)

@Composable
fun greenTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = PrimaryGreen,
    unfocusedBorderColor = BorderGray,
    errorBorderColor = ErrorRed,
    cursorColor = PrimaryGreen,
    focusedLabelColor = PrimaryGreen,
    unfocusedLabelColor = Color.Gray,
    errorLabelColor = ErrorRed
)

// IC number format
class IcNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(12)
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

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

// Malaysia Phone Number Format
class MalaysianPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(10)
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

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

// Email Validation
fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    return email.matches(emailPattern.toRegex())
}

@Composable
fun RegisterPage(
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    val context = LocalContext.current

    var fullName by rememberSaveable { mutableStateOf("") }
    var icNumber by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var houseNo by rememberSaveable { mutableStateOf("") }
    var street by rememberSaveable { mutableStateOf("") }
    var zipcode by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("") }
    var showMapPicker by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var privacyAccepted by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var nameError by rememberSaveable { mutableStateOf(false) }
    var icError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var phoneError by rememberSaveable { mutableStateOf(false) }
    var houseNoError by rememberSaveable { mutableStateOf(false) }
    var streetError by rememberSaveable { mutableStateOf(false) }
    var zipcodeError by rememberSaveable { mutableStateOf(false) }
    var cityError by rememberSaveable { mutableStateOf(false) }
    var stateError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    var confirmError by rememberSaveable { mutableStateOf(false) }
    var privacyError by rememberSaveable { mutableStateOf(false) }
    var registerMessage by rememberSaveable { mutableStateOf("") }

    // Location Permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showMapPicker = true
        } else {
            registerMessage = "Location permission denied. Please enable it in settings."
        }
    }

    val primaryGreen = PrimaryGreen
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val bgGray = Color(0xFFE2E8F0)
    val errorRed = ErrorRed

    // Validation
    fun validateAndRegister() {
        nameError = false
        icError = false
        emailError = false
        phoneError = false
        houseNoError = false
        streetError = false
        zipcodeError = false
        cityError = false
        stateError = false
        passwordError = false
        confirmError = false
        privacyError = false
        registerMessage = ""

        var valid = true

        if (fullName.isBlank()) {
            nameError = true
            valid = false
        }

        if (icNumber.length != 12) {
            icError = true
            valid = false
        }

        if (!isValidEmail(email)) {
            emailError = true
            valid = false
        }

        if (phoneNumber.length !in 9..10) {
            phoneError = true
            valid = false
        }

        if (houseNo.isBlank()) {
            houseNoError = true
            valid = false
        }

        if (street.isBlank()) {
            streetError = true
            valid = false
        }

        if (zipcode.isBlank()) {
            zipcodeError = true
            valid = false
        }

        if (zipcode.length != 5) {
            zipcodeError = true
            valid = false
        }

        if (city.isBlank()) {
            cityError = true
            valid = false
        }

        if (state.isBlank()) {
            stateError = true
            valid = false
        }

        if (password.length < 6) {
            passwordError = true
            valid = false
        }

        if (confirmPassword != password) {
            confirmError = true
            valid = false
        }

        if (!privacyAccepted) {
            privacyError = true
            valid = false
        }

        if (valid) {
            coroutineScope.launch {
                try {
                    isLoading = true
                    registerMessage = "Creating account..."

                    val cleanEmail = email.trim()
                    val cleanPassword = password.trim()

                    // 1. Insert user details into public.User table directly
                    val newUser = User(
                        icNumber = icNumber,
                        name = fullName,
                        email = cleanEmail,
                        phoneNumber = phoneNumber,
                        houseNo = houseNo,
                        street = street,
                        zipCode = zipcode.toDoubleOrNull() ?: 0.0,
                        city = city,
                        state = state,
                        password = cleanPassword,
                        accountId = null,
                        accountStatus = "Active"
                    )

                    withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("User").insert(newUser)
                    }

                    // 2. Set the global session
                    UserSession.user = newUser

                    registerMessage = "Registration successful!"
                    onRegisterSuccess()
                } catch (e: Exception) {
                    registerMessage = "Registration failed: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        } else {
            registerMessage = "Please check the errors above."
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 36.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.energynest_icon_1),
                    contentDescription = "App logo",
                    modifier = Modifier.size(150.dp)
                )

                Text(
                    text = "EnergyNest",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )

                Text(
                    text = "Create your account to start saving energy",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Text(
                    text = "Full Name",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        nameError = false
                    },
                    label = { Text("Enter your full name") },
                    placeholder = { Text("Example: John Tan") },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    colors = greenTextFieldColors()
                )

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
                        icError = false
                    },
                    label = { Text("Enter your IC number") },
                    placeholder = { Text("Example: 000000-00-0000") },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = IcNumberVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = icError,
                    colors = greenTextFieldColors()
                )

                Text(
                    text = "Email",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    label = { Text("Enter your email address") },
                    placeholder = { Text("Example: user@gmail.com") },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError,
                    colors = greenTextFieldColors()
                )

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
                        phoneNumber = input.filter { it.isDigit() }.take(10)
                        phoneError = false
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
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = MalaysianPhoneVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError,
                    colors = greenTextFieldColors()
                )

                Text(
                    text = "Address",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = houseNo,
                    onValueChange = {
                        houseNo = it
                        houseNoError = false
                    },
                    label = { Text("Unit / House No.") },
                    placeholder = { Text("Example: No. 12A / Lot 34") },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = houseNoError,
                    colors = greenTextFieldColors()
                )

                OutlinedTextField(
                    value = street,
                    onValueChange = {
                        street = it
                        streetError = false
                    },
                    label = { Text("Street") },
                    placeholder = { Text("Example: Jalan Ampang") },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = streetError,
                    colors = greenTextFieldColors()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = zipcode,
                        onValueChange = {
                            zipcode = it.filter { char -> char.isDigit() }.take(5)
                            zipcodeError = false
                        },
                        label = { Text("Zipcode") },
                        placeholder = { Text("50450") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        isError = zipcodeError,
                        colors = greenTextFieldColors()
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = {
                            city = it
                            cityError = false
                        },
                        label = { Text("City") },
                        placeholder = { Text("Kuala Lumpur") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        isError = cityError,
                        colors = greenTextFieldColors()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state,
                        onValueChange = {
                            state = it
                            stateError = false
                        },
                        label = { Text("State") },
                        placeholder = { Text("Selangor") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        isError = stateError,
                        colors = greenTextFieldColors()
                    )

                    IconButton(
                        onClick = {
                            val fineLocationGranted =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                            val coarseLocationGranted =
                                ContextCompat.checkSelfPermission(
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
                            painter = painterResource(id = R.drawable.location_icon),
                            contentDescription = "Select location",
                            tint = primaryGreen
                        )
                    }
                }

                if (showMapPicker) {
                    Dialog(
                        onDismissRequest = {
                            showMapPicker = false
                        },
                        properties = DialogProperties(
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            MapPicker(
                                onAddressSelected = { addressResult ->
                                    street = addressResult.street
                                    zipcode = addressResult.zipcode
                                    city = addressResult.city
                                    state = addressResult.state
                                    registerMessage = "Address picked from map"
                                },
                                onDismiss = {
                                    showMapPicker = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Password",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    label = { Text("Enter your password") },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) {
                                        R.drawable.visibility
                                    } else {
                                        R.drawable.non_visibility
                                    }
                                ),
                                contentDescription = "Show or hide password"
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError,
                    colors = greenTextFieldColors()
                )

                Text(
                    text = "Confirm Password",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmError = false
                    },
                    label = { Text("Confirm your password") },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (confirmPasswordVisible) {
                                        R.drawable.visibility
                                    } else {
                                        R.drawable.non_visibility
                                    }
                                ),
                                contentDescription = "Show or hide password"
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmError,
                    colors = greenTextFieldColors()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = privacyAccepted,
                        onCheckedChange = {
                            privacyAccepted = it
                            privacyError = false
                        }
                    )

                    Text(
                        text = "I agree to the ",
                        fontSize = 14.sp,
                        color = textGray
                    )

                    Text(
                        text = "Terms & Conditions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen,
                        modifier = Modifier.clickable {
                            onNavigateToTerms()
                        }
                    )
                }

                if (privacyError) {
                    Text(
                        text = "You must accept the Terms & Conditions",
                        color = errorRed,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        validateAndRegister()
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
                                text = "Register",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_icon),
                                contentDescription = null
                            )
                        }
                    }
                }

                if (registerMessage.isNotEmpty()) {
                    Text(
                        text = registerMessage,
                        color = if (
                            registerMessage.contains("successful", ignoreCase = true) ||
                            registerMessage.contains("picked", ignoreCase = true) ||
                            registerMessage.contains("creating", ignoreCase = true)
                        ) {
                            primaryGreen
                        } else {
                            errorRed
                        },
                        textAlign = TextAlign.Center
                    )
                }

                Row(
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
                            onBackToLogin()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        IconButton(
            onClick = {
                onBackToLogin()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 30.dp)
                .size(56.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = primaryGreen,
                modifier = Modifier.size(32.dp)
            )
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