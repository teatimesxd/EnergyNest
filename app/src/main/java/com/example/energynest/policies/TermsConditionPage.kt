package com.example.energynest.policies

import com.example.energynest.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energynest.ui.theme.EnergyNestTheme

@Composable
fun TermsConditionPage(
    onBackClick: () -> Unit = {}
) {
    val primaryGreen = Color(0xFF10B981)
    val textDark = Color(0xFF1E293B)
    val backgroundGray = Color(0xFFE2E8F0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundGray
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(
                        start = 28.dp,
                        end = 28.dp,
                        top = 76.dp,
                        bottom = 40.dp
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Text(
                    text = "Terms & Conditions",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                    color = primaryGreen
                )

                Spacer(modifier = Modifier.height(24.dp))

                TermsSectionTitle(title = "1. ACCEPTANCE OF TERMS")
                TermsBodyText(
                    text = "By accessing or using the EnergyNest mobile application, you agree to comply with these Terms and Conditions. If you do not agree with any part of these terms, please do not use the application."
                )

                TermsSectionTitle(title = "2. USER ACCOUNT")
                TermsBodyText(
                    text = "You are responsible for providing accurate and complete information when creating an EnergyNest account. You are also responsible for maintaining the confidentiality of your account credentials and for activities performed using your account."
                )

                TermsSectionTitle(title = "3. USE OF ENERGYNEST SERVICES")
                TermsBodyText(
                    text = "EnergyNest provides features and information related to energy monitoring, energy consumption, solar energy and other available services. The application should only be used for lawful purposes and according to these Terms and Conditions."
                )

                TermsSectionTitle(title = "4. USER RESPONSIBILITIES")
                TermsBodyText(
                    text = "Users must not misuse the application, attempt to access another user's account, interfere with the application's operation or use the application for unlawful activities."
                )

                TermsSectionTitle(title = "5. INFORMATION ACCURACY")
                TermsBodyText(
                    text = "EnergyNest aims to provide accurate information and services. However, information displayed in the application may occasionally contain errors, delays or inaccuracies. Users should verify important information when necessary."
                )

                TermsSectionTitle(title = "6. SERVICE AVAILABILITY")
                TermsBodyText(
                    text = "EnergyNest may modify, update, temporarily suspend or discontinue certain features or services without prior notice when necessary for maintenance, improvements or other operational reasons."
                )

                TermsSectionTitle(title = "7. INTELLECTUAL PROPERTY")
                TermsBodyText(
                    text = "The EnergyNest application, including its design, logo, text, graphics and other content, is protected by applicable intellectual property laws. Users may not copy, reproduce or distribute the application's content without permission."
                )

                TermsSectionTitle(title = "8. LIMITATION OF LIABILITY")
                TermsBodyText(
                    text = "EnergyNest is not responsible for indirect losses, damages or interruptions resulting from the use or inability to use the application, except where liability is required by applicable law."
                )

                TermsSectionTitle(title = "9. CHANGES TO TERMS")
                TermsBodyText(
                    text = "We may update these Terms and Conditions from time to time. Continued use of EnergyNest after changes are published means that you accept the updated Terms and Conditions."
                )

                TermsSectionTitle(title = "10. CONTACT US")
                TermsBodyText(
                    text = "If you have any questions regarding these Terms and Conditions, please contact the EnergyNest support team through the available support channels."
                )

                Spacer(modifier = Modifier.height(40.dp))
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
}

@Composable
fun TermsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    )
}

@Composable
fun TermsBodyText(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        lineHeight = 25.sp,
        color = Color(0xFF1E293B),
        textAlign = TextAlign.Justify,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun TermsConditionsPreview() {
    EnergyNestTheme {
        TermsConditionPage()
    }
}
