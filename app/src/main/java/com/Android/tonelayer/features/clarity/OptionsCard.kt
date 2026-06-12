// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.clarity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Android.tonelayer.ui.theme.AppNeutral
import com.Android.tonelayer.ui.theme.BrandVioletDark
import com.Android.tonelayer.ui.theme.BrandVioletMist
import com.Android.tonelayer.ui.theme.glassCard

@Composable
fun optionsCard(
    profile: ClarityProfileSelection,
    onProfileChange: (ClarityProfileSelection) -> Unit,
    showTeaching: Boolean,
    onShowTeachingChange: (Boolean) -> Unit,
    aiConsent: Boolean,
    onAiConsentChange: (Boolean) -> Unit,
    onOpenKeyboardSettings: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(tint = AppNeutral, cornerRadius = 18f)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.Tune, contentDescription = null, tint = BrandVioletDark)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Options", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = BrandVioletDark
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Group, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ND Profile", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Check all that apply. AUDHD = ADHD + Autism combined. Combinations build the AI instructions automatically.",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profileCheckbox("ADHD", profile.adhd, Modifier.weight(1f)) { onProfileChange(profile.copy(adhd = it)) }
                profileCheckbox("Autism", profile.autism, Modifier.weight(1f)) { onProfileChange(profile.copy(autism = it)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profileCheckbox("AUDHD", profile.audhd, Modifier.weight(1f)) { onProfileChange(profile.copy(audhd = it)) }
                profileCheckbox("PTSD", profile.ptsd, Modifier.weight(1f)) { onProfileChange(profile.copy(ptsd = it)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profileCheckbox("CPTSD", profile.cptsd, Modifier.weight(1f)) { onProfileChange(profile.copy(cptsd = it)) }
                Spacer(modifier = Modifier.weight(1f))
            }

            val profileString = profile.buildProfileString()
            if (profileString != "General ND") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = BrandVioletDark, modifier = Modifier.width(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Active: $profileString", fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = BrandVioletDark)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            optionToggle(
                icon = Icons.Filled.Lightbulb,
                title = "Teaching explanations",
                description = "Show the teaching card below the rewrite. Turn off only when you want rewrites without explanations.",
                checked = showTeaching,
                onCheckedChange = onShowTeachingChange
            )

            Spacer(modifier = Modifier.height(14.dp))
            optionToggle(
                icon = Icons.Filled.LockPerson,
                title = "AI processing consent",
                description = "Clarity sends only the message text you choose to clarify to the AI provider for rewriting. Do not include passwords, secrets, or medical record numbers in test messages.",
                checked = aiConsent,
                onCheckedChange = onAiConsentChange
            )

            Spacer(modifier = Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Keyboard, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keyboard", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Enable Clarity as a keyboard to rewrite selected text from other apps.",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onOpenKeyboardSettings,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandVioletDark)
            ) {
                Text("Open Keyboard Settings", color = Color.White)
            }
        }
    }
}

@Composable
private fun profileCheckbox(label: String, isOn: Boolean, modifier: Modifier = Modifier, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isOn) BrandVioletMist else Color(0xFFF1EFF6))
            .clickable { onToggle(!isOn) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isOn) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (isOn) BrandVioletDark else Color.Gray,
            modifier = Modifier.width(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
    }
}

@Composable
private fun optionToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = BrandVioletDark)
        )
    }
}
