package com.example.energynest

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class SmartSellWithPayment(
    @SerialName("smart_sell_id") val smartSellId: Int,
    @SerialName("Payment") val payment: PaymentData
)

@Serializable
data class BookingWithServiceAndPayment(
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("Service") val service: ServiceWithPayment
)

@Serializable
data class ServiceWithPayment(
    @SerialName("service_id") val serviceId: Int,
    @SerialName("Payment") val payment: PaymentData
)

@Serializable
data class PropertyWithCreamAndPayment(
    @SerialName("ic_number") val icNumber: String,
    @SerialName("Cream") val cream: CreamWithPayment
)

@Serializable
data class CreamWithPayment(
    @SerialName("cream_id") val creamId: Int,
    @SerialName("Payment") val payment: PaymentData
)

// Data Classes
data class PaymentHistoryItem(
    val id: Int,
    val title: String,
    val amount: Double,
    val date: String,
    val isCredit: Boolean,
    val details: PaymentDetail
)

data class PaymentDetail(
    val paymentTime: String,
    val referenceNumber: String,
    val mobileNumber: String,
    val paymentMethod: String,
    val item: String,
    val subtotal: Double,
    val tax: Double,
    val total: Double
)

// Main Screen
@Composable
fun PaymentHistoryScreen(
    userIc: String,
    onBack: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf("list") }
    var selectedItemId by remember { mutableStateOf<Int?>(null) }
    var historyItems by remember { mutableStateOf<List<PaymentHistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userIc) {
        if (userIc.isEmpty()) {
            isLoading = false
            return@LaunchedEffect
        }
        try {
            val items = withContext(Dispatchers.IO) {
                // 1. Fetch from Smart_Sell (Credits & Sales)
                val sellPayments = SupabaseClient.client.from("Smart_Sell")
                    .select(Columns.raw("*, Payment(*)")) {
                        filter { eq("ic_number", userIc) }
                    }.decodeList<SmartSellWithPayment>()

                // 2. Fetch from Booking (Maintenance/Cleaning/Consultation)
                val bookingPayments = SupabaseClient.client.from("Booking")
                    .select(Columns.raw("*, Service(*, Payment(*))")) {
                        filter { eq("ic_number", userIc) }
                    }.decodeList<BookingWithServiceAndPayment>()

                // 3. Fetch from Property (CREAM Roof Assessment)
                val propertyPayments = SupabaseClient.client.from("Property")
                    .select(Columns.raw("*, Cream(*, Payment(*))")) {
                        filter { eq("ic_number", userIc) }
                    }.decodeList<PropertyWithCreamAndPayment>()
                
                val allMappedItems = mutableListOf<PaymentHistoryItem>()

                sellPayments.forEach { allMappedItems.add(createHistoryItem(it.payment)) }
                bookingPayments.forEach { allMappedItems.add(createHistoryItem(it.service.payment)) }
                propertyPayments.forEach { allMappedItems.add(createHistoryItem(it.cream.payment)) }

                // Sort by date descending
                allMappedItems.sortedByDescending { it.date }
            }
            historyItems = items
        } catch (e: Exception) {
            // Error handling
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF10B981))
        }
        return
    }

    when (currentScreen) {
        "list" -> PaymentHistoryListScreen(
            items = historyItems,
            onBack = onBack,
            onItemClick = { item ->
                selectedItemId = item.id
                currentScreen = "detail"
            }
        )
        "detail" -> {
            val item = historyItems.find { it.id == selectedItemId }
            if (item != null) {
                PaymentHistoryDetailScreen(
                    item = item,
                    onBack = { currentScreen = "list" }
                )
            } else {
                currentScreen = "list"
            }
        }
    }
}

