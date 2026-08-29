package com.example.energynest

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

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
    onBack: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf("list") }
    var selectedItemId by remember { mutableStateOf<Int?>(null) }
    val historyItems = remember { generateDummyHistory() }

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

// List Screen
@Composable
fun PaymentHistoryListScreen(
    items: List<PaymentHistoryItem>,
    onBack: () -> Unit,
    onItemClick: (PaymentHistoryItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Color.White)
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
                    tint = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }
            Text("Payment History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(40.dp))
        }
        Divider(color = androidx.compose.ui.graphics.Color.LightGray, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val grouped = items.groupBy {
                SimpleDateFormat("MMMM yyyy", Locale.US).format(
                    SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(it.date) ?: Date()
                )
            }
            grouped.forEach { (monthYear, groupItems) ->
                item {
                    Text(monthYear, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                        color = androidx.compose.ui.graphics.Color(0xFF333333),
                        modifier = Modifier.padding(vertical = 8.dp))
                }
                items(groupItems) { item ->
                    PaymentHistoryCard(item, onItemClick)
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryCard(
    item: PaymentHistoryItem,
    onClick: (PaymentHistoryItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick(item) }
            .border(1.dp, androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    color = androidx.compose.ui.graphics.Color(0xFF222222))
                Text(item.date, fontSize = 12.sp, color = androidx.compose.ui.graphics.Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                Text(
                    text = "${if (item.isCredit) "+" else "-"}RM ${String.format("%.2f", item.amount)}",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = if (item.isCredit) androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    else androidx.compose.ui.graphics.Color(0xFFE53935),
                    modifier = Modifier.padding(end = 6.dp)
                )
                Icon(Icons.Default.ChevronRight, contentDescription = "Details",
                    tint = androidx.compose.ui.graphics.Color.Gray, modifier = Modifier.size(20.dp))
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, "Back",
                    tint = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp))
            }
            Text("Payment Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(40.dp))
        }
        Divider(color = androidx.compose.ui.graphics.Color.LightGray, thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color(0xFF4CAF50), modifier = Modifier.size(22.dp))
                Text("Transaction success", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50))
            }
            Spacer(modifier = Modifier.height(20.dp))

            DetailRow("Payment Time", detail.paymentTime)
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            DetailRow("Reference Number", detail.referenceNumber)
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            DetailRow("Mobile Number", detail.mobileNumber)
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            DetailRow("Payment Method", detail.paymentMethod)
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            DetailRow("Item", detail.item)
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            DetailRow("Subtotal (Before Tax)", "RM ${String.format("%.2f", detail.subtotal)}")
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            DetailRow("SST 6%", "RM ${String.format("%.2f", detail.tax)}")
            Divider(modifier = Modifier.padding(vertical = 6.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Amount", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.Black)
                Text("RM ${String.format("%.2f", detail.total)}", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDownloadClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = androidx.compose.ui.graphics.Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Receipt", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White)
            }
        }
    }
}

// Helper
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.Black)
        Text(value, fontSize = 14.sp, color = androidx.compose.ui.graphics.Color(0xFF333333),
            textAlign = TextAlign.End)
    }
}

// PDF Generation – loads logo (PNG, JPG, or Vector) using ContextCompat
fun generateAndSavePdf(context: Context, item: PaymentHistoryItem) {
    var pdfDocument: PdfDocument? = null
    try {
        val detail = item.details
        pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // ---- Load logo using ContextCompat (works for both bitmap and vector) ----
        var logoBitmap: Bitmap? = null
        try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.energynest_icon_1)
            if (drawable is android.graphics.drawable.BitmapDrawable) {
                logoBitmap = drawable.bitmap
            } else if (drawable != null) {
                // Vector drawable – draw to a Bitmap
                val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 120
                val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 120
                logoBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas2 = android.graphics.Canvas(logoBitmap)
                drawable.setBounds(0, 0, width, height)
                drawable.draw(canvas2)
            }
        } catch (_: Exception) { /* ignore – fallback to text */ }

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }
        val valuePaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            textAlign = Paint.Align.RIGHT
        }
        val totalPaint = Paint().apply {
            color = Color.parseColor("#4CAF50")
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }

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

        // Subtitle
        canvas.drawText("Payment Receipt", pageInfo.pageWidth / 2f, y.toFloat(), Paint().apply {
            color = Color.GRAY
            textSize = 14f
            textAlign = Paint.Align.CENTER
        })
        y += 40
        canvas.drawLine(50f, y.toFloat(), pageInfo.pageWidth - 50f, y.toFloat(), linePaint)
        y += 30
        canvas.drawText("✓ Payment Successful", 50f, y.toFloat(), Paint().apply {
            color = Color.parseColor("#4CAF50")
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
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        })
        canvas.drawText("RM ${String.format("%.2f", detail.total)}", pageInfo.pageWidth - 50f, y.toFloat(), totalPaint)
        y += 40
        canvas.drawText("Thank you for your payment", pageInfo.pageWidth / 2f, y.toFloat(), Paint().apply {
            color = Color.GRAY
            textSize = 12f
            textAlign = Paint.Align.CENTER
        })

        pdfDocument.finishPage(page)

        // Save and open (same as before)
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

// Dummy data generator
fun generateDummyHistory(): List<PaymentHistoryItem> {
    return listOf(
        PaymentHistoryItem(1, "Deposit for CREAM Program Request", 50.0, "18/01/2026", false,
            PaymentDetail("05-07-2025, 12:00PM", "NGA267389248DSAACV99", "012-8899-1456",
                "E-WALLET", "Deposit for CREAM Program Request", 48.30, 1.70, 50.00)),
        PaymentHistoryItem(2, "Maintenance Fees For Panel Solar", 100.0, "23/01/2026", false,
            PaymentDetail("23-01-2026, 10:30AM", "MAINT202601231030", "012-8899-1456",
                "VISA", "Maintenance Fees For Panel Solar", 100.00, 0.00, 100.00)),
        PaymentHistoryItem(3, "Cleaning Fees For Panel Solar", 30.0, "31/01/2026", false,
            PaymentDetail("31-01-2026, 09:15AM", "CLEAN202601310915", "012-8899-1456",
                "MASTERCARD", "Cleaning Fees For Panel Solar", 30.00, 0.00, 30.00)),
        PaymentHistoryItem(4, "Sell Electricity to TnB Malaysia", 500.0, "01/02/2026", true,
            PaymentDetail("01-02-2026, 08:00AM", "SELL202602010800", "012-8899-1456",
                "BANK TRANSFER", "Sell Electricity to TnB Malaysia", 500.00, 0.00, 500.00)),
        PaymentHistoryItem(5, "Repair Fees For Panel Solar", 70.0, "21/02/2026", false,
            PaymentDetail("21-02-2026, 11:45AM", "REPAIR202602211145", "012-8899-1456",
                "TOUCH 'N GO", "Repair Fees For Panel Solar", 70.00, 0.00, 70.00)),
        PaymentHistoryItem(6, "Maintenance Fees For Panel Solar", 100.0, "29/02/2026", false,
            PaymentDetail("29-02-2026, 02:00PM", "MAINT202602291400", "012-8899-1456",
                "E-WALLET", "Maintenance Fees For Panel Solar", 100.00, 0.00, 100.00))
    )
}

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewPaymentHistoryScreen() {
    MaterialTheme {
        PaymentHistoryListScreen(
            items = generateDummyHistory(),
            onBack = {},
            onItemClick = {}
        )
    }
}