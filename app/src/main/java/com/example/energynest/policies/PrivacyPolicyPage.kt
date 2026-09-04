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
fun PrivacyPolicyPage(
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

            // Main Privacy Policy Card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
                    .background(
                        color = Color.White,
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

                Text(
                    text = "Privacy Policy",
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

                PrivacyBodyText(
                    text = "EnergyNest is committed to protecting your privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application."
                )

                PrivacySectionTitle(
                    title = "1. INFORMATION WE COLLECT"
                )

                PrivacyBodyText(
                    text = "We may collect the following types of information:"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrivacyBulletText(
                    text = "Personal Identification Information: name, email address, phone number, IC number, and physical address."
                )

                PrivacyBulletText(
                    text = "Energy Usage Data: solar production, battery storage, and consumption patterns."
                )

                PrivacyBulletText(
                    text = "Location Data: with your permission, we collect precise location to help you find solar installation services and map-based features."
                )

                PrivacyBulletText(
                    text = "Device Information: device model, operating system, and unique device identifiers."
                )

                PrivacySectionTitle(
                    title = "2. HOW WE USE YOUR INFORMATION"
                )

                PrivacyBodyText(
                    text = "We use the collected data for:"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrivacyBulletText(
                    text = "Providing and maintaining the app's core features, including monitoring, energy optimisation, and leasing."
                )

                PrivacyBulletText(
                    text = "Processing your participation in the CREAM programme and government incentives."
                )

                PrivacyBulletText(
                    text = "Improving user experience and developing new features."
                )

                PrivacyBulletText(
                    text = "Sending you updates, promotional offers, and service notifications. You may opt out at any time."
                )

                PrivacySectionTitle(
                    title = "3. SHARING YOUR INFORMATION"
                )

                PrivacyBodyText(
                    text = "We do not sell your personal data. We may share your information only:"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrivacyBulletText(
                    text = "With government agencies, such as Suruhanjaya Tenaga, for subsidy and incentive processing."
                )

                PrivacyBulletText(
                    text = "With third-party service providers who assist us in operating the app, such as cloud storage and analytics providers."
                )

                PrivacyBulletText(
                    text = "When required by law or to protect our legal rights."
                )

                PrivacySectionTitle(
                    title = "4. DATA SECURITY"
                )

                PrivacyBodyText(
                    text = "We implement industry-standard security measures to protect your data from unauthorised access, alteration, or disclosure. However, no method of transmission over the internet is 100% secure."
                )

                PrivacySectionTitle(
                    title = "5. YOUR RIGHTS"
                )

                PrivacyBodyText(
                    text = "You have the right to:"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrivacyBulletText(
                    text = "Access, update, or delete your personal information at any time."
                )

                PrivacyBulletText(
                    text = "Withdraw consent for data processing."
                )

                PrivacyBulletText(
                    text = "Request a copy of the data we hold about you."
                )

                PrivacySectionTitle(
                    title = "6. CHILDREN'S PRIVACY"
                )

                PrivacyBodyText(
                    text = "Our app is not intended for children under 13. We do not knowingly collect personal information from children."
                )

                PrivacySectionTitle(
                    title = "7. CHANGES TO THIS POLICY"
                )

                PrivacyBodyText(
                    text = "We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new policy on this page."
                )

                PrivacySectionTitle(
                    title = "8. CONTACT US"
                )

                PrivacyBodyText(
                    text = "If you have any questions about this Privacy Policy, please contact us at:"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "support@energynest.com",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryGreen,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))
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
fun PrivacySectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(
            top = 24.dp,
            bottom = 12.dp
        )
    )
}

@Composable
fun PrivacyBodyText(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        lineHeight = 25.sp,
        color = Color(0xFF1E293B),
        textAlign = TextAlign.Justify,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PrivacyBulletText(text: String) {
    Text(
        text = "• $text",
        fontSize = 15.sp,
        lineHeight = 25.sp,
        color = Color(0xFF1E293B),
        textAlign = TextAlign.Justify,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 8.dp,
                bottom = 6.dp
            )
    )
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyPreview() {
    EnergyNestTheme {
        PrivacyPolicyPage()
    }
}