// Helper to convert DB Payment to History Item
private fun createHistoryItem(p: PaymentData): PaymentHistoryItem {
    return PaymentHistoryItem(
        id = p.paymentId ?: 0,
        title = p.title,
        amount = p.amount,
        date = p.date,
        // Transaction is a credit if it involves selling to grid or discharging
        isCredit = p.title.contains("Sell", ignoreCase = true) || 
                   p.title.contains("Discharge", ignoreCase = true) || 
                   p.title.contains("Earnings", ignoreCase = true) ||
                   p.method.contains("Grid", ignoreCase = true),
        details = PaymentDetail(
            paymentTime = p.time,
            referenceNumber = p.referenceNo ?: "N/A", 
            mobileNumber = "", 
            paymentMethod = p.method,
            item = p.title,
            subtotal = p.subtotal,
            tax = p.sst,
            total = p.amount
        )
    )
}

// List Screen
@Composable
fun PaymentHistoryListScreen(
    items: List<PaymentHistoryItem>,
    onBack: () -> Unit,
    onItemClick: (PaymentHistoryItem) -> Unit
) {
    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val backgroundGray = Color(0xFFE2E8F0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    text = "Payment History",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No payment records found.", color = textGray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val grouped = items.groupBy {
                            try {
                                SimpleDateFormat("MMMM yyyy", Locale.US).format(
                                    SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.date) ?: Date()
                                )
                            } catch (e: Exception) {
                                "Other"
                            }
                        }
                        grouped.forEach { (monthYear, groupItems) ->
                            item {
                                Text(
                                    text = monthYear,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textGray,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                            items(groupItems) { item ->
                                PaymentHistoryCard(item, onItemClick)
                            }
                        }
                    }
                }
            }
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 24.dp)
                .size(46.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape
                )
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE2E8F0),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = primaryGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun PaymentHistoryCard(
    item: PaymentHistoryItem,
    onClick: (PaymentHistoryItem) -> Unit
) {
    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick(item) }
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textDark
                )
                Text(
                    text = item.date,
                    fontSize = 12.sp,
                    color = textGray
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${if (item.isCredit) "+" else "-"}RM ${String.format("%.2f", item.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isCredit) primaryGreen else Color(0xFFEF4444),
                    modifier = Modifier.padding(end = 6.dp)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Details",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Detail Screen
@Composable
fun PaymentHistoryDetailScreen(
    item: PaymentHistoryItem,
    onBack: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    if (isPreview) {
        PaymentHistoryDetailContent(
            item = item,
            onBack = onBack,
            onDownloadClick = { /* dummy */ }
        )
        return
    }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            generateAndSavePdf(context, item)
        } else {
            Toast.makeText(context, "Storage permission needed to save receipt", Toast.LENGTH_SHORT).show()
        }
    }

    PaymentHistoryDetailContent(
        item = item,
        onBack = onBack,
        onDownloadClick = {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    generateAndSavePdf(context, item)
                }
            } else {
                generateAndSavePdf(context, item)
            }
        }
    )
}

// Detail content
@Composable
fun PaymentHistoryDetailContent(
    item: PaymentHistoryItem,
    onBack: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val detail = item.details
    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val backgroundGray = Color(0xFFE2E8F0)
    val dividerColor = Color(0xFFE5E7EB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    text = "Payment Details",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Transaction success",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryGreen
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                DetailRow("Payment Time", detail.paymentTime)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = dividerColor)
                DetailRow("Reference Number", detail.referenceNumber)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = dividerColor)
                DetailRow("Mobile Number", detail.mobileNumber)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = dividerColor)
                DetailRow("Payment Method", detail.paymentMethod)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = dividerColor)
                DetailRow("Item", detail.item)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = dividerColor)
                DetailRow("Subtotal (Before Tax)", "RM ${String.format("%.2f", detail.subtotal)}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = dividerColor)
                DetailRow("SST 6%", "RM ${String.format("%.2f", detail.tax)}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = dividerColor)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Amount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(
                        text = "RM ${String.format("%.2f", detail.total)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryGreen,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download Receipt",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 24.dp)
                .size(46.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape
                )
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE2E8F0),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = primaryGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// Helper
@Composable
fun DetailRow(label: String, value: String) {
    val textDark = Color(0xFF1E293B)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textDark
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = textDark,
            textAlign = TextAlign.End
        )
    }
}

