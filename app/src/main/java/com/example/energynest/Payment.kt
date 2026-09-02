package com.example.energynest

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Data Class
data class PaymentMethodType(
    val name: String,
    val icon: Painter,
    val color: Color
)

// Main Screen
@Composable
fun PaymentScreen(
    onBack: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf("main") }
    var selectedMethod by remember { mutableStateOf<PaymentMethodType?>(null) }
    var paymentDateTime by remember { mutableStateOf("") }
    var referenceNo by remember { mutableStateOf("") }

    when (currentScreen) {
        "main" -> PaymentMethodSelectionScreen(
            onBack = onBack,
            onSelectMethod = { method ->
                selectedMethod = method
                currentScreen = "detail"
            }
        )

        "detail" -> {
            when (selectedMethod?.name) {
                "Visa" -> VisaPaymentPage(
                    onBack = { currentScreen = "main" },
                    onPaymentSuccess = {
                        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
                        paymentDateTime = sdf.format(Date())
                        referenceNo = generatePaymentReference()
                        currentScreen = "result"
                    }
                )

                "Mastercard" -> MastercardPaymentPage(
                    onBack = { currentScreen = "main" },
                    onPaymentSuccess = {
                        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
                        paymentDateTime = sdf.format(Date())
                        referenceNo = generatePaymentReference()
                        currentScreen = "result"
                    }
                )

                "Touch 'n Go" -> TnGPaymentPage(
                    onBack = { currentScreen = "main" },
                    onPaymentSuccess = {
                        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
                        paymentDateTime = sdf.format(Date())
                        referenceNo = generatePaymentReference()
                        currentScreen = "result"
                    }
                )

                else -> {}
            }
        }

        "result" -> PaymentResultScreen(
            paymentMethod = selectedMethod?.name ?: "",
            paymentDateTime = paymentDateTime,
            referenceNo = referenceNo,
            onDone = {
                currentScreen = "main"
                onPaymentSuccess()
            }
        )
    }
}


// Format Card Number
fun formatCardNumber(value: TextFieldValue): TextFieldValue {
    val digitsBeforeCursor = value.text
        .take(value.selection.start)
        .count { it.isDigit() }

    val cleaned = value.text
        .filter { it.isDigit() }
        .take(16)

    val formatted = cleaned.chunked(4).joinToString(" ")

    var newCursorPosition = 0
    var digitCount = 0

    for (i in formatted.indices) {
        if (formatted[i].isDigit()) {
            digitCount++
        }

        if (digitCount == digitsBeforeCursor) {
            newCursorPosition = i + 1
            break
        }
    }

    if (digitsBeforeCursor == 0) {
        newCursorPosition = 0
    }

    if (digitsBeforeCursor >= cleaned.length) {
        newCursorPosition = formatted.length
    }

    return TextFieldValue(
        text = formatted,
        selection = TextRange(newCursorPosition)
    )
}

// Format Expiry Date
fun formatExpiryDate(value: TextFieldValue): TextFieldValue {
    val digitsBeforeCursor = value.text
        .take(value.selection.start)
        .count { it.isDigit() }

    val cleaned = value.text
        .filter { it.isDigit() }
        .take(4)

    val formatted = when {
        cleaned.length > 2 -> "${cleaned.take(2)}/${cleaned.drop(2)}"
        else -> cleaned
    }

    var newCursorPosition = 0
    var digitCount = 0

    for (i in formatted.indices) {
        if (formatted[i].isDigit()) {
            digitCount++
        }

        if (digitCount == digitsBeforeCursor) {
            newCursorPosition = i + 1

            if (i + 1 < formatted.length && formatted[i + 1] == '/') {
                newCursorPosition++
            }

            break
        }
    }

    if (digitsBeforeCursor == 0) {
        newCursorPosition = 0
    }

    if (digitsBeforeCursor >= cleaned.length) {
        newCursorPosition = formatted.length
    }

    return TextFieldValue(
        text = formatted,
        selection = TextRange(newCursorPosition.coerceIn(0, formatted.length))
    )
}

// Generate Payment Reference
fun generatePaymentReference(): String {
    val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
    val randomNumber = (100..999).random()
    return "$timestamp$randomNumber"
}

// Receipt Row
@Composable
fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

