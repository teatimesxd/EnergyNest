package com.example.energynest.sell_payment

import com.example.energynest.backend_models.SupabaseClient
import com.example.energynest.backend_models.SmartSellData
import com.example.energynest.backend_models.HomeStats
import com.example.energynest.backend_models.PaymentData
import com.example.energynest.backend_models.FloorUsage
import com.example.energynest.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// SupabaseClient import moved to top
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

private val Background = Color(0xFFF6F8F7)
private val TextDark = Color(0xFF191C1E)
private val TextGray = Color(0xFF5A6065)
private val BrandGreenColour = Color(0xFF00B87C)
private val LightGreenBg = Color(0xFFD8F3E5)
private val ProgressBg = Color(0xFFE5E7EB)
private val CardBorderColor = Color(0xFFE2E8F0)
private val CreditBoxBg = Color(0xFFF3F4F6)
private val FloorCircleBg = Color(0xFFEAECEE)
private val White = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSellScreen(
    userIc: String,
    onOpenDrawer: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var accumulatedCredits by remember { mutableDoubleStateOf(0.0) }
    var autoSellEnabled by remember { mutableStateOf(false) }
    var storedEnergyPercent by remember { mutableFloatStateOf(0f) }
    var storedEnergyKwh by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }

    // Manual Sell Bottom Sheet State
    var showSellSheet by remember { mutableStateOf(false) }
    val sellSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sellAmountKwh by remember { mutableFloatStateOf(0.5f) }
    val tnbRatePerKwh = 0.38
    val maxEnergyCapacity = 100.0 // Issue 2: Limit to 100kWh

    // Withdrawal Bottom Sheet State
    var showWithdrawSheet by remember { mutableStateOf(false) }
    val withdrawSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var withdrawAmountText by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Touch 'n Go eWallet") }
    var accountOrPhoneText by remember { mutableStateOf("") }
    var withdrawSuccess by remember { mutableStateOf(false) }
    var withdrawError by remember { mutableStateOf<String?>(null) }
    var isSavingToDb by remember { mutableStateOf(false) }

    var latestHomeDate by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val sellResult = withContext(Dispatchers.IO) {
                SupabaseClient.client.from("Smart_Sell")
                    .select {
                        filter { eq("ic_number", userIc) }
                        order("smart_sell_id", order = Order.DESCENDING)
                    }
                    .decodeList<SmartSellData>()
                    .firstOrNull()
            }
            if (sellResult != null) {
                accumulatedCredits = sellResult.accumulatedCredit
                autoSellEnabled = sellResult.autoSellEnabled
            }

            val homeResult = withContext(Dispatchers.IO) {
                SupabaseClient.client.from("Home")
                    .select {
                        filter { eq("ic_number", userIc) }
                        order("date", order = Order.DESCENDING)
                    }
                    .decodeList<HomeStats>()
                    .firstOrNull()
            }
            if (homeResult != null) {
                latestHomeDate = homeResult.date
                storedEnergyKwh = homeResult.storedEnergyKwh.toFloat()

                // Fixed: Calculate percentage based on 100kWh capacity
                // 4.56 kWh / 100.0 * 100 = 4.56%
                storedEnergyPercent = (storedEnergyKwh / 100f * 100f).coerceIn(0f, 100f)

                // Update slider max safely
                sellAmountKwh = if (storedEnergyKwh > 0.5f) 5.0f.coerceIn(0.5f, storedEnergyKwh) else 0.5f

                // Issue 2: Auto Sell Logic (Run once on enter if enabled)
                if (autoSellEnabled && (storedEnergyKwh > 80f)) { // 80% is 80kWh now
                    val excessKwh = (storedEnergyKwh - 80f).toDouble()
                    if (excessKwh >= 0.1) {
                        coroutineScope.launch {
                            performSellTransaction(
                                ic = userIc,
                                amountKwh = excessKwh,
                                currentCredits = accumulatedCredits,
                                currentKwh = storedEnergyKwh.toDouble(),
                                currentPct = storedEnergyPercent.toDouble(),
                                isAuto = true,
                                globalAutoEnabled = true,
                                homeDate = homeResult.date,
                                onResult = { newCredits: Double, newKwh: Double, newPct: Double ->
                                    accumulatedCredits = newCredits
                                    storedEnergyKwh = newKwh.toFloat()
                                    storedEnergyPercent = newPct.toFloat()
                                }
                            )
                        }
                    }
                }
            }

        } catch (e: Exception) {
            // Error handling
        } finally {
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ---- Top App Bar ----
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                painter = painterResource(id = R.drawable.sidebar_icon),
                                contentDescription = "Sidebar",
                                tint = TextDark
                            )
                        }
                        Text(
                            text = "Smart Sell",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    // ---- Profile Icon ----
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.profile_icon),
                            contentDescription = "Profile",
                            tint = TextDark
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = CardBorderColor)
            }
        }

        // ---- Main Screen Content ----
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ---- Money Earned Header Badge (Top Right) ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Energy Overview",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Surface(
                        modifier = Modifier.clickable {
                            withdrawAmountText = ""
                            withdrawError = null
                            withdrawSuccess = false
                            showWithdrawSheet = true
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = LightGreenBg,
                        border = BorderStroke(1.dp, BrandGreenColour.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.payments_icon),
                                contentDescription = "Money Earned",
                                tint = BrandGreenColour,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Earned: RM ${String.format(Locale.US, "%.2f", accumulatedCredits)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreenColour
                            )
                        }
                    }
                }

                // ---- Stored Energy Card ----
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, CardBorderColor),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(LightGreenBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.battery_icon),
                                    contentDescription = "Stored Energy Icon",
                                    tint = BrandGreenColour,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Stored Energy",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextGray
                                )
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${storedEnergyPercent.toInt()}%",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = "($storedEnergyKwh kWh)",
                                        fontSize = 14.sp,
                                        color = TextGray
                                    )
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { storedEnergyPercent / 100f },
                            modifier = Modifier
                                .width(110.dp)
                                .height(8.dp)
                                .clip(CircleShape),
                            color = BrandGreenColour,
                            trackColor = ProgressBg,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                // ---- Auto-Sell Excess Electricity Card ----
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, CardBorderColor),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.bolt_icon),
                                    contentDescription = "Bolt",
                                    tint = BrandGreenColour,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Auto-Sell Excess Electricity",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }

                            Switch(
                                checked = autoSellEnabled,
                                onCheckedChange = { isChecked ->
                                    autoSellEnabled = isChecked
                                    coroutineScope.launch {
                                        try {
                                            withContext(Dispatchers.IO) {
                                                SupabaseClient.client.from("Smart_Sell")
                                                    .update({
                                                        set("auto_Sell_Enabled", isChecked)
                                                    }) {
                                                        filter { eq("ic_number", userIc) }
                                                    }
                                            }

                                            // Trigger auto-sell immediately if turned ON and battery > 80%
                                            if (isChecked && storedEnergyPercent > 80f) {
                                                val excessKwh = (storedEnergyKwh * ((storedEnergyPercent - 80f) / storedEnergyPercent)).toDouble()
                                                if (excessKwh >= 0.1) {
                                                    performSellTransaction(
                                                        ic = userIc,
                                                        amountKwh = excessKwh,
                                                        currentCredits = accumulatedCredits,
                                                        currentKwh = storedEnergyKwh.toDouble(),
                                                        currentPct = storedEnergyPercent.toDouble(),
                                                        isAuto = true,
                                                        globalAutoEnabled = true,
                                                        homeDate = latestHomeDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                                                        onResult = { newCredits: Double, newKwh: Double, newPct: Double ->
                                                            accumulatedCredits = newCredits
                                                            storedEnergyKwh = newKwh.toFloat()
                                                            storedEnergyPercent = newPct.toFloat()
                                                        }
                                                    )
                                                }
                                            }
                                        } catch (e: Exception) {
                                            // Handle error
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = White,
                                    checkedTrackColor = BrandGreenColour,
                                    uncheckedThumbColor = White,
                                    uncheckedTrackColor = ProgressBg,
                                    uncheckedBorderColor = Color.Transparent
                                )
                            )
                        }

                        Text(
                            text = "Automatically sell excess power to TNB under the 1:1 Solar ATAP credit program when your battery storage exceeds 80%.",
                            fontSize = 13.sp,
                            color = TextGray,
                            lineHeight = 18.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CreditBoxBg)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Accumulated Bill Credits",
                                fontSize = 14.sp,
                                color = TextGray
                            )
                            Text(
                                text = "RM ${String.format(Locale.US, "%.2f", accumulatedCredits)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreenColour
                            )
                        }

                        OutlinedButton(
                            onClick = { showSellSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CardBorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                        ) {
                            Text(
                                text = "Sell Excess Manually Now",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSellSheet) {
        val estimatedEarnings = sellAmountKwh * tnbRatePerKwh

        ModalBottomSheet(
            onDismissRequest = { if (!isSavingToDb) showSellSheet = false },
            sheetState = sellSheetState,
            containerColor = White
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Manual Energy Discharge",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = "Select how much stored energy to sell to the TNB grid immediately at RM ${tnbRatePerKwh}/kWh.",
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Amount to Sell",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.1f", sellAmountKwh)} kWh",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreenColour
                        )
                    }

                    Slider(
                        value = sellAmountKwh.coerceIn(0.5f, maxOf(0.5f, storedEnergyKwh)),
                        onValueChange = { sellAmountKwh = it },
                        valueRange = 0.5f..maxOf(0.5f, storedEnergyKwh),
                        steps = if (storedEnergyKwh > 0.5f) ((maxOf(0.5f, storedEnergyKwh) - 0.5f) / 0.1f).toInt() else 0,
                        enabled = !isSavingToDb,
                        colors = SliderDefaults.colors(
                            thumbColor = BrandGreenColour,
                            activeTrackColor = BrandGreenColour,
                            inactiveTrackColor = ProgressBg
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Min: 0.5 kWh", fontSize = 12.sp, color = TextGray)
                        Text(text = "Max: ${storedEnergyKwh} kWh", fontSize = 12.sp, color = TextGray)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CreditBoxBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Bill Credit",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                        Text(
                            text = "1:1 Solar ATAP Rate",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                    Text(
                        text = "+ RM ${String.format(Locale.US, "%.2f", estimatedEarnings)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenColour
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showSellSheet = false },
                        enabled = !isSavingToDb,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text(text = "Cancel", color = TextDark, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (userIc.isBlank()) {
                                Toast.makeText(context, "Error: User IC is missing. Please log in again.", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            coroutineScope.launch {
                                isSavingToDb = true
                                performSellTransaction(
                                    ic = userIc,
                                    amountKwh = sellAmountKwh.toDouble(),
                                    currentCredits = accumulatedCredits,
                                    currentKwh = storedEnergyKwh.toDouble(),
                                    currentPct = storedEnergyPercent.toDouble(),
                                    isAuto = false,
                                    globalAutoEnabled = autoSellEnabled,
                                    homeDate = latestHomeDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                                    onResult = { newCredits: Double, newKwh: Double, newPct: Double ->
                                        accumulatedCredits = newCredits
                                        storedEnergyKwh = newKwh.toFloat()
                                        storedEnergyPercent = newPct.toFloat()
                                        showSellSheet = false
                                        Toast.makeText(context, "✅ Energy discharge successful!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { msg: String ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    },
                                    onComplete = {
                                        isSavingToDb = false
                                    }
                                )
                            }
                        },
                        enabled = !isSavingToDb,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreenColour)
                    ) {
                        if (isSavingToDb) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White, strokeWidth = 2.dp)
                        } else {
                            Text(text = "Discharge Now", fontWeight = FontWeight.Bold, color = White)
                        }
                    }
                }
            }
        }
    }

    if (showWithdrawSheet) {
        ModalBottomSheet(
            onDismissRequest = { if (!isSavingToDb) showWithdrawSheet = false },
            sheetState = withdrawSheetState,
            containerColor = White
        ) {
            val withdrawScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(withdrawScrollState)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Withdraw Earnings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CreditBoxBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Earnings",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                        Text(
                            text = "RM ${String.format(Locale.US, "%.2f", accumulatedCredits)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreenColour
                        )
                    }
                }

                if (withdrawSuccess) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGreenBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.check_circle_icon),
                                contentDescription = "Success",
                                tint = BrandGreenColour
                            )
                            Text(
                                text = "Withdrawal request processed successfully!",
                                fontSize = 14.sp,
                                color = BrandGreenColour,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Button(
                        onClick = { showWithdrawSheet = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreenColour)
                    ) {
                        Text(text = "Done", fontWeight = FontWeight.Bold, color = White)
                    }
                } else {
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() || it == '.' }) {
                                if (input.count { it == '.' } <= 1) {
                                    withdrawAmountText = input
                                    withdrawError = null
                                }
                            }
                        },
                        label = { Text("Withdrawal Amount (RM)") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isSavingToDb,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = BrandGreenColour,
                            focusedLabelColor = BrandGreenColour,
                            cursorColor = BrandGreenColour
                        )
                    )

                    withdrawError?.let { error ->
                        Text(
                            text = error,
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }

                    Text(
                        text = "Payment Method",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    val paymentMethods = listOf("Touch 'n Go eWallet", "Bank Transfer (Maybank/TNB)")
                    paymentMethods.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (selectedPaymentMethod == method) BrandGreenColour else CardBorderColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(enabled = !isSavingToDb) {
                                    selectedPaymentMethod = method
                                    accountOrPhoneText = ""
                                    withdrawError = null
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = method,
                                fontSize = 14.sp,
                                fontWeight = if (selectedPaymentMethod == method) FontWeight.Bold else FontWeight.Normal,
                                color = TextDark
                            )
                            RadioButton(
                                selected = (selectedPaymentMethod == method),
                                onClick = {
                                    selectedPaymentMethod = method
                                    accountOrPhoneText = ""
                                    withdrawError = null
                                },
                                enabled = !isSavingToDb,
                                colors = RadioButtonDefaults.colors(selectedColor = BrandGreenColour)
                            )
                        }
                    }

                    // Issue 1: Bank Selection
                    var showBankDropdown by remember { mutableStateOf(false) }
                    val bankList = listOf("Maybank", "CIMB Bank", "Public Bank", "RHB Bank", "Hong Leong Bank", "AmBank")
                    var selectedBank by remember { mutableStateOf("") }

                    if (selectedPaymentMethod.contains("Bank", ignoreCase = true)) {
                        Text(
                            text = "Select Bank",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { showBankDropdown = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (selectedBank.isEmpty()) Color.Red else BrandGreenColour)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(if (selectedBank.isEmpty()) "Choose your bank" else selectedBank, color = if (selectedBank.isEmpty()) Color.Gray else Color.Black)
                                    Icon(painterResource(id = R.drawable.arrow_drop_down), null, tint = Color.Black)
                                }
                            }
                            DropdownMenu(
                                expanded = showBankDropdown,
                                onDismissRequest = { showBankDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.85f).background(White)
                            ) {
                                bankList.forEach { bank ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = bank,
                                                color = Color.Black // Issue 1: Fix selection color
                                            )
                                        },
                                        onClick = {
                                            selectedBank = bank
                                            showBankDropdown = false
                                            withdrawError = null
                                        }
                                    )
                                }
                            }
                        }
                    }

                    val label = when {
                        selectedPaymentMethod.contains("Touch 'n Go", ignoreCase = true) -> "Phone Number"
                        selectedPaymentMethod.contains("Bank", ignoreCase = true) -> "Bank Account Number (16 digits)"
                        else -> "Account Detail"
                    }

                    val placeholder = when {
                        selectedPaymentMethod.contains("Touch 'n Go", ignoreCase = true) -> "0123456789"
                        selectedPaymentMethod.contains("Bank", ignoreCase = true) -> "1234 5678 1234 5678"
                        else -> "Enter details"
                    }

                    OutlinedTextField(
                        value = accountOrPhoneText,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { char -> char.isDigit() }
                            // Limit bank account to 16, phone to 11
                            val limit = if (selectedPaymentMethod.contains("Bank")) 16 else 11
                            accountOrPhoneText = digitsOnly.take(limit)
                            withdrawError = null
                        },
                        label = { Text(label) },
                        placeholder = { Text(placeholder) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isSavingToDb,
                        keyboardOptions = KeyboardOptions(keyboardType = if (selectedPaymentMethod.contains("Bank")) KeyboardType.Number else KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = BrandGreenColour,
                            focusedLabelColor = BrandGreenColour,
                            cursorColor = BrandGreenColour
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showWithdrawSheet = false },
                            enabled = !isSavingToDb,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text(text = "Cancel", color = TextDark, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val amount = withdrawAmountText.toDoubleOrNull()
                                val isBank = selectedPaymentMethod.contains("Bank")

                                // Issue 2: Fixed decimal precision comparison
                                val roundedAmount = if (amount != null) (Math.round(amount * 100.0) / 100.0) else 0.0
                                val roundedBalance = Math.round(accumulatedCredits * 100.0) / 100.0
                                val withdrawAmount = roundedAmount
                                val availableBalance = roundedBalance

                                when {
                                    amount == null || amount <= 0 -> {
                                        withdrawError = "Please enter a valid amount."
                                    }
                                    withdrawAmount > (availableBalance + 0.001) -> {
                                        withdrawError = "Amount exceeds available balance."
                                    }
                                    isBank && selectedBank.isEmpty() -> {
                                        withdrawError = "Please select a bank."
                                    }
                                    isBank && accountOrPhoneText.length != 16 -> {
                                        withdrawError = "Bank account must be 16 digits."
                                    }
                                    accountOrPhoneText.isBlank() -> {
                                        withdrawError = "Please enter your details."
                                    }
                                    else -> {
                                        coroutineScope.launch {
                                            try {
                                                isSavingToDb = true
                                                val now = Date()
                                                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
                                                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)

                                                val payment = PaymentData(
                                                    title = "Withdrawal - ${if(isBank) selectedBank else selectedPaymentMethod}",
                                                    referenceNo = UUID.randomUUID().toString(),
                                                    method = selectedPaymentMethod,
                                                    date = dateStr,
                                                    time = timeStr,
                                                    subtotal = roundedAmount,
                                                    sst = 0.0,
                                                    amount = roundedAmount,
                                                    status = true
                                                )

                                                val paymentResult = withContext(Dispatchers.IO) {
                                                    SupabaseClient.client.from("Payment")
                                                        .insert(payment) { select() }
                                                        .decodeSingle<PaymentData>()
                                                }

                                                val newBalance = Math.max(0.0, (Math.round((accumulatedCredits - roundedAmount) * 100.0) / 100.0))
                                                withContext(Dispatchers.IO) {
                                                    SupabaseClient.client.from("Smart_Sell")
                                                        .insert(SmartSellData(
                                                            icNumber = userIc,
                                                            paymentId = paymentResult.paymentId,
                                                            accumulatedCredit = newBalance,
                                                            amountKwh = 0.0,
                                                            estimatedBillCredit = -roundedAmount,
                                                            autoSellEnabled = autoSellEnabled
                                                        ))
                                                }

                                                accumulatedCredits = newBalance
                                                withdrawSuccess = true
                                                Toast.makeText(context, "✅ Withdrawal successful!", Toast.LENGTH_SHORT).show()
                                            } catch (e: Exception) {
                                                withdrawError = "Failed to process withdrawal: ${e.message}"
                                            } finally {
                                                isSavingToDb = false
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isSavingToDb,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreenColour)
                        ) {
                            if (isSavingToDb) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White, strokeWidth = 2.dp)
                            } else {
                                Text(text = "Withdraw Now", fontWeight = FontWeight.Bold, color = White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSmartSellScreen() {
    SmartSellScreen(userIc = "123456789012")
}

private suspend fun performSellTransaction(
    ic: String,
    amountKwh: Double,
    currentCredits: Double,
    currentKwh: Double,
    currentPct: Double,
    isAuto: Boolean,
    globalAutoEnabled: Boolean,
    homeDate: String,
    onResult: (Double, Double, Double) -> Unit,
    onError: (String) -> Unit = {},
    onComplete: () -> Unit = {}
) {
    try {
        val tnbRatePerKwh = 0.38
        val earnings = amountKwh * tnbRatePerKwh
        val now = Date()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)

        val payment = PaymentData(
            title = if (isAuto) "Auto Energy Discharge" else "Manual Energy Discharge",
            referenceNo = UUID.randomUUID().toString(),
            method = "Grid Sell",
            date = dateStr,
            time = timeStr,
            subtotal = earnings,
            sst = 0.0,
            amount = earnings,
            status = true
        )

        // 1. Insert Payment
        val paymentResult = withContext(Dispatchers.IO) {
            SupabaseClient.client.from("Payment")
                .insert(payment) { select() }
                .decodeSingle<PaymentData>()
        }

        if (paymentResult.paymentId == null) {
            throw Exception("Failed to retrieve Payment ID")
        }

        // 2. Insert Smart Sell record (Issue 3 fix: keep globalAutoEnabled)
        val smartSellEntry = SmartSellData(
            icNumber = ic,
            paymentId = paymentResult.paymentId,
            accumulatedCredit = currentCredits + earnings,
            amountKwh = amountKwh,
            estimatedBillCredit = earnings,
            autoSellEnabled = globalAutoEnabled
        )

        withContext(Dispatchers.IO) {
            SupabaseClient.client.from("Smart_Sell")
                .insert(smartSellEntry)
        }

        // 3. Deduct from Home Stats (Issue 1 fix: use date filter)
        val newKwh = (currentKwh - amountKwh).coerceAtLeast(0.0)
        // Fixed capacity to 100kWh: Percentage is numerically equal to kWh (e.g. 4.56kWh = 4.56%)
        val newPct = newKwh.coerceIn(0.0, 100.0)

        withContext(Dispatchers.IO) {
            SupabaseClient.client.from("Home")
                .update({
                    set("stored_energy_kwh", newKwh)
                    set("stored_energy_pct", newPct)
                }) {
                    filter {
                        eq("ic_number", ic)
                        eq("date", homeDate)
                    }
                }
        }

        onResult(currentCredits + earnings, newKwh, newPct)
    } catch (e: Exception) {
        onError("Database Error: ${e.message}")
    } finally {
        onComplete()
    }
}
