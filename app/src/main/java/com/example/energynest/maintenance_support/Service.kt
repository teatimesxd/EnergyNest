package com.example.energynest.maintenance_support

import com.example.energynest.backend_models.SupabaseClient
import com.example.energynest.backend_models.ServiceData
import com.example.energynest.backend_models.BookingData
import com.example.energynest.backend_models.PaymentData
import com.example.energynest.sell_payment.VisaPaymentPage
import com.example.energynest.sell_payment.MastercardPaymentPage
import com.example.energynest.sell_payment.TnGPaymentPage
import com.example.energynest.sell_payment.PaymentMethodType
import com.example.energynest.R
import com.example.energynest.ui.theme.Background
import com.example.energynest.ui.theme.TextDark
import com.example.energynest.ui.theme.TextGray
import com.example.energynest.ui.theme.BrandGreenColour
import com.example.energynest.ui.theme.White
import com.example.energynest.ui.theme.IconBg
import com.example.energynest.ui.theme.BorderLight

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
// Old SupabaseClient import removed
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.text.SimpleDateFormat

// Consolidated colors in ui.theme.Color.kt

// SST rate used across the app: 6% of the subtotal.
// Kept identical to the rate used in PaymentHistoryScreen.kt so the amount
// shown at checkout always matches the amount shown later in Payment History.
private const val SST_RATE = 0.06

private enum class ServicePage {
    HOME,
    CUSTOMER_SERVICE,
    CONSULTATION,
    MAINTENANCE,
    CLEANING,
    FAQ
}

private data class FAQData(
    val question: String,
    val answer: String
)

@Composable
fun ServicesScreen(
    userIc: String,
    onOpenDrawer: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onProfileClick: () -> Unit = onOpenProfile
) {
    val coroutineScope = rememberCoroutineScope()
    var currentPage by remember {
        mutableStateOf(ServicePage.HOME)
    }

    val handleProfileClick = {
        onOpenProfile()
        onProfileClick()
    }

    when (currentPage) {

        ServicePage.HOME -> ServicesHome(
            onOpenDrawer = onOpenDrawer,
            onOpenProfile = handleProfileClick,
            onOpenPage = {
                currentPage = it
            }
        )

        ServicePage.CUSTOMER_SERVICE -> CustomerServicePage(
            onBack = {
                currentPage = ServicePage.HOME
            },
            onOpenProfile = handleProfileClick
        )

        ServicePage.CONSULTATION -> ConsultationPage(
            onBack = {
                currentPage = ServicePage.HOME
            },
            onOpenProfile = handleProfileClick,
            coroutineScope = coroutineScope,
            userIc = userIc
        )

        ServicePage.MAINTENANCE -> MaintenancePage(
            onBack = {
                currentPage = ServicePage.HOME
            },
            onOpenProfile = handleProfileClick,
            coroutineScope = coroutineScope,
            userIc = userIc
        )

        ServicePage.CLEANING -> CleaningPage(
            onBack = {
                currentPage = ServicePage.HOME
            },
            onOpenProfile = handleProfileClick,
            coroutineScope = coroutineScope,
            userIc = userIc
        )

        ServicePage.FAQ -> FAQPage(
            onBack = {
                currentPage = ServicePage.HOME
            },
            onOpenProfile = handleProfileClick
        )
    }
}

