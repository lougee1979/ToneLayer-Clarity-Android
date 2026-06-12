// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.clarity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Android.tonelayer.R
import com.Android.tonelayer.ui.theme.BrandVioletDark
import com.Android.tonelayer.ui.theme.BrandVioletMist
import com.Android.tonelayer.ui.theme.glassCard

val messageDirections = listOf("They sent this to me", "I'm about to send this")
val rewriteGoals = listOf("Make clearer", "Reduce anxiety", "Make actionable")
val resultTabs = listOf("Original", "Rewrite", "Tone")

@Composable
fun SegmentedPicker(options: List<String>, selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val isSelected = option == selected
            if (isSelected) {
                Button(
                    onClick = { onSelect(option) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandVioletDark)
                ) {
                    Text(option, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            } else {
                OutlinedButton(
                    onClick = { onSelect(option) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandVioletDark)
                ) {
                    Text(option, fontSize = 12.sp, maxLines = 1)
                }
            }
            if (index != options.lastIndex) Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@Composable
fun headerCard(messageDirection: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(tint = BrandVioletDark, cornerRadius = 18f)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.tonelayer_clarity_logo),
            contentDescription = null,
            modifier = Modifier
                .height(88.dp)
                .width(88.dp)
                .clip(RoundedCornerShape(22.dp))
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "ToneLayer Clarity",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = BrandVioletDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Rewrite NT speech so it lands more clearly for neurodivergent readers.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            statusPill(label = "Mode", value = "Clarity", modifier = Modifier.weight(1f))
            statusPill(
                label = "Direction",
                value = if (messageDirection == "I'm about to send this") "ND → NT" else "NT → ND",
                modifier = Modifier.weight(1f)
            )
            statusPill(label = "Server", value = "✓ railway.app", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun statusPill(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = BrandVioletMist,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandVioletDark, maxLines = 1)
        }
    }
}

@Composable
fun dailyTipCard() {
    val tip = todayTip()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(tint = BrandVioletDark, cornerRadius = 18f)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.TipsAndUpdates, contentDescription = null, tint = com.Android.tonelayer.ui.theme.BrandGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text("FYI of the day", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(tip.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(tip.body, fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFF38424D))
    }
}

@Composable
fun composerCard(
    draft: String,
    onDraftChange: (String) -> Unit,
    messageDirection: String,
    onDirectionChange: (String) -> Unit,
    goal: String,
    onGoalChange: (String) -> Unit,
    isRewriting: Boolean,
    status: String,
    selectedResult: String,
    onSelectResult: (String) -> Unit,
    hasOutput: Boolean,
    resultWindowText: String,
    onPaste: () -> Unit,
    onClear: () -> Unit,
    onRewrite: () -> Unit,
    onCopy: () -> Unit,
    onReplaceDraft: () -> Unit,
    onHelpful: (Boolean) -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(tint = BrandVioletDark, cornerRadius = 18f)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = BrandVioletDark)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Message Check", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            if (draft.isNotEmpty()) {
                Text("${draft.length} chars", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Whose message is this?", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        SegmentedPicker(messageDirections, messageDirection, onDirectionChange)

        Spacer(modifier = Modifier.height(10.dp))
        Text("Goal", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        SegmentedPicker(rewriteGoals, goal, onGoalChange)

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = draft,
            onValueChange = onDraftChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp, max = 240.dp),
            placeholder = { Text("Paste what you were going to say...") }
        )

        Spacer(modifier = Modifier.height(6.dp))
        val words = draft.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        Text("${draft.length} chars • $words words", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onPaste, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Filled.ContentPaste, contentDescription = null, modifier = Modifier.height(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Paste")
            }
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f), enabled = draft.isNotEmpty()) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = null, modifier = Modifier.height(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        val canRewrite = !isRewriting && draft.isNotBlank()
        Button(
            onClick = onRewrite,
            enabled = canRewrite,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandVioletDark,
                disabledContainerColor = BrandVioletDark.copy(alpha = 0.45f)
            )
        ) {
            if (isRewriting) {
                CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isRewriting) "Tuning…" else "Rewrite", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        if (status.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(status, fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            resultTabs.forEach { tab ->
                val selected = selectedResult == tab
                if (selected) {
                    Button(
                        onClick = { onSelectResult(tab) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandVioletDark)
                    ) {
                        Text(tab, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                } else {
                    OutlinedButton(
                        onClick = { onSelectResult(tab) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandVioletDark)
                    ) {
                        Text(tab, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp, max = 280.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color = BrandVioletMist.copy(alpha = 0.6f))
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            SelectionContainer {
                Text(resultWindowText, fontSize = 15.sp, lineHeight = 22.sp, color = Color(0xFF1F2630))
            }
        }

        if (hasOutput) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandVioletDark)
                ) {
                    Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.height(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy", color = Color.White)
                }
                OutlinedButton(onClick = onReplaceDraft, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = null, modifier = Modifier.height(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Replace Draft")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { onHelpful(true) }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.height(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Helpful")
                }
                OutlinedButton(onClick = { onHelpful(false) }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Filled.ThumbDown, contentDescription = null, modifier = Modifier.height(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Not Yet")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onShare,
            enabled = hasOutput,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandVioletDark,
                disabledContainerColor = BrandVioletDark.copy(alpha = 0.45f)
            )
        ) {
            Icon(imageVector = Icons.Filled.Share, contentDescription = null, tint = Color.White, modifier = Modifier.height(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share", color = Color.White)
        }
    }
}

@Composable
fun teachingCard(
    hasOutput: Boolean,
    teachingExplanation: String,
    interpretationRisk: String,
    changeNotes: String,
    learningTakeaway: String,
    draft: String,
    profile: ClarityProfileSelection
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BrandVioletMist.copy(alpha = 0.5f))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = BrandVioletDark)
            Spacer(modifier = Modifier.width(8.dp))
            Text("How this lands", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = BrandVioletDark)
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (hasOutput) {
            if (teachingExplanation.isNotEmpty()) {
                Text(teachingExplanation, fontSize = 15.sp, lineHeight = 22.sp)
            }
            if (interpretationRisk.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("How this may sound:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Text(interpretationRisk, fontSize = 14.sp, lineHeight = 20.sp)
            }
            if (changeNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("What changed:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Text(changeNotes, fontSize = 14.sp, lineHeight = 20.sp)
            }
            if (learningTakeaway.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandVioletMist)
                        .padding(12.dp)
                ) {
                    Text(learningTakeaway, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
                }
            }
        } else if (draft.isNotBlank()) {
            Text(createClarityAnalysis(draft, profile), fontSize = 14.sp, color = Color(0xFF38424D), lineHeight = 20.sp)
        } else {
            Text(
                "Paste a message above and tap Rewrite to see how it may land for a neurodivergent reader.",
                fontSize = 15.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )
        }
    }
}
