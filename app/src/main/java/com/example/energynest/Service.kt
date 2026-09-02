package com.example.energynest

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreenColour = Color(0xFF00B87C)
private val White = Color.White
private val IconBg = Color(0xFFE8ECE9)
private val BorderLight = Color(0xFFE2E8F0)

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
    onOpenDrawer: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onProfileClick: () -> Unit = onOpenProfile
) {
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
            onOpenProfile = handleProfileClick
        )

        ServicePage.MAINTENANCE -> MaintenancePage(
            onBack = {
                currentPage = ServicePage.HOME
            },
            onOpenProfile = handleProfileClick
        )

        ServicePage.CLEANING -> CleaningPage(
            onBack = {
                currentPage = ServicePage.HOME
            },
            onOpenProfile = handleProfileClick
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
                "How do I track my energy savings?",
                "Open View Electric Analysis to see your energy usage, estimated savings, and recent performance."
            ),
            FAQData(
                "What is CREAM?",
                "CREAM is the leasing service used to help customers access renewable energy solutions through a flexible leasing arrangement."
            ),
            FAQData(
                "How long does customer service take to respond?",
                "Most general enquiries are reviewed within one business day. Urgent service issues may be prioritised."
            ),
            FAQData(
                "How can I book an energy consultation?",
                "Tap Consultation on this page, select a preferred date and time, and submit the request."
            ),
            FAQData(
                "Can I reschedule a consultation?",
                "Yes. Contact Customer Service with your booking details and preferred new time."
            ),
            FAQData(
                "How often should solar panels be maintained?",
                "A routine inspection is recommended periodically to check system performance, connections, and panel condition."
            ),
            FAQData(
                "Why does my energy production change?",
                "Production can change because of sunlight, weather, panel condition, system performance, and household usage."
            ),
            FAQData(
                "How do I request solar panel cleaning?",
                "Tap Cleaning, choose a preferred date, provide your location, and submit the cleaning request."
            ),
            FAQData(
                "What happens during maintenance?",
                "A technician can inspect the solar panels, connections, battery equipment, and general system condition."
            ),
            FAQData(
                "Can I contact support about billing?",
                "Yes. Customer Service can assist with general billing questions and direct you to the appropriate account information."
            ),
            FAQData(
                "Where can I view my payment history?",
                "Payment History is available from the main navigation menu of the EnergyNest application."
            ),
            FAQData(
                "Where can I view my electricity analysis?",
                "Use View Electric Analysis from the main navigation menu to review your electricity information."
            ),
            FAQData(
                "Can I request a service for another date?",
                "Yes. Choose your preferred date when submitting a consultation, maintenance, or cleaning request."
            ),
            FAQData(
                "What information should I provide to support?",
                "Providing a short description of the issue can help support assist you faster."
            ),
            FAQData(
                "Do I need to be at home for maintenance?",
                "It depends on the type of service and access required."
            ),
            FAQData(
                "How do I cancel a service request?",
                "Contact Customer Service with your request details before the scheduled appointment."
            ),
            FAQData(
                "How can I improve my household energy efficiency?",
                "Review your electricity analysis, identify high-usage periods, and consider energy-efficient appliances and habits."
            ),
            FAQData(
                "Can I request help choosing a service?",
                "Yes. Customer Service can explain the available services and help you choose the most suitable option."
            ),
            FAQData(
                "How will I know if my service request is submitted?",
                "A confirmation message will appear after a request is successfully submitted."
            ),
            FAQData(
                "Where can I get more help?",
                "Use Customer Service to contact the EnergyNest support team for account, service, or general assistance."
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
        elevation = CardDefaults.cardElevation(0.dp)
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

    androidx.compose.material3.DatePickerDialog(
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
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGreenColour,
            contentColor = White
        )
    ) {

        Text(
            text,
            fontWeight = FontWeight.Bold
        )
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
            .background(Background),
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
                elevation = CardDefaults.cardElevation(0.dp)
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
                        "Mon – Fri, 9:00 AM – 5:00 PM"
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
    onOpenProfile: () -> Unit = {}
) {

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
            }
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
            enabled = date.isNotEmpty()
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
            onClick = {

                if (
                    date.isNotEmpty() &&
                    time.isNotEmpty()
                ) {

                    submitted = true
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


// ================================================================
// MAINTENANCE PAGE
// Added RM 50.00 payment checkout.
// ================================================================

@Composable
private fun MaintenancePage(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {

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

    // ------------------------------------------------------------
    // PAYMENT CHECKOUT
    // ------------------------------------------------------------

    if (showPaymentPage) {

        when (selectedPaymentMethod) {

            "Visa" -> VisaPaymentPage(
                onBack = {
                    showPaymentPage = false
                },
                onPaymentSuccess = {
                    showPaymentPage = false
                    submitted = true
                }
            )

            "Mastercard" -> MastercardPaymentPage(
                onBack = {
                    showPaymentPage = false
                },
                onPaymentSuccess = {
                    showPaymentPage = false
                    submitted = true
                }
            )

            "Touch 'n Go" -> TnGPaymentPage(
                onBack = {
                    showPaymentPage = false
                },
                onPaymentSuccess = {
                    showPaymentPage = false
                    submitted = true
                }
            )
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
            }
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
            enabled = date.isNotEmpty()
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

        // --------------------------------------------------------
        // MAINTENANCE FEE
        // --------------------------------------------------------

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
                elevation = CardDefaults.cardElevation(0.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                        text = "RM 50.00",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenColour
                    )
                }
            }

            // ----------------------------------------------------
            // PAYMENT METHODS
            // ----------------------------------------------------

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
                        .clickable {

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
                        CardDefaults.cardElevation(0.dp)
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

        // --------------------------------------------------------
        // CHECKOUT BUTTON
        // --------------------------------------------------------

        SubmitButton(
            text =
                if (submitted) {
                    "BOOKING CONFIRMED"
                } else {
                    "PROCEED TO PAYMENT (RM 50.00)"
                },
            onClick = {

                if (
                    isFormValid &&
                    !submitted
                ) {

                    showPaymentPage = true
                }
            }
        )

        // --------------------------------------------------------
        // SUCCESS MESSAGE
        // --------------------------------------------------------

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
                    CardDefaults.cardElevation(0.dp)
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
                            "Your RM 50.00 fee via $selectedPaymentMethod has been processed. Your appointment will be confirmed by support.",
                        fontSize = 12.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // --------------------------------------------------------
        // DATE PICKER
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // TIME PICKER
        // --------------------------------------------------------

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


// ================================================================
// CLEANING PAGE
// Cleaning fee changed from RM 50.00 to RM 100.00
// ================================================================

@Composable
private fun CleaningPage(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {

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

        when (selectedPaymentMethod) {

            "Visa" -> VisaPaymentPage(
                onBack = {
                    showPaymentPage = false
                },
                onPaymentSuccess = {
                    showPaymentPage = false
                    submitted = true
                }
            )

            "Mastercard" -> MastercardPaymentPage(
                onBack = {
                    showPaymentPage = false
                },
                onPaymentSuccess = {
                    showPaymentPage = false
                    submitted = true
                }
            )

            "Touch 'n Go" -> TnGPaymentPage(
                onBack = {
                    showPaymentPage = false
                },
                onPaymentSuccess = {
                    showPaymentPage = false
                    submitted = true
                }
            )
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
            }
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
            enabled = date.isNotEmpty()
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
                elevation = CardDefaults.cardElevation(0.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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

                    // CHANGED: RM 50.00 -> RM 100.00

                    Text(
                        text = "RM 100.00",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenColour
                    )
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
                        .clickable {

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
                        CardDefaults.cardElevation(0.dp)
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
                    // CHANGED: RM 50.00 -> RM 100.00
                    "PROCEED TO PAYMENT (RM 100.00)"
                },
            onClick = {

                if (
                    isFormValid &&
                    !submitted
                ) {

                    showPaymentPage = true
                }
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
                    CardDefaults.cardElevation(0.dp)
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
                        // CHANGED: RM 50.00 -> RM 100.00
                        text =
                            "Your RM 100.00 fee via $selectedPaymentMethod has been processed. Your appointment will be confirmed by support.",
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
            .background(Background),
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
                    CardDefaults.cardElevation(0.dp)
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
                "How do I track my energy savings?",
                "Open View Electric Analysis to see your energy usage, estimated savings, and recent performance."
            ),
            FAQData(
                "What is CREAM?",
                "CREAM is the leasing service used to help customers access renewable energy solutions through a flexible leasing arrangement."
            ),
            FAQData(
                "How long does customer service take to respond?",
                "Most general enquiries are reviewed within one business day."
            ),
            FAQData(
                "How can I book an energy consultation?",
                "Tap Consultation on the Services page, select a preferred date and time, and submit the request."
            ),
            FAQData(
                "Can I reschedule a consultation?",
                "Yes. Contact Customer Service with your booking details and preferred new time."
            ),
            FAQData(
                "How often should solar panels be maintained?",
                "A routine inspection is recommended periodically."
            ),
            FAQData(
                "Why does my energy production change?",
                "Production can change because of sunlight, weather, panel condition, system performance, and household usage."
            ),
            FAQData(
                "How do I request solar panel cleaning?",
                "Tap Cleaning, choose a preferred date, provide your location, and submit the cleaning request."
            ),
            FAQData(
                "What happens during maintenance?",
                "A technician can inspect the solar panels, connections, battery equipment, and general system condition."
            ),
            FAQData(
                "Can I contact support about billing?",
                "Yes. Customer Service can assist with general billing questions."
            ),
            FAQData(
                "Where can I view my payment history?",
                "Payment History is available from the main navigation menu."
            ),
            FAQData(
                "Where can I view my electricity analysis?",
                "Use View Electric Analysis from the main navigation menu."
            ),
            FAQData(
                "Can I request a service for another date?",
                "Yes. Choose your preferred date when submitting a service request."
            ),
            FAQData(
                "What information should I provide to support?",
                "Providing a short description of the issue can help support assist you faster."
            ),
            FAQData(
                "Do I need to be at home for maintenance?",
                "It depends on the type of service and access required."
            ),
            FAQData(
                "How do I cancel a service request?",
                "Contact Customer Service with your request details before the scheduled appointment."
            ),
            FAQData(
                "How can I improve my household energy efficiency?",
                "Review your electricity analysis and identify high-usage periods."
            ),
            FAQData(
                "Can I request help choosing a service?",
                "Yes. Customer Service can explain the available services."
            ),
            FAQData(
                "How will I know if my service request is submitted?",
                "A confirmation message will appear after a request is successfully submitted."
            ),
            FAQData(
                "Where can I get more help?",
                "Use Customer Service to contact the EnergyNest support team."
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
            CardDefaults.cardElevation(0.dp)
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
    ServicesScreen()
}