@Composable
private fun ServicesHome(
    onOpenDrawer: () -> Unit,
    onOpenProfile: () -> Unit = {},
    onOpenPage: (ServicePage) -> Unit
) {
    val faqs = remember {
        listOf(
            FAQData(
                "How does the 100kWh storage scale work?",
                "The system uses a 100kWh base for easy monitoring. If your display shows 4.56kWh, it represents exactly 4% of your total capacity."
            ),
            FAQData(
                "What is Auto-Sell and how does it trigger?",
                "Auto-Sell automatically sells excess energy to the grid when your battery exceeds 80kWh (80%), helping you earn credits without manual input."
            ),
            FAQData(
                "Is my LEGA Roof Assessment deposit refundable?",
                "Yes. The RM 50.00 deposit is fully refundable if your property is found to be ineligible for solar installation after our professional evaluation."
            ),
            FAQData(
                "How do I sell energy back to the grid manually?",
                "Go to the Smart Sell page, use the slider to select an amount, and tap 'Discharge Now'. Your battery will be deducted and credits added instantly."
            ),
            FAQData(
                "How can I download a receipt for my payment?",
                "Open Payment History from the menu, tap on any successful transaction, and use the 'Download Receipt' button to generate a professional PDF."
            ),
            FAQData(
                "What should I do if my home stats aren't updating?",
                "You can tap the Refresh icon next to your name on the Home screen to force a real-time sync with the database."
            ),
            FAQData(
                "Why can't I withdraw my full credit balance?",
                "Ensure you have selected a bank and entered exactly 16 digits. The system also uses high-precision rounding to ensure you can withdraw every cent."
            ),
            FAQData(
                "What is the CREAM Roof yield evaluation?",
                "It is a professional assessment where we analyze your roof space and shading levels to estimate how much solar income you can generate."
            ),
            FAQData(
                "How long does it take for a maintenance booking to be confirmed?",
                "Once you pay the fee, the status is set to 'Confirmed'. A technician will typically call you within 24 hours to finalize the arrival time."
            ),
            FAQData(
                "Can I update my house address after registration?",
                "Yes. Go to your Profile and update your address. The new address will be automatically used for all future LEGA assessments and service bookings."
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        item {
            TopBar(
                title = "Services",
                onOpenDrawer = onOpenDrawer,
                onProfileClick = onOpenProfile
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ServiceCard(
                        title = "Customer Service",
                        description = "Get help with your account, billing inquiries, and general support.",
                        buttonText = "CONTACT US",
                        iconRes = R.drawable.headset_icon,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onOpenPage(ServicePage.CUSTOMER_SERVICE)
                        }
                    )

                    ServiceCard(
                        title = "Consultation",
                        description = "Schedule a session with our energy experts for your home.",
                        buttonText = "BOOK SESSION",
                        iconRes = R.drawable.handyman_icon,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onOpenPage(ServicePage.CONSULTATION)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ServiceCard(
                        title = "Maintenance",
                        description = "Regular check-ups for your solar panels and battery storage.",
                        buttonText = "SCHEDULE CHECK",
                        iconRes = R.drawable.home_repair_service_icon,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onOpenPage(ServicePage.MAINTENANCE)
                        }
                    )

                    ServiceCard(
                        title = "Cleaning",
                        description = "Professional cleaning for solar panels to maintain performance.",
                        buttonText = "BOOK CLEANING",
                        iconRes = R.drawable.cleaning_services_icon,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onOpenPage(ServicePage.CLEANING)
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "Common Questions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }

        items(faqs.take(5)) { faq ->

            Box(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
            ) {
                FAQItem(faq)
            }
        }

        item {

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        onOpenPage(ServicePage.FAQ)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        BrandGreenColour
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandGreenColour,
                        containerColor = White
                    )
                ) {
                    Text(
                        text = "View All FAQs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    onOpenDrawer: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    onProfileClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                IconButton(
                    onClick = {
                        if (onBack != null) {
                            onBack()
                        } else {
                            onOpenDrawer?.invoke()
                        }
                    }
                ) {

                    Icon(
                        painter = painterResource(
                            id = if (onBack != null) {
                                R.drawable.back_arrow
                            } else {
                                R.drawable.sidebar_icon
                            }
                        ),
                        contentDescription = if (onBack != null) {
                            "Back"
                        } else {
                            "Sidebar"
                        },
                        tint = TextDark
                    )
                }

                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onProfileClick
            ) {

                Icon(
                    painter = painterResource(
                        id = R.drawable.profile_icon
                    ),
                    contentDescription = "Profile",
                    tint = TextDark
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = BorderLight
        )
    }
}

@Composable
private fun ServiceCard(
    title: String,
    description: String,
    buttonText: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        border = BorderStroke(
            1.dp,
            BorderLight
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(IconBg),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        tint = BrandGreenColour,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = description,
                fontSize = 12.sp,
                color = TextGray,
                lineHeight = 16.sp,
                minLines = 3,
                maxLines = 3
            )

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreenColour,
                    contentColor = White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 0.dp
                )
            ) {

                Text(
                    text = buttonText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ServiceHeader(
    @DrawableRes iconRes: Int,
    title: String,
    description: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(IconBg),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = BrandGreenColour,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                description,
                fontSize = 13.sp,
                color = TextGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(
    text: String
) {

    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
    )
}

@Composable
private fun InfoRow(
    @DrawableRes iconRes: Int,
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(IconBg),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = BrandGreenColour,
                modifier = Modifier.size(19.dp)
            )
        }

        Column {

            Text(
                title,
                fontSize = 12.sp,
                color = TextGray
            )

            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        placeholder = {
            Text(placeholder)
        },
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandGreenColour,
            focusedLabelColor = BrandGreenColour,
            cursorColor = BrandGreenColour,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        )
    )
}

@Composable
private fun DateTimePickerField(
    value: String,
    label: String,
    placeholder: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                onClick()
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                White
            } else {
                Color.LightGray.copy(alpha = 0.3f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (enabled) {
                BorderLight
            } else {
                Color.LightGray
            }
        ),
        elevation = CardDefaults.cardElevation(0.0.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = if (enabled) {
                        TextGray
                    } else {
                        Color.LightGray
                    }
                )

                Text(
                    text = if (value.isNotEmpty()) {
                        value
                    } else {
                        placeholder
                    },
                    fontSize = 14.sp,
                    color = if (value.isNotEmpty()) {
                        TextDark
                    } else {
                        TextGray.copy(alpha = 0.6f)
                    }
                )
            }

            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "Select $label",
                tint = if (enabled) {
                    BrandGreenColour
                } else {
                    Color.LightGray
                }
            )
        }
    }
}

