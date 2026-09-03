package com.example.energynest

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreen = Color(0xFF00B87C)
private val BorderLight = Color(0xFFE2E8F0)
private val White = Color.White
private val LightGreenBg = Color(0xFFD8F3E5)

@Composable
fun LegaEligibilityScreen(
    userIc: String,
    onBack: () -> Unit = {},
    onCompleteAssessment: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Separated Address State
    var houseNo by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var zipcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var propertyType by remember { mutableStateOf("Terrace") }
    var showMapPicker by remember { mutableStateOf(false) }

    // Payment & Submission State
    var selectedPaymentMethod by remember { mutableStateOf("Touch 'n Go") }
    var showPaymentPage by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showMapPicker = true
        }
    }

    // Common TextField Colors
    val greenTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        disabledTextColor = Color.Black,
        focusedBorderColor = BrandGreen,
        unfocusedBorderColor = BorderLight,
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        cursorColor = BrandGreen,
        focusedLabelColor = BrandGreen,
        unfocusedLabelColor = TextGray
    )

    val paymentMethods = listOf(
        PaymentMethodType("Touch 'n Go", painterResource(id = R.drawable.ewallet), Color(0xFF00A651)),
        PaymentMethodType("Visa", painterResource(id = R.drawable.visa), Color(0xFF1A237E)),
        PaymentMethodType("Mastercard", painterResource(id = R.drawable.mastercard), Color(0xFFE65100))
    )

    // Handle Active Payment Page Display
    if (showPaymentPage) {
        val onPaymentSuccessAction = {
            coroutineScope.launch {
                try {
                    val now = Date()
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
                    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)

                    // 1. Create Payment Record
                    val payment = PaymentData(
                        title = "LEGA Roof Assessment Deposit",
                        referenceNo = UUID.randomUUID().toString(),
                        method = selectedPaymentMethod,
                        date = dateStr,
                        time = timeStr,
                        subtotal = 50.0,
                        sst = 0.0,
                        amount = 50.0,
                        status = true
                    )

                    val paymentResult = withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Payment")
                            .insert(payment) { select() }
                            .decodeSingle<PaymentData>()
                    }

                    // 2. Create Cream Record
                    val cream = CreamData(
                        paymentId = paymentResult.paymentId,
                        isEligible = true,
                        estimatedIncomeMin = 100.0,
                        estimatedIncomeMax = 300.0,
                        shadingLevel = "Low"
                    )

                    val creamResult = withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Cream")
                            .insert(cream) { select() }
                            .decodeSingle<CreamData>()
                    }

                    // 3. Create Property Record
                    val property = PropertyData(
                        icNumber = userIc,
                        creamId = creamResult.creamId!!,
                        propertyType = propertyType,
                        roofSpaceSqFt = 1200.0 
                    )

                    withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Property")
                            .insert(property)
                    }

                    // 4. Update User Address
                    withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("User")
                            .update({
                                set("house_no", houseNo)
                                set("street", street)
                                set("zip_code", zipcode.toDoubleOrNull() ?: 0.0)
                                set("city", city)
                                set("state", state)
                            }) {
                                filter { eq("ic_number", userIc) }
                            }
                    }

                    showPaymentPage = false
                    isSubmitted = true
                } catch (e: Exception) {
                    // Handle error
                }
            }
            Unit
        }

        when (selectedPaymentMethod) {
            "Visa" -> VisaPaymentPage(
                onBack = { showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )
            "Mastercard" -> MastercardPaymentPage(
                onBack = { showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )
            "Touch 'n Go" -> TnGPaymentPage(
                onBack = { showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark
                    )
                }
                Text(
                    text = "LEGA Roof Assessment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
            HorizontalDivider(thickness = 1.dp, color = BorderLight)
        }

        // Form Body
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreenBg)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BrandGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "LEGA Roof Assessment",
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Submit property details and place a RM 50 deposit to request a rooftop solar yield evaluation.",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }
                    }
                }

                // --- PROPERTY ADDRESS SECTION ---
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "PROPERTY LOCATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )

                    // House / Unit No.
                    OutlinedTextField(
                        value = houseNo,
                        onValueChange = { houseNo = it },
                        label = { Text("Unit / House No.") },
                        placeholder = { Text("Example: No. 12A / Lot 34") },
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSubmitted,
                        singleLine = true,
                        colors = greenTextFieldColors
                    )

                    // Street
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("Street Address") },
                        placeholder = { Text("Example: Jalan Ampang") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSubmitted,
                        singleLine = true,
                        colors = greenTextFieldColors
                    )

                    // Zipcode & City
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = zipcode,
                            onValueChange = { zipcode = it.filter { char -> char.isDigit() }.take(5) },
                            label = { Text("Zipcode") },
                            placeholder = { Text("50450") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            enabled = !isSubmitted,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = greenTextFieldColors
                        )

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            placeholder = { Text("Kuala Lumpur") },
                            singleLine = true,
                            enabled = !isSubmitted,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = greenTextFieldColors
                        )
                    }

                    // State & Location Picker Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State") },
                            placeholder = { Text("Selangor") },
                            singleLine = true,
                            enabled = !isSubmitted,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = greenTextFieldColors
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
                                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                            enabled = !isSubmitted
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.location_icon),
                                contentDescription = "Select location from map",
                                tint = BrandGreen
                            )
                        }
                    }

                    // Property Type Selection
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "PROPERTY TYPE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray
                        )
                        var expanded by remember { mutableStateOf(false) }
                        val propertyTypes = listOf("Terrace", "Semi-D", "Bungalow")
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.dp, if (expanded) BrandGreen else BorderLight, RoundedCornerShape(12.dp))
                                .background(White)
                                .clickable(enabled = !isSubmitted) { expanded = !expanded }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.home_icon),
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = propertyType,
                                        fontSize = 16.sp,
                                        color = TextDark
                                    )
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_drop_down),
                                    contentDescription = "Select Property Type",
                                    tint = TextDark
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(White).fillMaxWidth(0.9f)
                            ) {
                                propertyTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = type,
                                                fontSize = 16.sp,
                                                color = TextDark
                                            )
                                        },
                                        onClick = {
                                            propertyType = type
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
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
                                    showMapPicker = false
                                },
                                onDismiss = { showMapPicker = false }
                            )
                        }
                    }
                }

                // --- ASSESSMENT DEPOSIT & PAYMENT METHOD SECTION ---
                if (!isSubmitted) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "ASSESSMENT DEPOSIT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Payment,
                                        contentDescription = null,
                                        tint = BrandGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Roof Inspection Deposit",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = "Refundable if ineligible",
                                            fontSize = 11.sp,
                                            color = TextGray
                                        )
                                    }
                                }
                                Text(
                                    text = "RM 50.00",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGreen
                                )
                            }
                        }

                        Text(
                            text = "SELECT PAYMENT METHOD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray,
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        paymentMethods.forEach { method ->
                            val isSelected = selectedPaymentMethod == method.name
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) BrandGreen else BorderLight,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        selectedPaymentMethod = method.name
                                    },
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(method.color.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = method.icon,
                                                contentDescription = method.name,
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Text(
                                            text = method.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextDark
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPaymentMethod = method.name },
                                        colors = RadioButtonDefaults.colors(selectedColor = BrandGreen)
                                    )
                                }
                            }
                        }
                    }
                }

                // --- SUBMISSION CONFIRMATION CARD ---
                if (isSubmitted) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "Deposit Paid & Assessment Submitted!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Your RM 50.00 deposit via $selectedPaymentMethod has been processed. Please wait 1-3 working days for the evaluation result.",
                                fontSize = 13.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Action Button
                val isFormValid = houseNo.isNotBlank() && street.isNotBlank() && zipcode.isNotBlank() && city.isNotBlank() && state.isNotBlank() && propertyType.isNotBlank()

                Button(
                    onClick = {
                        if (isSubmitted) {
                            onCompleteAssessment()
                        } else {
                            showPaymentPage = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid || isSubmitted,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text(
                        text = if (isSubmitted) "Return to Home" else "Proceed to Payment (RM 50.00)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LegaEligibilityScreenPreview() {
    LegaEligibilityScreen(userIc = "123456789012")
}
