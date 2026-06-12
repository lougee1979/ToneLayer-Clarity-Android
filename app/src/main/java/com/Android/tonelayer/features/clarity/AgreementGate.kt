// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.clarity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.Android.tonelayer.ui.theme.BrandVioletDark

const val PREF_CLARITY_AGREEMENT_ACCEPTED = "clarityBetaAgreementAccepted.v1"

private val GateTitleColor = Color(0xFF381F95)
private val GateBodyColor = Color(0xFF1F1A38)
private val GateGradientTop = Color(0xFFEAE5FB)
private val GateGradientBottom = Color(0xFFBFD1FE)

@Composable
fun ClarityAgreementGate(onAccepted: () -> Unit) {
    val context = LocalContext.current
    var accepted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(GateGradientTop, GateGradientBottom)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Balance,
                    contentDescription = null,
                    tint = BrandVioletDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Clarity",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = GateTitleColor
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = "Beta Testing Agreement",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.55f))
            ) {
                Text(
                    text = agreementText,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = GateBodyColor,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clickable { accepted = !accepted },
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (accepted) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        contentDescription = null,
                        tint = if (accepted) BrandVioletDark else Color.Gray,
                        modifier = Modifier.width(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "I have read and agree to the Clarity Beta Testing Agreement, including use of the Clarity keyboard extension.",
                        fontSize = 13.sp,
                        color = GateBodyColor
                    )
                }

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Button(
                    onClick = {
                        if (!accepted) return@Button
                        context.getSharedPreferences("tonelayer_clarity_prefs", android.content.Context.MODE_PRIVATE)
                            .edit { putBoolean(PREF_CLARITY_AGREEMENT_ACCEPTED, true) }
                        onAccepted()
                    },
                    enabled = accepted,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandVioletDark,
                        disabledContainerColor = Color(0xFF9999B3)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Enter Clarity",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

fun hasAcceptedClarityAgreement(context: android.content.Context): Boolean {
    return context.getSharedPreferences("tonelayer_clarity_prefs", android.content.Context.MODE_PRIVATE)
        .getBoolean(PREF_CLARITY_AGREEMENT_ACCEPTED, false)
}

private val agreementText = """
Clarity Beta Testing Agreement

Last updated: June 2026

Thank you for testing ToneLayer Clarity. This agreement covers the ToneLayer Clarity app and the ToneLayer Clarity keyboard extension. By accepting and entering the app you agree to the following.

1. INTELLECTUAL PROPERTY — THE APP
ToneLayer Clarity, including its software, code, design, branding, AI prompts, and all associated content, is the exclusive intellectual property of the developer and is protected by copyright law. You may not copy, reproduce, modify, distribute, reverse-engineer, decompile, or create derivative works from ToneLayer Clarity or any of its components without explicit written permission from the developer. Unauthorized use constitutes copyright infringement and may result in legal action.

2. YOU OWN WHAT YOU PROCESS
You confirm that you have the right to share and process any text you enter into ToneLayer Clarity or the ToneLayer Clarity keyboard. Do not paste or submit text that belongs to someone else or that you do not have explicit permission to use. ToneLayer Clarity is not responsible for any copyright or intellectual-property claims arising from text you submit.

3. BETA SOFTWARE — NO WARRANTIES
ToneLayer Clarity is beta software. Features may change, crash, or produce unexpected results at any time without notice. Outputs are provided as-is and accuracy is not guaranteed. The developer is not liable for any direct or indirect loss, harm, or misunderstanding resulting from use during the beta period.

4. NOT A SUBSTITUTE FOR PROFESSIONAL HELP
ToneLayer Clarity is a communication aid. It is not a medical device, therapy tool, diagnostic service, or source of legal advice. It does not provide clinical, psychological, or legal guidance. If you need professional support, please speak with a qualified professional.

5. YOUR TEXT IS PROCESSED ON OUR SERVER
Messages you type in the app or keyboard are sent to tonelayer.app for AI processing. Your text is not permanently stored on the server. Do not enter sensitive personal information such as passwords, financial data, or private medical details. By using ToneLayer Clarity you consent to this processing.

6. FEEDBACK
As a beta tester you agree to report bugs, usability issues, and unexpected behavior using the feedback option in the app. Your feedback directly improves the app.

7. CONFIDENTIALITY
Please do not share screenshots or video of beta features publicly without permission from the developer.

8. CHANGES TO THIS AGREEMENT
This agreement may be updated before general release. You will be asked to re-read and accept any material changes.

If you have questions, contact the developer through the app or at the support email provided on the App Store listing.

Thank you for helping make ToneLayer Clarity better.
""".trimIndent()