private fun getTodayMillis(): Long {

    val today = LocalDate.now()

    return today
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    val todayMillis = remember {
        getTodayMillis()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayMillis,
        selectableDates = object : SelectableDates {

            override fun isSelectableDate(
                utcTimeMillis: Long
            ): Boolean {

                return utcTimeMillis >= todayMillis
            }

            override fun isSelectableYear(
                year: Int
            ): Boolean {

                val currentYear = LocalDate.now().year

                return year >= currentYear
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,

        confirmButton = {

            TextButton(
                onClick = {

                    val selectedMillis =
                        datePickerState.selectedDateMillis

                    if (
                        selectedMillis != null &&
                        selectedMillis >= todayMillis
                    ) {

                        val selectedDate =
                            Instant
                                .ofEpochMilli(selectedMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                        if (!selectedDate.isBefore(LocalDate.now())) {

                            val formatter =
                                DateTimeFormatter.ofPattern(
                                    "dd MMM yyyy",
                                    Locale.US
                                )

                            onDateSelected(
                                selectedDate.format(formatter)
                            )

                            onDismiss()
                        }
                    }
                }
            ) {

                Text(
                    text = "SELECT",
                    color = BrandGreenColour,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "CANCEL",
                    color = TextGray
                )
            }
        }
    ) {

        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = {

                Text(
                    text = "Select Date",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 16.dp
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}

@Composable
private fun ServiceTimePickerDialog(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    val timeSlots = listOf(
        "09:00 AM",
        "09:30 AM",
        "10:00 AM",
        "10:30 AM",
        "11:00 AM",
        "11:30 AM",
        "12:00 PM",
        "12:30 PM",
        "01:00 PM",
        "01:30 PM",
        "02:00 PM",
        "02:30 PM",
        "03:00 PM",
        "03:30 PM",
        "04:00 PM",
        "04:30 PM",
        "05:00 PM"
    )

    var selectedTime by remember {
        mutableStateOf<String?>(null)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Select Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(
                        bottom = 4.dp
                    )
                )

                Text(
                    text = "Available from 9:00 AM to 5:00 PM",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.padding(
                        bottom = 12.dp
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {

                    items(timeSlots) { time ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    selectedTime = time
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    if (selectedTime == time) {
                                        BrandGreenColour.copy(
                                            alpha = 0.10f
                                        )
                                    } else {
                                        White
                                    }
                            ),
                            border =
                                if (selectedTime == time) {
                                    BorderStroke(
                                        1.dp,
                                        BrandGreenColour
                                    )
                                } else {
                                    BorderStroke(
                                        1.dp,
                                        BorderLight
                                    )
                                },
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                verticalAlignment =
                                    Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.Center
                            ) {

                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.timer_icon
                                    ),
                                    contentDescription = null,
                                    tint =
                                        if (selectedTime == time) {
                                            BrandGreenColour
                                        } else {
                                            TextGray
                                        },
                                    modifier =
                                        Modifier.size(18.dp)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(10.dp)
                                )

                                Text(
                                    text = time,
                                    fontSize = 14.sp,
                                    color =
                                        if (selectedTime == time) {
                                            BrandGreenColour
                                        } else {
                                            TextDark
                                        },
                                    fontWeight =
                                        if (selectedTime == time) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {

                        Text("CANCEL")
                    }

                    Button(
                        onClick = {

                            selectedTime?.let {

                                onTimeSelected(it)

                                onDismiss()
                            }
                        },
                        enabled = selectedTime != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                BrandGreenColour,
                            contentColor = White,
                            disabledContainerColor =
                                Color.LightGray
                        )
                    ) {

                        Text(
                            text = "SELECT",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubmitButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGreenColour,
            contentColor = White
        )
    ) {

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White, strokeWidth = 2.dp)
        } else {
            Text(
                text,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CustomerServicePage(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {

    var message by remember {
        mutableStateOf("")
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    var showError by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding(),
        contentPadding = PaddingValues(
            bottom = 28.dp
        )
    ) {

        item {

            TopBar(
                title = "Customer Service",
                onBack = onBack,
                onProfileClick = onOpenProfile
            )
        }

        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                border = BorderStroke(
                    1.dp,
                    BorderLight
                ),
                elevation = CardDefaults.cardElevation(0.0.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(15.dp)
                ) {

                    ServiceHeader(
                        R.drawable.headset_icon,
                        "How can we help?",
                        "Our support team is ready to assist with your EnergyNest account."
                    )

                    HorizontalDivider(
                        color = BorderLight
                    )

                    InfoRow(
                        R.drawable.calendar_month_icon,
                        "Support hours",
                        "9:00 AM – 5:00 PM"
                    )

                    SectionTitle(
                        "Send an enquiry"
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = {

                            if (it.length <= 100) {

                                message = it

                                showError = false
                            }
                        },
                        label = {
                            Text("Message")
                        },
                        placeholder = {
                            Text(
                                "Describe your question or issue (max 100 chars)"
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                BrandGreenColour,
                            focusedLabelColor =
                                BrandGreenColour,
                            cursorColor =
                                BrandGreenColour,
                            unfocusedTextColor =
                                Color.Black,
                            focusedTextColor =
                                Color.Black
                        )
                    )

                    Text(
                        text = "${message.length}/100 characters",
                        fontSize = 11.sp,
                        color =
                            if (message.length > 90) {
                                Color.Red
                            } else {
                                TextGray
                            },
                        modifier =
                            Modifier.align(Alignment.End)
                    )

                    if (showError) {

                        Text(
                            text =
                                "Message cannot exceed 100 characters",
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }

                    SubmitButton(
                        text =
                            if (submitted) {
                                "ENQUIRY SENT"
                            } else {
                                "SEND ENQUIRY"
                            },
                        onClick = {

                            if (
                                message.isNotBlank() &&
                                message.length <= 100
                            ) {

                                submitted = true
                                showError = false

                            } else if (
                                message.length > 100
                            ) {

                                showError = true
                            }
                        }
                    )

                    if (submitted) {

                        Text(
                            "Thank you. Your enquiry has been submitted successfully.",
                            fontSize = 13.sp,
                            color = BrandGreenColour,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsultationPage(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {},
    coroutineScope: CoroutineScope,
    userIc: String
) {
    val context = LocalContext.current
    var date by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var isSaving by remember { mutableStateOf(false) }

    ServiceFormPage(
        title = "Consultation",
        iconRes = R.drawable.handyman_icon,
        description =
            "Speak with an energy expert about your home's energy needs.",
        onBack = onBack,
        onOpenProfile = onOpenProfile
    ) {

        DateTimePickerField(
            value = date,
            label = "Preferred Date",
            placeholder = "Select a date",
            iconRes = R.drawable.calendar_month_icon,
            onClick = {
                showDatePicker = true
            },
            enabled = !submitted && !isSaving
        )

        DateTimePickerField(
            value = time,
            label = "Preferred Time",
            placeholder = "Select a time",
            iconRes = R.drawable.timer_icon,
            onClick = {

                if (date.isNotEmpty()) {
                    showTimePicker = true
                }
            },
            enabled = date.isNotEmpty() && !submitted && !isSaving
        )

        SectionTitle(
            "Consultation topics"
        )

        listOf(
            "Energy usage review",
            "Solar system advice",
            "Energy-saving recommendations"
        ).forEach {

            Text(
                "•  $it",
                fontSize = 13.sp,
                color = TextGray
            )
        }

        SubmitButton(
            text =
                if (submitted) {
                    "SESSION REQUESTED"
                } else {
                    "BOOK SESSION"
                },
            isLoading = isSaving,
            enabled = !submitted && date.isNotEmpty() && time.isNotEmpty(),
            onClick = {
                if (userIc.isBlank()) {
                    Toast.makeText(context, "Error: User IC is missing. Please log in.", Toast.LENGTH_LONG).show()
                    return@SubmitButton
                }

                coroutineScope.launch {
                    try {
                        isSaving = true
                        // 1. Create Service Record
                        val service = ServiceData(
                            type = "Consultation",
                            notes = "Energy consultation request",
                            location = "Remote/Online",
                            status = "Pending",
                            isFree = true,
                            paymentId = null
                        )

                        val serviceResult = withContext(Dispatchers.IO) {
                            SupabaseClient.client.from("Service")
                                .insert(service) { select() }
                                .decodeSingle<ServiceData>()
                        }

                        // 2. Create Booking Record
                        val formattedDate = try {
                            val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            outputFormat.format(inputFormat.parse(date)!!)
                        } catch (e: Exception) { date }

                        val formattedTime = try {
                            val inputFormat = SimpleDateFormat("hh:mm a", Locale.US)
                            val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
                            outputFormat.format(inputFormat.parse(time)!!)
                        } catch (e: Exception) { time }

                        val booking = BookingData(
                            icNumber = userIc,
                            serviceId = serviceResult.serviceId!!,
                            date = formattedDate,
                            time = formattedTime
                        )

                        withContext(Dispatchers.IO) {
                            SupabaseClient.client.from("Booking")
                                .insert(booking)
                        }

                        submitted = true
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        isSaving = false
                    }
                }
            }
        )

        if (submitted) {

            Text(
                "Your consultation request has been recorded. We will contact you to confirm the session.",
                fontSize = 13.sp,
                color = BrandGreenColour
            )
        }

        if (showDatePicker) {

            ServiceDatePickerDialog(

                onDateSelected = {
                        selectedDate ->

                    val formatter =
                        DateTimeFormatter.ofPattern(
                            "dd MMM yyyy",
                            Locale.US
                        )

                    val parsedDate =
                        try {
                            LocalDate.parse(
                                selectedDate,
                                formatter
                            )
                        } catch (
                            e: Exception
                        ) {
                            null
                        }

                    if (
                        parsedDate != null &&
                        !parsedDate.isBefore(
                            LocalDate.now()
                        )
                    ) {

                        date = selectedDate
                        time = ""
                    }
                },

                onDismiss = {
                    showDatePicker = false
                }
            )
        }

        if (showTimePicker) {

            ServiceTimePickerDialog(

                onTimeSelected = {
                        selectedTime ->

                    time = selectedTime
                },

                onDismiss = {
                    showTimePicker = false
                }
            )
        }
    }
}


@Composable
private fun MaintenancePage(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {},
    coroutineScope: CoroutineScope,
    userIc: String
) {
    val context = LocalContext.current
    var date by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    var issue by remember {
        mutableStateOf("")
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    // Payment state
    var selectedPaymentMethod by remember {
        mutableStateOf("Touch 'n Go")
    }

    var showPaymentPage by remember {
        mutableStateOf(false)
    }

    var isSaving by remember { mutableStateOf(false) }

    // Fee breakdown: base fee + 6% SST = total charged.
    // This MUST match the calculation used in PaymentHistoryScreen.kt
    // (tax = subtotal * SST_RATE, total = subtotal + tax) so the amount
    // shown here at checkout is identical to the amount shown later in
    // Payment History.
    val maintenanceBaseFee = 50.0
    val maintenanceSst = remember { maintenanceBaseFee * SST_RATE }
    val maintenanceTotal = remember { maintenanceBaseFee + maintenanceSst }

    val paymentMethods = listOf(
        PaymentMethodType(
            "Touch 'n Go",
            painterResource(id = R.drawable.ewallet),
            Color(0xFF00A651)
        ),
        PaymentMethodType(
            "Visa",
            painterResource(id = R.drawable.visa),
            Color(0xFF1A237E)
        ),
        PaymentMethodType(
            "Mastercard",
            painterResource(id = R.drawable.mastercard),
            Color(0xFFE65100)
        )
    )

    if (showPaymentPage) {

        val onPaymentSuccessAction = {
            coroutineScope.launch {
                try {
                    isSaving = true
                    val now = Date()
                    val dateStrLocal = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
                    val timeStrLocal = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)

                    // 1. Create Payment Record
                    // subtotal/sst/amount now match what Payment History will show.
                    val payment = PaymentData(
                        title = "Solar Maintenance Fee",
                        referenceNo = UUID.randomUUID().toString(),
                        method = selectedPaymentMethod,
                        date = dateStrLocal,
                        time = timeStrLocal,
                        subtotal = maintenanceBaseFee,
                        sst = maintenanceSst,
                        amount = maintenanceTotal,
                        status = true
                    )

                    val paymentResult = withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Payment")
                            .insert(payment) { select() }
                            .decodeSingle<PaymentData>()
                    }

                    // 2. Create Service Record
                    val service = ServiceData(
                        paymentId = paymentResult.paymentId,
                        type = "Maintenance",
                        notes = issue,
                        location = "User Registered Address",
                        status = "Confirmed",
                        isFree = false
                    )

                    val serviceResult = withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Service")
                            .insert(service) { select() }
                            .decodeSingle<ServiceData>()
                    }

                    // 3. Create Booking Record
                    if (userIc.isBlank()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: User IC is missing.", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }

                    val formattedDate = try {
                        val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        outputFormat.format(inputFormat.parse(date)!!)
                    } catch (e: Exception) { date }

                    val formattedTime = try {
                        val inputFormat = SimpleDateFormat("hh:mm a", Locale.US)
                        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
                        outputFormat.format(inputFormat.parse(time)!!)
                    } catch (e: Exception) { time }

                    val booking = BookingData(
                        icNumber = userIc,
                        serviceId = serviceResult.serviceId!!,
                        date = formattedDate,
                        time = formattedTime
                    )

                    withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Booking")
                            .insert(booking)
                    }

                    showPaymentPage = false
                    submitted = true
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                        showPaymentPage = false
                    }
                } finally {
                    isSaving = false
                }
            }
            Unit
        }

        when (selectedPaymentMethod) {

            "Visa" -> VisaPaymentPage(
                onBack = { if (!isSaving) showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )

            "Mastercard" -> MastercardPaymentPage(
                onBack = { if (!isSaving) showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )

            "Touch 'n Go" -> TnGPaymentPage(
                onBack = { if (!isSaving) showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )
        }

        if (isSaving) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreenColour)
            }
        }

        return
    }

    val isFormValid =
        date.isNotEmpty() &&
                time.isNotEmpty()

    ServiceFormPage(
        title = "Maintenance",
        iconRes = R.drawable.home_repair_service_icon,
        description =
            "Keep your renewable energy equipment operating reliably.",
        onBack = onBack,
        onOpenProfile = onOpenProfile
    ) {

        InfoRow(
            R.drawable.home_repair_service_icon,
            "Service type",
            "Solar & battery inspection"
        )

        DateTimePickerField(
            value = date,
            label = "Preferred Date",
            placeholder = "Select a date",
            iconRes = R.drawable.calendar_month_icon,
            onClick = {
                showDatePicker = true
            },
            enabled = !submitted && !isSaving
        )

        DateTimePickerField(
            value = time,
            label = "Preferred Time",
            placeholder = "Select a time",
            iconRes = R.drawable.timer_icon,
            onClick = {

                if (date.isNotEmpty()) {
                    showTimePicker = true
                }
            },
            enabled = date.isNotEmpty() && !submitted && !isSaving
        )

        FormTextField(
            issue,
            {
                issue = it
            },
            "Issue / notes",
            "Optional: describe anything unusual"
        )

        SectionTitle(
            "Maintenance checklist"
        )

        listOf(
            "Solar panel condition",
            "System connections",
            "Battery condition",
            "General performance check"
        ).forEach {

            Text(
                "✓  $it",
                fontSize = 13.sp,
                color = TextGray
            )
        }

        if (!submitted) {

            SectionTitle(
                "Service fee"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                border = BorderStroke(
                    1.dp,
                    BorderLight
                ),
                elevation = CardDefaults.cardElevation(0.0.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Filled.Payment,
                                contentDescription = null,
                                tint = BrandGreenColour,
                                modifier = Modifier.size(24.dp)
                            )

                            Column {

                                Text(
                                    text = "Maintenance Fee",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark
                                )

                                Text(
                                    text = "Payable to confirm booking",
                                    fontSize = 11.sp,
                                    color = TextGray
                                )
                            }
                        }

                        Text(
                            text = "RM ${String.format("%.2f", maintenanceTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreenColour
                        )
                    }

                    HorizontalDivider(color = BorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Text(
                            text = "RM ${String.format("%.2f", maintenanceBaseFee)}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SST 6%",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Text(
                            text = "RM ${String.format("%.2f", maintenanceSst)}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }
            }

            SectionTitle(
                "Select payment method"
            )

            paymentMethods.forEach { method ->

                val isSelected =
                    selectedPaymentMethod == method.name

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !isSaving) {

                            selectedPaymentMethod =
                                method.name
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) {
                            BrandGreenColour
                        } else {
                            BorderLight
                        }
                    ),
                    elevation =
                        CardDefaults.cardElevation(0.0.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        method.color.copy(
                                            alpha = 0.12f
                                        )
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    painter = method.icon,
                                    contentDescription =
                                        method.name,
                                    tint = Color.Unspecified,
                                    modifier =
                                        Modifier.size(24.dp)
                                )
                            }

                            Text(
                                text = method.name,
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.Medium,
                                color = TextDark
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = {

                                selectedPaymentMethod =
                                    method.name
                            },
                            enabled = !isSaving,
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor =
                                        BrandGreenColour
                                )
                        )
                    }
                }
            }
        }

        SubmitButton(
            text =
                if (submitted) {
                    "BOOKING CONFIRMED"
                } else {
                    "PROCEED TO PAYMENT (RM ${String.format("%.2f", maintenanceTotal)})"
                },
            isLoading = isSaving,
            enabled = isFormValid && !submitted,
            onClick = {
                showPaymentPage = true
            }
        )

        if (submitted) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                border = BorderStroke(
                    1.dp,
                    BrandGreenColour
                ),
                elevation =
                    CardDefaults.cardElevation(0.0.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = BrandGreenColour,
                        modifier =
                            Modifier.size(36.dp)
                    )

                    Text(
                        text =
                            "Maintenance request submitted successfully.",
                        fontSize = 13.sp,
                        color = BrandGreenColour,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text =
                            "Your RM ${String.format("%.2f", maintenanceTotal)} fee via $selectedPaymentMethod has been processed. Your appointment will be confirmed by support.",
                        fontSize = 12.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        if (showDatePicker) {

            ServiceDatePickerDialog(

                onDateSelected = {
                        selectedDate ->

                    val formatter =
                        DateTimeFormatter.ofPattern(
                            "dd MMM yyyy",
                            Locale.US
                        )

                    val parsedDate =
                        try {
                            LocalDate.parse(
                                selectedDate,
                                formatter
                            )
                        } catch (
                            e: Exception
                        ) {
                            null
                        }

                    if (
                        parsedDate != null &&
                        !parsedDate.isBefore(
                            LocalDate.now()
                        )
                    ) {

                        date = selectedDate
                        time = ""
                    }
                },

                onDismiss = {
                    showDatePicker = false
                }
            )
        }

        if (showTimePicker) {

            ServiceTimePickerDialog(

                onTimeSelected = {
                        selectedTime ->

                    time = selectedTime
                },

                onDismiss = {
                    showTimePicker = false
                }
            )
        }
    }
}


@Composable
private fun CleaningPage(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {},
    coroutineScope: CoroutineScope,
    userIc: String
) {
    val context = LocalContext.current
    var address by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var selectedPaymentMethod by remember {
        mutableStateOf("Touch 'n Go")
    }

    var showPaymentPage by remember {
        mutableStateOf(false)
    }

    var isSaving by remember { mutableStateOf(false) }

    // Fee breakdown: base fee + 6% SST = total charged.
    // This MUST match the calculation used in PaymentHistoryScreen.kt
    // (tax = subtotal * SST_RATE, total = subtotal + tax) so the amount
    // shown here at checkout is identical to the amount shown later in
    // Payment History.
    val cleaningBaseFee = 100.0
    val cleaningSst = remember { cleaningBaseFee * SST_RATE }
    val cleaningTotal = remember { cleaningBaseFee + cleaningSst }

    val paymentMethods = listOf(
        PaymentMethodType(
            "Touch 'n Go",
            painterResource(id = R.drawable.ewallet),
            Color(0xFF00A651)
        ),
        PaymentMethodType(
            "Visa",
            painterResource(id = R.drawable.visa),
            Color(0xFF1A237E)
        ),
        PaymentMethodType(
            "Mastercard",
            painterResource(id = R.drawable.mastercard),
            Color(0xFFE65100)
        )
    )

    if (showPaymentPage) {

        val onPaymentSuccessAction = {
            coroutineScope.launch {
                try {
                    isSaving = true
                    val now = Date()
                    val dateStrLocal = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
                    val timeStrLocal = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)

                    // 1. Create Payment Record
                    // subtotal/sst/amount now match what Payment History will show.
                    val payment = PaymentData(
                        title = "Solar Cleaning Fee",
                        referenceNo = UUID.randomUUID().toString(),
                        method = selectedPaymentMethod,
                        date = dateStrLocal,
                        time = timeStrLocal,
                        subtotal = cleaningBaseFee,
                        sst = cleaningSst,
                        amount = cleaningTotal,
                        status = true
                    )

                    val paymentResult = withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Payment")
                            .insert(payment) { select() }
                            .decodeSingle<PaymentData>()
                    }

                    // 2. Create Service Record
                    val service = ServiceData(
                        paymentId = paymentResult.paymentId,
                        type = "Cleaning",
                        notes = "Location: $address",
                        location = address,
                        status = "Confirmed",
                        isFree = false
                    )

                    val serviceResult = withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Service")
                            .insert(service) { select() }
                            .decodeSingle<ServiceData>()
                    }

                    // 3. Create Booking Record
                    if (userIc.isBlank()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: User IC is missing.", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }

                    val formattedDate = try {
                        val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        outputFormat.format(inputFormat.parse(date)!!)
                    } catch (e: Exception) { date }

                    val formattedTime = try {
                        val inputFormat = SimpleDateFormat("hh:mm a", Locale.US)
                        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
                        outputFormat.format(inputFormat.parse(time)!!)
                    } catch (e: Exception) { time }

                    val booking = BookingData(
                        icNumber = userIc,
                        serviceId = serviceResult.serviceId!!,
                        date = formattedDate,
                        time = formattedTime
                    )

                    withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("Booking")
                            .insert(booking)
                    }

                    showPaymentPage = false
                    submitted = true
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                        showPaymentPage = false
                    }
                } finally {
                    isSaving = false
                }
            }
            Unit
        }

        when (selectedPaymentMethod) {

            "Visa" -> VisaPaymentPage(
                onBack = { if (!isSaving) showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )

            "Mastercard" -> MastercardPaymentPage(
                onBack = { if (!isSaving) showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )

            "Touch 'n Go" -> TnGPaymentPage(
                onBack = { if (!isSaving) showPaymentPage = false },
                onPaymentSuccess = onPaymentSuccessAction
            )
        }

        if (isSaving) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreenColour)
            }
        }

        return
    }

    val isFormValid =
        address.isNotBlank() &&
                date.isNotEmpty() &&
                time.isNotEmpty()

    ServiceFormPage(
        title = "Cleaning",
        iconRes = R.drawable.cleaning_services_icon,
        description =
            "Professional solar panel cleaning to help maintain good performance.",
        onBack = onBack,
        onOpenProfile = onOpenProfile
    ) {

        FormTextField(
            address,
            {
                address = it
            },
            "Service location",
            "Enter your service location"
        )

        DateTimePickerField(
            value = date,
            label = "Preferred Date",
            placeholder = "Select a date",
            iconRes = R.drawable.calendar_month_icon,
            onClick = {
                showDatePicker = true
            },
            enabled = !submitted && !isSaving
        )

        DateTimePickerField(
            value = time,
            label = "Preferred Time",
            placeholder = "Select a time",
            iconRes = R.drawable.timer_icon,
            onClick = {

                if (date.isNotEmpty()) {
                    showTimePicker = true
                }
            },
            enabled = date.isNotEmpty() && !submitted && !isSaving
        )

        InfoRow(
            R.drawable.cleaning_services_icon,
            "Estimated service",
            "Solar panel cleaning"
        )

        if (!submitted) {

            SectionTitle(
                "Service fee"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                border = BorderStroke(
                    1.dp,
                    BorderLight
                ),
                elevation = CardDefaults.cardElevation(0.0.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Filled.Payment,
                                contentDescription = null,
                                tint = BrandGreenColour,
                                modifier =
                                    Modifier.size(24.dp)
                            )

                            Column {

                                Text(
                                    text =
                                        "Solar Panel Cleaning Fee",
                                    fontSize = 14.sp,
                                    fontWeight =
                                        FontWeight.SemiBold,
                                    color = TextDark
                                )

                                Text(
                                    text =
                                        "Payable to confirm booking",
                                    fontSize = 11.sp,
                                    color = TextGray
                                )
                            }
                        }

                        Text(
                            text = "RM ${String.format("%.2f", cleaningTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreenColour
                        )
                    }

                    HorizontalDivider(color = BorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Text(
                            text = "RM ${String.format("%.2f", cleaningBaseFee)}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SST 6%",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Text(
                            text = "RM ${String.format("%.2f", cleaningSst)}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }
            }

            SectionTitle(
                "Select payment method"
            )

            paymentMethods.forEach { method ->

                val isSelected =
                    selectedPaymentMethod == method.name

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !isSaving) {

                            selectedPaymentMethod =
                                method.name
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) {
                            BrandGreenColour
                        } else {
                            BorderLight
                        }
                    ),
                    elevation =
                        CardDefaults.cardElevation(0.0.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        method.color.copy(
                                            alpha = 0.12f
                                        )
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    painter = method.icon,
                                    contentDescription =
                                        method.name,
                                    tint = Color.Unspecified,
                                    modifier =
                                        Modifier.size(24.dp)
                                )
                            }

                            Text(
                                text = method.name,
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.Medium,
                                color = TextDark
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = {

                                selectedPaymentMethod =
                                    method.name
                            },
                            enabled = !isSaving,
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor =
                                        BrandGreenColour
                                )
                        )
                    }
                }
            }
        }

        SubmitButton(
            text =
                if (submitted) {
                    "BOOKING CONFIRMED"
                } else {
                    "PROCEED TO PAYMENT (RM ${String.format("%.2f", cleaningTotal)})"
                },
            isLoading = isSaving,
            enabled = isFormValid && !submitted,
            onClick = {
                showPaymentPage = true
            }
        )

        if (submitted) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                border = BorderStroke(
                    1.dp,
                    BrandGreenColour
                ),
                elevation =
                    CardDefaults.cardElevation(0.0.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = BrandGreenColour,
                        modifier =
                            Modifier.size(36.dp)
                    )

                    Text(
                        text =
                            "Cleaning request submitted successfully.",
                        fontSize = 13.sp,
                        color = BrandGreenColour,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text =
                            "Your RM ${String.format("%.2f", cleaningTotal)} fee via $selectedPaymentMethod has been processed. Your appointment will be confirmed by support.",
                        fontSize = 12.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        if (showDatePicker) {

            ServiceDatePickerDialog(

                onDateSelected = {
                        selectedDate ->

                    val formatter =
                        DateTimeFormatter.ofPattern(
                            "dd MMM yyyy",
                            Locale.US
                        )

                    val parsedDate =
                        try {
                            LocalDate.parse(
                                selectedDate,
                                formatter
                            )
                        } catch (
                            e: Exception
                        ) {
                            null
                        }

                    if (
                        parsedDate != null &&
                        !parsedDate.isBefore(
                            LocalDate.now()
                        )
                    ) {

                        date = selectedDate
                        time = ""
                    }
                },

                onDismiss = {
                    showDatePicker = false
                }
            )
        }

        if (showTimePicker) {

            ServiceTimePickerDialog(

                onTimeSelected = {
                        selectedTime ->

                    time = selectedTime
                },

                onDismiss = {
                    showTimePicker = false
                }
            )
        }
    }
}

@Composable
private fun ServiceFormPage(
    title: String,
    @DrawableRes iconRes: Int,
    description: String,
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding(),
        contentPadding = PaddingValues(
            bottom = 28.dp
        )
    ) {

        item {

            TopBar(
                title = title,
                onBack = onBack,
                onProfileClick = onOpenProfile
            )
        }

        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                border = BorderStroke(
                    1.dp,
                    BorderLight
                ),
                elevation =
                    CardDefaults.cardElevation(0.0.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(15.dp)
                ) {

                    ServiceHeader(
                        iconRes,
                        title,
                        description
                    )

                    HorizontalDivider(
                        color = BorderLight
                    )

                    content()
                }
            }
        }
    }
}

