// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.Android.tonelayer.features.clarity.ClarityAgreementGate
import com.Android.tonelayer.features.clarity.ClarityProfileSelection
import com.Android.tonelayer.features.clarity.copyToClipboard
import com.Android.tonelayer.features.clarity.composerCard
import com.Android.tonelayer.features.clarity.dailyTipCard
import com.Android.tonelayer.features.clarity.hasAcceptedClarityAgreement
import com.Android.tonelayer.features.clarity.headerCard
import com.Android.tonelayer.features.clarity.incrementMetric
import com.Android.tonelayer.features.clarity.openKeyboardSettings
import com.Android.tonelayer.features.clarity.optionsCard
import com.Android.tonelayer.features.clarity.requestRewrite
import com.Android.tonelayer.features.clarity.shareText
import com.Android.tonelayer.features.clarity.teachingCard
import com.Android.tonelayer.features.decoder.ClarityDecodeEntry
import com.Android.tonelayer.features.decoder.ClarityDecodeResult
import com.Android.tonelayer.features.decoder.ClarityDecodeStore
import com.Android.tonelayer.features.decoder.callDecode
import com.Android.tonelayer.features.decoder.decoderCard
import com.Android.tonelayer.features.decoder.friendlyDecodeFailure
import com.Android.tonelayer.ui.theme.AppSurface
import com.Android.tonelayer.ui.theme.BrandVioletMist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var accepted by remember { mutableStateOf(hasAcceptedClarityAgreement(this)) }
                if (accepted) {
                    ToneLayerApp()
                } else {
                    ClarityAgreementGate(onAccepted = { accepted = true })
                }
            }
        }
    }
}

const val PREFS_NAME = "tonelayer_clarity_prefs"
const val PREF_AI_CONSENT = "ai_processing_consent"
const val PREF_SHOW_TEACHING = "show_teaching_boxes"

