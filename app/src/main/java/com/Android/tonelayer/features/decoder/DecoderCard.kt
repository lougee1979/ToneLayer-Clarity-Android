// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.decoder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Android.tonelayer.features.clarity.SegmentedPicker
import com.Android.tonelayer.ui.theme.glassCard

private val DecoderBlue = Color(0xFF1A5CDB)
private val DecoderTranslationInk = Color(0xFF142E6B)
private val DecoderPatternRed = Color(0xFFBF1F1F)
private val DecoderPatternIconRed = Color(0xFFD92626)
private val DecoderInk = Color(0xFF1F242E)
private val DecoderPurple = Color(0xFFB02E9E)
private val DecoderResultsBackground = Color(0xFFE3EDFF)

val decodeSensitivities = listOf("Low", "Medium", "High")

@Composable
fun decoderCard(
    contactName: String,
    onContactNameChange: (String) -> Unit,
    decodeText: String,
    onDecodeTextChange: (String) -> Unit,
    sensitivity: String,
    onSensitivityChange: (String) -> Unit,
    senderAdhd: Boolean,
    onSenderAdhdChange: (Boolean) -> Unit,
    senderAutism: Boolean,
    onSenderAutismChange: (Boolean) -> Unit,
    senderPtsd: Boolean,
    onSenderPtsdChange: (Boolean) -> Unit,
    isDecoding: Boolean,
    status: String,
    result: ClarityDecodeResult?,
    onPaste: () -> Unit,
    onClear: () -> Unit,
    onDecode: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(tint = DecoderBlue, cornerRadius = 18f)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Visibility, contentDescription = null, tint = DecoderBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Decoder", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = DecoderBlue)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Paste a message you received. Clarity reads it — what it actually means, and any patterns worth knowing.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Contact name", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = contactName,
            onValueChange = onContactNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Who sent this?") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = decodeText,
            onValueChange = onDecodeTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 260.dp),
            placeholder = { Text("Paste message here…") }
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onPaste, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Filled.ContentPaste, contentDescription = null, modifier = Modifier.height(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Paste")
            }
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f), enabled = decodeText.isNotEmpty()) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = null, modifier = Modifier.height(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Sensitivity", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        SegmentedPicker(decodeSensitivities, sensitivity, onSensitivityChange)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            when (sensitivity) {
                "Low" -> "Only surfaces clear, strong signals. Recommended."
                "Medium" -> "Flags moderate patterns and clear signals."
                else -> "Flags subtle patterns. May over-flag."
            },
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Sender may have (optional)", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            senderProfileCheckbox("ADHD", senderAdhd, Modifier.weight(1f), onSenderAdhdChange)
            senderProfileCheckbox("Autism", senderAutism, Modifier.weight(1f), onSenderAutismChange)
            senderProfileCheckbox("PTSD", senderPtsd, Modifier.weight(1f), onSenderPtsdChange)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Helps Clarity read bluntness, info-dumps, or literal phrasing as communication style rather than rudeness.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))
        val canDecode = !isDecoding && decodeText.isNotBlank()
        Button(
            onClick = onDecode,
            enabled = canDecode,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = DecoderBlue,
                disabledContainerColor = DecoderBlue.copy(alpha = 0.45f)
            )
        ) {
            if (isDecoding) {
                CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Icon(imageVector = Icons.Filled.Visibility, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isDecoding) "Decoding…" else "Decode", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        if (status.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            val isProgress = status.contains("…")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isProgress) Color.Transparent else Color.Red.copy(alpha = 0.08f))
                    .padding(10.dp)
            ) {
                Text(status, fontSize = 14.sp, color = if (isProgress) Color.Gray else Color.Red)
            }
        }

        if (result != null && result.translation.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            decodeResultsView(result)
        }
    }
}

@Composable
private fun senderProfileCheckbox(label: String, isOn: Boolean, modifier: Modifier = Modifier, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isOn) DecoderResultsBackground else Color(0xFFF1EFF6))
            .clickable { onToggle(!isOn) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isOn) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (isOn) DecoderBlue else Color.Gray,
            modifier = Modifier.width(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun decodeResultsView(result: ClarityDecodeResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DecoderResultsBackground)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = DecoderBlue, modifier = Modifier.height(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("What it’s saying", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DecoderBlue)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(result.translation, fontSize = 16.sp, lineHeight = 22.sp, color = DecoderTranslationInk)

        if (result.patterns.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Flag, contentDescription = null, tint = DecoderPatternRed, modifier = Modifier.height(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Patterns flagged", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DecoderPatternRed)
            }
            Spacer(modifier = Modifier.height(6.dp))
            result.patterns.forEach { pattern ->
                Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        tint = DecoderPatternIconRed,
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(pattern, fontSize = 14.sp, lineHeight = 20.sp, color = DecoderInk)
                }
            }
        }

        if (result.communicationStyle.isNotEmpty() && !result.communicationStyle.lowercase().startsWith("neutral")) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Psychology, contentDescription = null, tint = DecoderPurple, modifier = Modifier.height(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Communication style", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DecoderPurple)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(result.communicationStyle, fontSize = 14.sp, lineHeight = 20.sp, color = DecoderInk)
        }

        if (result.baseline.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = DecoderBlue, modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(result.baseline, fontSize = 12.sp, lineHeight = 18.sp, color = DecoderBlue)
            }
        }

        if (result.tentative) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Baseline still building — read is tentative.",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Gray
            )
        }
    }
}