@Composable
private fun FAQPage(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {

    val faqs = remember {

        listOf(
            FAQData(
                "How does the 100kWh storage scale work?",
                "The system uses a 100kWh base for easy monitoring. If your display shows 4.56kWh, it represents exactly 4% of your total capacity."
            ),
            FAQData(
                "What is Auto-Sell and how does it trigger?",
                "Auto-Sell automatically sells excess energy to the grid when your battery exceeds 80kWh (80%), helping you earn credits without manual input."
            ),
            FAQData(
                "Is my LEGA Roof Assessment deposit refundable?",
                "Yes. The RM 50.00 deposit is fully refundable if your property is found to be ineligible for solar installation after our professional evaluation."
            ),
            FAQData(
                "How do I sell energy back to the grid manually?",
                "Go to the Smart Sell page, use the slider to select an amount, and tap 'Discharge Now'. Your battery will be deducted and credits added instantly."
            ),
            FAQData(
                "How can I download a receipt for my payment?",
                "Open Payment History from the menu, tap on any successful transaction, and use the 'Download Receipt' button to generate a professional PDF."
            ),
            FAQData(
                "What should I do if my home stats aren't updating?",
                "You can tap the Refresh icon next to your name on the Home screen to force a real-time sync with the database."
            ),
            FAQData(
                "Why can't I withdraw my full credit balance?",
                "Ensure you have selected a bank and entered exactly 16 digits. The system also uses high-precision rounding to ensure you can withdraw every cent."
            ),
            FAQData(
                "What is the CREAM Roof yield evaluation?",
                "It is a professional assessment where we analyze your roof space and shading levels to estimate how much solar income you can generate."
            ),
            FAQData(
                "How long does it take for a maintenance booking to be confirmed?",
                "Once you pay the fee, the status is set to 'Confirmed'. A technician will typically call you within 24 hours to finalize the arrival time."
            ),
            FAQData(
                "Can I update my house address after registration?",
                "Yes. Go to your Profile and update your address. The new address will be automatically used for all future LEGA assessments and service bookings."
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(
            bottom = 28.dp
        )
    ) {

        item {

            TopBar(
                title = "Frequently Asked Questions",
                onBack = onBack,
                onProfileClick = onOpenProfile
            )
        }

        item {

            Text(
                "20 common questions about EnergyNest services",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                )
            )
        }

        items(faqs) { faq ->

            Box(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
            ) {

                FAQItem(faq)
            }
        }
    }
}

@Composable
private fun FAQItem(
    faq: FAQData
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        border = BorderStroke(
            1.dp,
            BorderLight
        ),
        elevation =
            CardDefaults.cardElevation(0.0.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = faq.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(
                        id = if (expanded) {
                            R.drawable.arrow_drop_up
                        } else {
                            R.drawable.arrow_drop_down
                        }
                    ),
                    contentDescription =
                        if (expanded) {
                            "Collapse"
                        } else {
                            "Expand"
                        },
                    tint = BrandGreenColour
                )
            }

            if (expanded) {

                HorizontalDivider(
                    color = BorderLight
                )

                Text(
                    text = faq.answer,
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceScreenPreview() {
    ServicesScreen(userIc = "123456789012")
}