@Composable
fun ToneLayerApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var draft by remember { mutableStateOf("") }
    var profile by remember { mutableStateOf(ClarityProfileSelection.fromPrefs(prefs)) }
    var goal by remember { mutableStateOf("Make clearer") }
    var messageDirection by remember { mutableStateOf("They sent this to me") }
    var isRewriting by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var clearerVersion by remember { mutableStateOf("") }
    var interpretationRisk by remember { mutableStateOf("") }
    var changeNotes by remember { mutableStateOf("") }
    var learningTakeaway by remember { mutableStateOf("") }
    var teachingExplanation by remember { mutableStateOf("") }
    var selectedResult by remember { mutableStateOf("Rewrite") }
    var showTeaching by remember { mutableStateOf(prefs.getBoolean(PREF_SHOW_TEACHING, true)) }
    var aiConsent by remember { mutableStateOf(prefs.getBoolean(PREF_AI_CONSENT, false)) }

    val decodeStore = remember { ClarityDecodeStore(context) }
    var decodeContactName by remember { mutableStateOf("") }
    var decodeText by remember { mutableStateOf("") }
    var decodeSensitivity by remember { mutableStateOf("Low") }
    var isDecoding by remember { mutableStateOf(false) }
    var decodeStatus by remember { mutableStateOf("") }
    var decodeResult by remember { mutableStateOf<ClarityDecodeResult?>(null) }
    var senderAdhd by remember { mutableStateOf(prefs.getBoolean("decode.senderProfile.adhd", false)) }
    var senderAutism by remember { mutableStateOf(prefs.getBoolean("decode.senderProfile.autism", false)) }
    var senderPtsd by remember { mutableStateOf(prefs.getBoolean("decode.senderProfile.ptsd", false)) }

    val hasOutput = clearerVersion.isNotEmpty() || interpretationRisk.isNotEmpty() || changeNotes.isNotEmpty()
    val selectedResultText = when (selectedResult) {
        "Original" -> draft
        "Tone" -> interpretationRisk
        else -> clearerVersion
    }
    val resultWindowText = when {
        selectedResult == "Original" -> draft.ifEmpty { "Your original message will show here." }
        !hasOutput -> "Tap Rewrite to see the rewritten version here."
        else -> selectedResultText.ifEmpty { "Nothing to show for this tab yet." }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppSurface, Color.White, BrandVioletMist)))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        headerCard(messageDirection)
        dailyTipCard()
        composerCard(
            draft = draft,
            onDraftChange = { draft = it },
            messageDirection = messageDirection,
            onDirectionChange = { messageDirection = it },
            goal = goal,
            onGoalChange = { goal = it },
            isRewriting = isRewriting,
            status = status,
            selectedResult = selectedResult,
            onSelectResult = { selectedResult = it },
            hasOutput = hasOutput,
            resultWindowText = resultWindowText,
            onPaste = {
                val pasted = pasteFromClipboard(context)
                if (pasted != null) {
                    draft = pasted
                    status = "Pasted ${pasted.length} characters"
                } else {
                    status = "Clipboard is empty"
                }
            },
            onClear = {
                draft = ""
                clearerVersion = ""
                interpretationRisk = ""
                changeNotes = ""
                learningTakeaway = ""
                teachingExplanation = ""
                status = ""
            },
            onRewrite = {
                val input = draft.trim()
                isRewriting = true
                status = "Checking message..."
                selectedResult = "Rewrite"
                requestRewrite(
                    prefs = prefs,
                    scope = scope,
                    aiConsent = aiConsent,
                    input = input,
                    profile = profile,
                    goal = goal,
                    messageDirection = messageDirection,
                    onResult = { result ->
                        clearerVersion = result.clearerVersion
                        interpretationRisk = result.interpretationRisk
                        changeNotes = result.changeNotes
                        learningTakeaway = result.learningTakeaway
                        teachingExplanation = result.teachingExplanation
                        isRewriting = false
                        status = "Ready"
                    }
                )
            },
            onCopy = {
                copyToClipboard(context, selectedResultText)
                incrementMetric(prefs, "rewrite.copied")
                incrementMetric(prefs, "rewrite.accepted")
                status = "Copied $selectedResult"
            },
            onReplaceDraft = {
                draft = clearerVersion
                incrementMetric(prefs, "rewrite.replacedDraft")
                incrementMetric(prefs, "rewrite.accepted")
                status = "Draft replaced"
            },
            onHelpful = { helpful ->
                incrementMetric(prefs, if (helpful) "satisfaction.helpful" else "satisfaction.notHelpful")
                status = if (helpful) "Marked helpful" else "Marked for improvement"
            },
            onShare = {
                shareText(context, selectedResultText)
                status = "Choose where to share"
            }
        )

        if (showTeaching) {
            teachingCard(
                hasOutput = hasOutput,
                teachingExplanation = teachingExplanation,
                interpretationRisk = interpretationRisk,
                changeNotes = changeNotes,
                learningTakeaway = learningTakeaway,
                draft = draft,
                profile = profile
            )
        }

        decoderCard(
            contactName = decodeContactName,
            onContactNameChange = { decodeContactName = it },
            decodeText = decodeText,
            onDecodeTextChange = { decodeText = it },
            sensitivity = decodeSensitivity,
            onSensitivityChange = { decodeSensitivity = it },
            senderAdhd = senderAdhd,
            onSenderAdhdChange = {
                senderAdhd = it
                prefs.edit { putBoolean("decode.senderProfile.adhd", it) }
            },
            senderAutism = senderAutism,
            onSenderAutismChange = {
                senderAutism = it
                prefs.edit { putBoolean("decode.senderProfile.autism", it) }
            },
            senderPtsd = senderPtsd,
            onSenderPtsdChange = {
                senderPtsd = it
                prefs.edit { putBoolean("decode.senderProfile.ptsd", it) }
            },
            isDecoding = isDecoding,
            status = decodeStatus,
            result = decodeResult,
            onPaste = {
                val pasted = pasteFromClipboard(context)
                if (pasted != null) {
                    decodeText = pasted
                } else {
                    decodeStatus = "Clipboard is empty"
                }
            },
            onClear = {
                decodeText = ""
                decodeResult = null
                decodeStatus = ""
            },
            onDecode = {
                val trimmed = decodeText.trim()
                val textToDecode = trimmed.ifEmpty { pasteFromClipboard(context)?.trim().orEmpty() }
                if (textToDecode.isEmpty()) {
                    decodeStatus = "Nothing to decode — copy a message first."
                } else {
                    if (trimmed.isEmpty()) decodeText = textToDecode
                    isDecoding = true
                    decodeStatus = "Decoding…"
                    decodeResult = null
                    val contact = decodeContactName.trim()
                    val history = decodeStore.messagesFor(contact)
                    val senderProfile = listOfNotNull(
                        "ADHD".takeIf { senderAdhd },
                        "Autism".takeIf { senderAutism },
                        "PTSD".takeIf { senderPtsd }
                    ).joinToString(", ")
                    scope.launch {
                        val outcome = withContext(Dispatchers.IO) {
                            runCatching { callDecode(textToDecode, contact, decodeSensitivity, senderProfile, history) }
                        }
                        outcome.onSuccess { result ->
                            isDecoding = false
                            decodeStatus = ""
                            decodeResult = result
                            decodeStore.append(
                                ClarityDecodeEntry(
                                    id = UUID.randomUUID().toString(),
                                    timestamp = System.currentTimeMillis(),
                                    contact = contact.ifEmpty { "Unknown" },
                                    text = textToDecode,
                                    sensitivity = decodeSensitivity,
                                    translation = result.translation,
                                    patterns = result.patterns,
                                    baseline = result.baseline
                                )
                            )
                        }.onFailure { error ->
                            isDecoding = false
                            decodeStatus = friendlyDecodeFailure(error)
                        }
                    }
                }
            }
        )

        optionsCard(
            profile = profile,
            onProfileChange = {
                profile = it
                it.saveToPrefs(prefs)
            },
            showTeaching = showTeaching,
            onShowTeachingChange = {
                showTeaching = it
                prefs.edit { putBoolean(PREF_SHOW_TEACHING, it) }
            },
            aiConsent = aiConsent,
            onAiConsentChange = {
                aiConsent = it
                prefs.edit { putBoolean(PREF_AI_CONSENT, it) }
            },
            onOpenKeyboardSettings = { openKeyboardSettings(context) }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

private fun pasteFromClipboard(context: Context): String? {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = clipboard.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.text?.toString()
    return text?.takeIf { it.isNotEmpty() }
}