// PDF Generation
fun generateAndSavePdf(context: Context, item: PaymentHistoryItem) {
    var pdfDocument: PdfDocument? = null
    try {
        val detail = item.details
        pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        var logoBitmap: Bitmap? = null
        try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.energynest_icon_1)
            if (drawable is android.graphics.drawable.BitmapDrawable) {
                logoBitmap = drawable.bitmap
            } else if (drawable != null) {
                val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 120
                val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 120
                logoBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas2 = android.graphics.Canvas(logoBitmap)
                drawable.setBounds(0, 0, width, height)
                drawable.draw(canvas2)
            }
        } catch (_: Exception) { }

        val titlePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 24f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val headerPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }
        val valuePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
            textAlign = Paint.Align.RIGHT
        }
        val totalPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#10B981")
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        val linePaint = Paint().apply { color = android.graphics.Color.LTGRAY; strokeWidth = 1f }

        var y = 80

        if (logoBitmap != null) {
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 120, 120, true)
            val x = (pageInfo.pageWidth - scaledLogo.width) / 2f
            canvas.drawBitmap(scaledLogo, x, 30f, Paint())
            y = 170
        } else {
            canvas.drawText("EnergyNest", pageInfo.pageWidth / 2f, y.toFloat(), titlePaint)
            y += 40
        }

        canvas.drawText("Payment Receipt", pageInfo.pageWidth / 2f, y.toFloat(), Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 14f
            textAlign = Paint.Align.CENTER
        })
        y += 40
        canvas.drawLine(50f, y.toFloat(), pageInfo.pageWidth - 50f, y.toFloat(), linePaint)
        y += 30
        canvas.drawText("✓ Payment Successful", 50f, y.toFloat(), Paint().apply {
            color = android.graphics.Color.parseColor("#10B981")
            textSize = 16f
            isFakeBoldText = true
        })
        y += 40

        fun drawRow(label: String, value: String) {
            canvas.drawText(label, 50f, y.toFloat(), headerPaint)
            canvas.drawText(value, pageInfo.pageWidth - 50f, y.toFloat(), valuePaint)
            y += 30
            canvas.drawLine(50f, y.toFloat(), pageInfo.pageWidth - 50f, y.toFloat(), linePaint)
            y += 20
        }

        drawRow("Payment Time", detail.paymentTime)
        drawRow("Reference Number", detail.referenceNumber)
        drawRow("Mobile Number", detail.mobileNumber)
        drawRow("Payment Method", detail.paymentMethod)
        drawRow("Item", detail.item)
        drawRow("Subtotal (Before Tax)", "RM ${String.format("%.2f", detail.subtotal)}")
        drawRow("SST 6%", "RM ${String.format("%.2f", detail.tax)}")

        canvas.drawText("Amount", 50f, y.toFloat(), Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        })
        canvas.drawText("RM ${String.format("%.2f", detail.total)}", pageInfo.pageWidth - 50f, y.toFloat(), totalPaint)
        y += 40
        canvas.drawText("Thank you for your payment", pageInfo.pageWidth / 2f, y.toFloat(), Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 12f
            textAlign = Paint.Align.CENTER
        })

        pdfDocument.finishPage(page)

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "Receipt_${detail.referenceNumber}_$timestamp.pdf"
        var fileUri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                fileUri = it
                Toast.makeText(context, "Receipt saved to Downloads", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(context, "Failed to save receipt", Toast.LENGTH_SHORT).show()
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir != null && downloadsDir.exists()) {
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                fileUri = try {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } catch (e: Exception) {
                    Uri.fromFile(file)
                }
                Toast.makeText(context, "Receipt saved to Downloads", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Downloads folder not available", Toast.LENGTH_SHORT).show()
            }
        }

        fileUri?.let { uri ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Open PDF with")
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
            } else {
                Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument?.close()
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewPaymentHistoryScreen() {
    MaterialTheme {
        PaymentHistoryListScreen(
            items = emptyList(),
            onBack = {},
            onItemClick = {}
        )
    }
}