// Payment Result Screen
@Composable
fun PaymentResultScreen(
    paymentMethod: String = "Visa",
    paymentDateTime: String = "",
    referenceNo: String = "",
    onDone: () -> Unit = {}
) {
    val currentDate = paymentDateTime.ifEmpty {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
    }

    val refNo = referenceNo.ifEmpty { generatePaymentReference() }

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
            Box(modifier = Modifier.size(40.dp))
            Text("Payment Receipt", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(40.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Payment Successful!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Your payment has been completed",
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
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "EnergyNest",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                "Payment Receipt",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                "PAID",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    ReceiptRow("Reference No", refNo)
                    ReceiptRow("Payment Method", paymentMethod)
                    ReceiptRow("Date & Time", currentDate)
                    ReceiptRow("Status", "Completed", valueColor = Color(0xFF4CAF50))

                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total Amount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            "RM 150.00",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Done",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// Animated Credit Card
@Composable
fun AnimatedCreditCard(
    isFlipped: Boolean,
    cardNumber: String,
    cardHolderName: String,
    cardExpiry: String,
    cardCvv: String,
    cardColor: Color = Color(0xFF1A237E),
    cardLogo: Painter = painterResource(id = R.drawable.visa_white),
    onCardClick: () -> Unit = {}
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 4.dp)
            .graphicsLayer {
                cameraDistance = 8f * 100
                rotationY = rotation
            }
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCardClick() }
    ) {
        if (rotation <= 90f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(cardColor, cardColor.copy(alpha = 0.7f))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                "EnergyNest",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                "PREMIUM",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Icon(
                            painter = cardLogo,
                            contentDescription = "Card Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Column {
                        Text(
                            if (cardNumber.isNotEmpty()) cardNumber else "••••  ••••  ••••  ••••",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "CARD HOLDER",
                                    fontSize = 8.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    if (cardHolderName.isNotEmpty()) cardHolderName else "JOHN SMITH",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            Column {
                                Text(
                                    "EXPIRY",
                                    fontSize = 8.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    if (cardExpiry.isNotEmpty()) cardExpiry else "MM/YY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Text(
                        if (cardColor == Color(0xFF1A237E)) "VISA" else "MASTERCARD",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    }
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(cardColor, cardColor.copy(alpha = 0.7f))
                        ),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color.Black.copy(alpha = 0.7f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            "CVV",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(Color.White)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                if (cardCvv.isNotEmpty()) cardCvv else "•••",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            if (cardColor == Color(0xFF1A237E)) "VISA" else "MASTERCARD",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

// Payment Method Selection Screen
@Composable
fun PaymentMethodSelectionScreen(
    onBack: () -> Unit = {},
    onSelectMethod: (PaymentMethodType) -> Unit
) {
    val paymentMethods = listOf(
        PaymentMethodType(
            name = "Visa",
            icon = painterResource(id = R.drawable.visa),
            color = Color(0xFF1A237E)
        ),
        PaymentMethodType(
            name = "Mastercard",
            icon = painterResource(id = R.drawable.mastercard),
            color = Color(0xFFE65100)
        ),
        PaymentMethodType(
            name = "Touch 'n Go",
            icon = painterResource(id = R.drawable.ewallet),
            color = Color(0xFF00A651)
        )
    )

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
            IconButton(
                onClick = { onBack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Back",
                    tint = Color(0xFF00B87C),
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                "Payment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.size(40.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Amount", fontSize = 13.sp, color = Color.Gray)
                        Text("RM 150.00", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00B87C))
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            "Jan 2026",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Select Payment Method",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    method = method,
                    onClick = { onSelectMethod(method) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Secure payment encrypted",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}


// Payment Method Card
@Composable
fun PaymentMethodCard(
    method: PaymentMethodType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(method.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = method.icon,
                    contentDescription = method.name,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                method.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                Icons.Default.ChevronRight,
                "Select",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}


// Visa Payment Page
@Composable
fun VisaPaymentPage(
    onBack: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var cardNumber by remember { mutableStateOf(TextFieldValue("")) }
    var cardHolderName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf(TextFieldValue("")) }
    var cardCvv by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var isCardFlipped by remember { mutableStateOf(false) }
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var cardHolderError by remember { mutableStateOf<String?>(null) }
    var expiryError by remember { mutableStateOf<String?>(null) }
    var cvvError by remember { mutableStateOf<String?>(null) }
    var showToastMessage by remember { mutableStateOf<String?>(null) }

    fun validateCardNumber(value: String): String? {
        val clean = value.replace(" ", "")
        return when {
            clean.isEmpty() -> "Card number is required"
            clean.length < 16 -> "Enter 16 digits"
            clean.length > 16 -> "Maximum 16 digits"
            else -> null
        }
    }

    fun validateCardHolder(value: String): String? {
        val clean = value.trim()
        return when {
            clean.isEmpty() -> "Card holder name is required"
            !clean.all { it.isLetter() || it.isWhitespace() } -> "Only letters allowed"
            clean.length < 2 -> "Name is too short"
            else -> null
        }
    }

    fun validateExpiry(value: String): String? {
        return when {
            value.isEmpty() -> "Expiry date is required"
            value.length < 5 -> "Enter MM/YY"
            else -> {
                val parts = value.split("/")
                if (parts.size == 2) {
                    val month = parts[0].toIntOrNull()
                    val year = parts[1].toIntOrNull()
                    when {
                        month == null || year == null -> "Invalid format"
                        month !in 1..12 -> "Invalid month"
                        else -> null
                    }
                } else {
                    null
                }
            }
        }
    }

    fun validateCvv(value: String): String? {
        return when {
            value.isEmpty() -> "CVV is required"
            value.length < 3 -> "Enter 3 digits"
            value.length > 3 -> "Maximum 3 digits"
            else -> null
        }
    }

    showToastMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        showToastMessage = null
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
            IconButton(
                onClick = { onBack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Back",
                    tint = Color(0xFF1A237E),
                    modifier = Modifier.size(28.dp)
                )
            }

            Text("Visa", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Box(modifier = Modifier.size(40.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedCreditCard(
                isFlipped = isCardFlipped,
                cardNumber = cardNumber.text,
                cardHolderName = cardHolderName,
                cardExpiry = cardExpiry.text,
                cardCvv = cardCvv,
                cardColor = Color(0xFF1A237E),
                cardLogo = painterResource(id = R.drawable.visa_white),
                onCardClick = { isCardFlipped = !isCardFlipped }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Card Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A237E)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { newValue ->
                            cardNumber = formatCardNumber(newValue)
                            cardNumberError = validateCardNumber(cardNumber.text)
                        },
                        label = { Text("Card Number") },
                        placeholder = { Text("1234 5678 9012 3456") },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        isError = cardNumberError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = {
                            if (cardNumberError != null) {
                                Text(cardNumberError!!, fontSize = 11.sp, color = Color.Red)
                            } else {
                                Text("${cardNumber.text.replace(" ", "").length}/16 digits", fontSize = 11.sp, color = Color.Gray)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (cardNumberError != null) Color.Red else Color(0xFF1A237E),
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = if (cardNumberError != null) Color.Red else Color(0xFF1A237E),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color(0xFF1A237E)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cardHolderName,
                        onValueChange = {
                            cardHolderName = it
                            cardHolderError = validateCardHolder(it)
                        },
                        label = { Text("Card Holder Name") },
                        placeholder = { Text("John Smith") },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        isError = cardHolderError != null,
                        supportingText = {
                            if (cardHolderError != null) {
                                Text(cardHolderError!!, fontSize = 11.sp, color = Color.Red)
                            } else {
                                Text("Letters only", fontSize = 11.sp, color = Color.Gray)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (cardHolderError != null) Color.Red else Color(0xFF1A237E),
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = if (cardHolderError != null) Color.Red else Color(0xFF1A237E),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color(0xFF1A237E)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { newValue ->
                                cardExpiry = formatExpiryDate(newValue)
                                expiryError = validateExpiry(cardExpiry.text)
                            },
                            label = { Text("Expiry") },
                            placeholder = { Text("MM/YY") },
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            isError = expiryError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = {
                                if (expiryError != null) {
                                    Text(expiryError!!, fontSize = 11.sp, color = Color.Red)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (expiryError != null) Color.Red else Color(0xFF1A237E),
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = if (expiryError != null) Color.Red else Color(0xFF1A237E),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF1A237E)
                            )
                        )

                        OutlinedTextField(
                            value = cardCvv,
                            onValueChange = {
                                if (it.length <= 3) {
                                    cardCvv = it.filter { char -> char.isDigit() }
                                    cvvError = validateCvv(cardCvv)
                                    if (it.length == 3) {
                                        isCardFlipped = true
                                    }
                                }
                            },
                            label = { Text("CVV") },
                            placeholder = { Text("123") },
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            isError = cvvError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = {
                                if (cvvError != null) {
                                    Text(cvvError!!, fontSize = 11.sp, color = Color.Red)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (cvvError != null) Color.Red else Color(0xFF1A237E),
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = if (cvvError != null) Color.Red else Color(0xFF1A237E),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF1A237E)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Secure payment", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val isFormValid =
                cardNumberError == null &&
                        cardHolderError == null &&
                        expiryError == null &&
                        cvvError == null &&
                        cardNumber.text.replace(" ", "").length == 16 &&
                        cardHolderName.isNotBlank() &&
                        cardExpiry.text.length == 5 &&
                        cardCvv.length == 3

            Button(
                onClick = {
                    if (isFormValid) {
                        isProcessing = true
                        coroutineScope.launch {
                            delay(1500)
                            isProcessing = false
                            onPaymentSuccess()
                        }
                    } else {
                        when {
                            cardNumber.text.replace(" ", "").isEmpty() -> {
                                showToastMessage = "Please fill in your card number"
                                cardNumberError = "Card number is required"
                            }
                            cardNumber.text.replace(" ", "").length < 16 -> {
                                showToastMessage = "Please enter a valid 16-digit card number"
                                cardNumberError = "Enter 16 digits"
                            }
                            cardHolderName.isBlank() -> {
                                showToastMessage = "Please fill in your card holder name"
                                cardHolderError = "Card holder name is required"
                            }
                            cardExpiry.text.isEmpty() -> {
                                showToastMessage = "Please fill in your card expiry date"
                                expiryError = "Expiry date is required"
                            }
                            cardExpiry.text.length < 5 -> {
                                showToastMessage = "Please enter a valid expiry date (MM/YY)"
                                expiryError = "Enter MM/YY"
                            }
                            cardCvv.isEmpty() -> {
                                showToastMessage = "Please fill in your CVV"
                                cvvError = "CVV is required"
                            }
                            cardCvv.length < 3 -> {
                                showToastMessage = "Please enter a valid 3-digit CVV"
                                cvvError = "Enter 3 digits"
                            }
                            else -> {
                                showToastMessage = "Please fill in all card details"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFF1A237E) else Color.Gray,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Pay Now",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Secure payment encrypted",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}


// Mastercard Payment Page
@Composable
fun MastercardPaymentPage(
    onBack: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var cardNumber by remember { mutableStateOf(TextFieldValue("")) }
    var cardHolderName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf(TextFieldValue("")) }
    var cardCvv by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var isCardFlipped by remember { mutableStateOf(false) }
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var cardHolderError by remember { mutableStateOf<String?>(null) }
    var expiryError by remember { mutableStateOf<String?>(null) }
    var cvvError by remember { mutableStateOf<String?>(null) }
    var showToastMessage by remember { mutableStateOf<String?>(null) }

    fun validateCardNumber(value: String): String? {
        val clean = value.replace(" ", "")
        return when {
            clean.isEmpty() -> "Card number is required"
            clean.length < 16 -> "Enter 16 digits"
            clean.length > 16 -> "Maximum 16 digits"
            else -> null
        }
    }

    fun validateCardHolder(value: String): String? {
        val clean = value.trim()
        return when {
            clean.isEmpty() -> "Card holder name is required"
            !clean.all { it.isLetter() || it.isWhitespace() } -> "Only letters allowed"
            clean.length < 2 -> "Name is too short"
            else -> null
        }
    }

    fun validateExpiry(value: String): String? {
        return when {
            value.isEmpty() -> "Expiry date is required"
            value.length < 5 -> "Enter MM/YY"
            else -> {
                val parts = value.split("/")
                if (parts.size == 2) {
                    val month = parts[0].toIntOrNull()
                    val year = parts[1].toIntOrNull()
                    when {
                        month == null || year == null -> "Invalid format"
                        month !in 1..12 -> "Invalid month"
                        else -> null
                    }
                } else {
                    null
                }
            }
        }
    }

    fun validateCvv(value: String): String? {
        return when {
            value.isEmpty() -> "CVV is required"
            value.length < 3 -> "Enter 3 digits"
            value.length > 3 -> "Maximum 3 digits"
            else -> null
        }
    }

    showToastMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        showToastMessage = null
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
            IconButton(
                onClick = { onBack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Back",
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(28.dp)
                )
            }

            Text("Mastercard", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Box(modifier = Modifier.size(40.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedCreditCard(
                isFlipped = isCardFlipped,
                cardNumber = cardNumber.text,
                cardHolderName = cardHolderName,
                cardExpiry = cardExpiry.text,
                cardCvv = cardCvv,
                cardColor = Color(0xFFE65100),
                cardLogo = painterResource(id = R.drawable.mastercard),
                onCardClick = { isCardFlipped = !isCardFlipped }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Card Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE65100)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { newValue ->
                            cardNumber = formatCardNumber(newValue)
                            cardNumberError = validateCardNumber(cardNumber.text)
                        },
                        label = { Text("Card Number") },
                        placeholder = { Text("1234 5678 9012 3456") },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        isError = cardNumberError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = {
                            if (cardNumberError != null) {
                                Text(cardNumberError!!, fontSize = 11.sp, color = Color.Red)
                            } else {
                                Text("${cardNumber.text.replace(" ", "").length}/16 digits", fontSize = 11.sp, color = Color.Gray)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (cardNumberError != null) Color.Red else Color(0xFFE65100),
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = if (cardNumberError != null) Color.Red else Color(0xFFE65100),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color(0xFFE65100)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cardHolderName,
                        onValueChange = {
                            cardHolderName = it
                            cardHolderError = validateCardHolder(it)
                        },
                        label = { Text("Card Holder Name") },
                        placeholder = { Text("John Smith") },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        isError = cardHolderError != null,
                        supportingText = {
                            if (cardHolderError != null) {
                                Text(cardHolderError!!, fontSize = 11.sp, color = Color.Red)
                            } else {
                                Text("Letters only", fontSize = 11.sp, color = Color.Gray)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (cardHolderError != null) Color.Red else Color(0xFFE65100),
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = if (cardHolderError != null) Color.Red else Color(0xFFE65100),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color(0xFFE65100)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { newValue ->
                                cardExpiry = formatExpiryDate(newValue)
                                expiryError = validateExpiry(cardExpiry.text)
                            },
                            label = { Text("Expiry") },
                            placeholder = { Text("MM/YY") },
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            isError = expiryError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = {
                                if (expiryError != null) {
                                    Text(expiryError!!, fontSize = 11.sp, color = Color.Red)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (expiryError != null) Color.Red else Color(0xFFE65100),
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = if (expiryError != null) Color.Red else Color(0xFFE65100),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFFE65100)
                            )
                        )

                        OutlinedTextField(
                            value = cardCvv,
                            onValueChange = {
                                if (it.length <= 3) {
                                    cardCvv = it.filter { char -> char.isDigit() }
                                    cvvError = validateCvv(cardCvv)
                                    if (it.length == 3) {
                                        isCardFlipped = true
                                    }
                                }
                            },
                            label = { Text("CVV") },
                            placeholder = { Text("123") },
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            isError = cvvError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = {
                                if (cvvError != null) {
                                    Text(cvvError!!, fontSize = 11.sp, color = Color.Red)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (cvvError != null) Color.Red else Color(0xFFE65100),
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = if (cvvError != null) Color.Red else Color(0xFFE65100),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFFE65100)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Secure payment", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val isFormValid =
                cardNumberError == null &&
                        cardHolderError == null &&
                        expiryError == null &&
                        cvvError == null &&
                        cardNumber.text.replace(" ", "").length == 16 &&
                        cardHolderName.isNotBlank() &&
                        cardExpiry.text.length == 5 &&
                        cardCvv.length == 3

            Button(
                onClick = {
                    if (isFormValid) {
                        isProcessing = true
                        coroutineScope.launch {
                            delay(1500)
                            isProcessing = false
                            onPaymentSuccess()
                        }
                    } else {
                        when {
                            cardNumber.text.replace(" ", "").isEmpty() -> {
                                showToastMessage = "Please fill in your card number"
                                cardNumberError = "Card number is required"
                            }
                            cardNumber.text.replace(" ", "").length < 16 -> {
                                showToastMessage = "Please enter a valid 16-digit card number"
                                cardNumberError = "Enter 16 digits"
                            }
                            cardHolderName.isBlank() -> {
                                showToastMessage = "Please fill in your card holder name"
                                cardHolderError = "Card holder name is required"
                            }
                            cardExpiry.text.isEmpty() -> {
                                showToastMessage = "Please fill in your card expiry date"
                                expiryError = "Expiry date is required"
                            }
                            cardExpiry.text.length < 5 -> {
                                showToastMessage = "Please enter a valid expiry date (MM/YY)"
                                expiryError = "Enter MM/YY"
                            }
                            cardCvv.isEmpty() -> {
                                showToastMessage = "Please fill in your CVV"
                                cvvError = "CVV is required"
                            }
                            cardCvv.length < 3 -> {
                                showToastMessage = "Please enter a valid 3-digit CVV"
                                cvvError = "Enter 3 digits"
                            }
                            else -> {
                                showToastMessage = "Please fill in all card details"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFFE65100) else Color.Gray,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Pay Now",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Secure payment encrypted",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}


// Touch 'n Go Payment Page
@Composable
fun TnGPaymentPage(
    onBack: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val pinFocusRequester = remember { FocusRequester() }
    var isProcessing by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var pinError by remember { mutableStateOf<String?>(null) }
    var showToastMessage by remember { mutableStateOf<String?>(null) }

    fun validatePhone(value: String): String? {
        val clean = value.replace(" ", "")
        return when {
            clean.isEmpty() -> "Phone number is required"
            clean.length < 9 -> "Enter valid phone number"
            else -> null
        }
    }

    fun validatePin(value: String): String? {
        return when {
            value.isEmpty() -> "PIN is required"
            value.length < 6 -> "Enter 6 digits"
            else -> null
        }
    }

    showToastMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        showToastMessage = null
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
            IconButton(
                onClick = { onBack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Back",
                    tint = Color(0xFF00A651),
                    modifier = Modifier.size(28.dp)
                )
            }

            Text("Touch 'n Go", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Box(modifier = Modifier.size(40.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00A651).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ewallet),
                    contentDescription = "TnG",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Touch 'n Go eWallet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00A651)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Enter your eWallet details",
                fontSize = 13.sp,
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
                        .padding(16.dp)
                ) {
                    Text(
                        "eWallet Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00A651)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "+60",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00A651),
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                if (it.length <= 10) {
                                    phone = it.filter { char -> char.isDigit() }
                                    phoneError = validatePhone(phone)
                                }
                            },
                            label = { Text("Mobile Number") },
                            placeholder = { Text("12 345 6789") },
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            isError = phoneError != null,
                            supportingText = {
                                if (phoneError != null) {
                                    Text(phoneError!!, fontSize = 11.sp, color = Color.Red)
                                } else {
                                    Text("${phone.length}/10 digits", fontSize = 11.sp, color = Color.Gray)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (phoneError != null) Color.Red else Color(0xFF00A651),
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = if (phoneError != null) Color.Red else Color(0xFF00A651),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF00A651)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "Enter 6-Digit PIN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                pinFocusRequester.requestFocus()
                            },
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (index in 0 until 6) {
                            val digit = if (index < pin.length) pin[index].toString() else ""

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        if (pinError != null) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = if (pinError != null) 2.dp else 1.dp,
                                        color = if (pinError != null) Color.Red else if (index == pin.length && pin.length < 6) Color(0xFF00A651) else Color.LightGray,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (digit.isNotEmpty()) "●" else "",
                                    fontSize = 28.sp,
                                    color = Color(0xFF00A651)
                                )
                            }
                        }
                    }

                    BasicTextField(
                        value = pin,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() }.take(6)
                            pin = filtered
                            pinError = validatePin(filtered)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.dp)
                            .focusRequester(pinFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        textStyle = TextStyle(fontSize = 0.sp, color = Color.Transparent),
                        decorationBox = { innerTextField -> innerTextField() }
                    )

                    if (pinError != null) {
                        Text(
                            pinError!!,
                            fontSize = 11.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Text(
                            "${pin.length}/6 digits",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Secured with TnG eWallet",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val isPhoneValid = phoneError == null && phone.length >= 9
            val isPinValid = pinError == null && pin.length == 6
            val isFormValid = isPhoneValid && isPinValid

            Button(
                onClick = {
                    if (isFormValid) {
                        isProcessing = true
                        coroutineScope.launch {
                            delay(1500)
                            isProcessing = false
                            onPaymentSuccess()
                        }
                    } else {
                        when {
                            phone.isEmpty() -> {
                                showToastMessage = "Please fill in your mobile number"
                                phoneError = "Phone number is required"
                            }
                            phone.length < 9 -> {
                                showToastMessage = "Please enter a valid phone number"
                                phoneError = "Enter valid phone number"
                            }
                            pin.isEmpty() -> {
                                showToastMessage = "Please fill in your 6-digit PIN"
                                pinError = "PIN is required"
                            }
                            pin.length < 6 -> {
                                showToastMessage = "Please enter a valid 6-digit PIN"
                                pinError = "Enter 6 digits"
                            }
                            else -> {
                                showToastMessage = "Please fill in all eWallet details"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFF00A651) else Color.Gray,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Pay Now",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Secure payment encrypted",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewPaymentScreen() {
    MaterialTheme {
        PaymentScreen()
    }
}