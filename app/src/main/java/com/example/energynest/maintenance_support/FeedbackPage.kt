package com.example.energynest.maintenance_support

import com.example.energynest.backend_models.SupabaseClient
import com.example.energynest.backend_models.FeedbackData
import com.example.energynest.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energynest.ui.theme.EnergyNestTheme
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeedbackPage(
    userIc: String,
    onBackClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Keying state by userIc ensures data is cleared when the user logs out/switches
    var feedback by remember(userIc) { mutableStateOf("") }
    var feedbackError by remember(userIc) { mutableStateOf(false) }
    var feedbackMessage by remember(userIc) { mutableStateOf("") }
    var isLoading by remember(userIc) { mutableStateOf(false) }

    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val textGray = Color(0xFF505F76)
    val backgroundGray = Color(0xFFE2E8F0)
    val errorRed = Color(0xFFEF4444)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
            .padding(20.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // Main Feedback Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(
                    start = 20.dp,
                    end = 28.dp,
                    top = 76.dp,
                    bottom = 40.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Feedback",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = textDark
            )

            Text(
                text = "We would love to hear from you. Share your suggestions, comments or report any problems you experienced while using EnergyNest.",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                color = textGray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your Feedback",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textGray,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = feedback,
                onValueChange = {
                    feedback = it
                    feedbackError = false
                    feedbackMessage = ""
                },
                label = {
                    Text(text = "Tell us what you think")
                },
                placeholder = {
                    Text(text = "Enter your feedback here...")
                },
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp),
                isError = feedbackError
            )

            if (feedbackMessage.isNotEmpty()) {
                Text(
                    text = feedbackMessage,
                    color = if (feedbackError) {
                        errorRed
                    } else {
                        primaryGreen
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (feedback.trim().isBlank()) {
                        feedbackError = true
                        feedbackMessage =
                            "Please enter your feedback before submitting."
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val now = Date()
                                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
                                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)

                                val feedbackData = FeedbackData(
                                    icNumber = userIc,
                                    content = feedback,
                                    date = dateStr,
                                    time = timeStr
                                )

                                withContext(Dispatchers.IO) {
                                    SupabaseClient.client.from("Feedback")
                                        .insert(feedbackData)
                                }

                                feedbackError = false
                                feedbackMessage =
                                    "Thank you! Your feedback has been submitted successfully."
                                feedback = ""
                            } catch (e: Exception) {
                                feedbackError = true
                                feedbackMessage = "Failed to submit feedback: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
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
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.send_icon
                        ),
                        contentDescription = "Submit Feedback",
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Submit Feedback",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Modern Back Button
        IconButton(
            onClick = onBackClick,
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
                painter = painterResource(
                    id = R.drawable.back_arrow
                ),
                contentDescription = "Back",
                tint = primaryGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackPreview() {
    EnergyNestTheme {
        FeedbackPage(userIc = "123456789012")
    }
}
