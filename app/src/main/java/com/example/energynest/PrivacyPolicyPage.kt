package com.example.energynest

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.energynest.ui.theme.EnergyNestTheme

@Composable
fun PrivacyPolicyPage() {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFE2E8F0)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                        .padding(horizontal = 32.dp, vertical = 40.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ----- TITLE -----
                    Text(
                        text = "Privacy Policy",
                        fontSize = 36.sp,           // bigger
                        fontWeight = FontWeight.ExtraBold, // bolder
                        color = Color(0xFF0E4B3A),  // deep green for formality
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Formal divider
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color(0xFF10B981)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ----- BODY TEXT (with enhanced section headings) -----
                    Text(
                        text = """
                            EnergyNest is committed to protecting your privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application.

                            1. INFORMATION WE COLLECT
                            We may collect the following types of information:
                            • Personal Identification Information: name, email address, phone number, IC number, and physical address.
                            • Energy Usage Data: solar production, battery storage, and consumption patterns.
                            • Location Data: with your permission, we collect precise location to help you find solar installation services and map-based features.
                            • Device Information: device model, operating system, and unique device identifiers.

                            2. HOW WE USE YOUR INFORMATION
                            We use the collected data for:
                            • Providing and maintaining the app’s core features (monitoring, energy optimisation, leasing).
                            • Processing your participation in the CREAM programme and government incentives.
                            • Improving user experience and developing new features.
                            • Sending you updates, promotional offers, and service notifications (you may opt out at any time).

                            3. SHARING YOUR INFORMATION
                            We do not sell your personal data. We may share your information only:
                            • With government agencies (e.g., Suruhanjaya Tenaga) for subsidy and incentive processing.
                            • With third‑party service providers who assist us in operating the app (e.g., cloud storage, analytics).
                            • When required by law or to protect our legal rights.

                            4. DATA SECURITY
                            We implement industry‑standard security measures to protect your data from unauthorised access, alteration, or disclosure. However, no method of transmission over the internet is 100% secure.

                            5. YOUR RIGHTS
                            You have the right to:
                            • Access, update, or delete your personal information at any time.
                            • Withdraw consent for data processing.
                            • Request a copy of the data we hold about you.

                            6. CHILDREN'S PRIVACY
                            Our app is not intended for children under 13. We do not knowingly collect personal information from children.

                            7. CHANGES TO THIS POLICY
                            We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new policy on this page.

                            8. CONTACT US
                            If you have any questions about this Privacy Policy, please contact us at:
                            support@energynest.com
                        """.trimIndent(),
                        fontSize = 15.sp,
                        lineHeight = 26.sp,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Back button
            IconButton(
                onClick = {
                    (context as? Activity)?.finish()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 30.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyPreview() {
    EnergyNestTheme {
        PrivacyPolicyPage()
    }
}