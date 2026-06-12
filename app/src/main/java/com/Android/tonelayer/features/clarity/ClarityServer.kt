// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.clarity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

const val TL_SERVER_URL = "https://tonelayer-server-production.up.railway.app"
const val TL_APP_TOKEN = "d731136d97cdd46453e7581465537e0d9aee811512b885c2"

/** Kept for the keyboard's quick rewrite chips (Clarify/Shorter/Warmer/Direct). */
enum class RewriteStyle(val buttonLabel: String, val resultTitle: String) {
    CLEAR("Clarify", "ND rewrite"),
    SHORTER("Shorter", "Shorter ND rewrite"),
    WARMER("Warmer", "Warmer ND rewrite"),
    DIRECT("Direct", "Direct ND rewrite")
}

data class ClarityRewriteResult(
    val clearerVersion: String,
    val interpretationRisk: String,
    val changeNotes: String,
    val learningTakeaway: String,
    val teachingExplanation: String
)

class ClarityHttpException(
    val statusCode: Int,
    private val responseBody: String
) : Exception("Clarity server request failed with HTTP $statusCode") {
    fun bodyLowercase(): String = responseBody.lowercase()
}

/** Matches the iOS app's `callServer(text:)` — POST `/rewrite` on the shared production server. */
fun callClarityServer(
    text: String,
    profile: ClarityProfileSelection,
    goal: String,
    messageDirection: String
): ClarityRewriteResult {
    val direction = if (messageDirection == "I'm about to send this") "outgoing" else "incoming"
    val body = JSONObject().apply {
        put("text", text)
        put("profile", profile.buildProfileString())
        put("level", goal)
        put("mode", "clarity")
        put("style", "Clarify")
        put("direction", direction)
    }

    val conn = (URL("$TL_SERVER_URL/rewrite").openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        connectTimeout = 30000
        readTimeout = 90000
        doOutput = true
        setRequestProperty("x-app-token", TL_APP_TOKEN)
        setRequestProperty("Content-Type", "application/json")
    }
    OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
    val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
    val response = stream.bufferedReader().use { it.readText() }
    if (conn.responseCode !in 200..299) throw ClarityHttpException(conn.responseCode, response)

    val parsed = JSONObject(response)
    val paragraphs = parsed.optJSONArray("paragraphs")
    val clearer = when {
        parsed.optString("clearer_version").isNotEmpty() -> parsed.getString("clearer_version")
        paragraphs != null && paragraphs.length() > 0 ->
            (0 until paragraphs.length()).joinToString("\n\n") { paragraphs.getString(it) }
        parsed.optString("rewrite").isNotEmpty() -> parsed.getString("rewrite")
        else -> text
    }
    return ClarityRewriteResult(
        clearerVersion = clearer,
        interpretationRisk = parsed.optString("interpretation_risk", ""),
        changeNotes = parsed.optString("change_notes", ""),
        learningTakeaway = parsed.optString("learning_takeaway", ""),
        teachingExplanation = parsed.optString("teaching_explanation", parsed.optString("explanation", ""))
    )
}

fun friendlyRewriteFailure(error: Throwable): String {
    return when {
        error is ClarityHttpException && error.statusCode == 429 ->
            "The Clarity server is rate-limiting requests right now. Showing a local rewrite for now."
        error is ClarityHttpException && error.statusCode in 500..599 ->
            "The Clarity server is having an issue right now. Showing a local rewrite for now."
        else ->
            "Live rewrite is unavailable right now. Showing a local rewrite for now."
    }
}

/**
 * Local (offline) fallback used when AI processing consent is off, or the
 * server request fails. This is an Android-only addition beyond iOS parity.
 */
fun requestRewrite(
    prefs: SharedPreferences,
    scope: CoroutineScope,
    aiConsent: Boolean,
    input: String,
    profile: ClarityProfileSelection,
    goal: String,
    messageDirection: String,
    onResult: (ClarityRewriteResult) -> Unit
) {
    incrementMetric(prefs, "rewrite.requested")
    if (input.length >= 700 || input.split(Regex("\\s+")).filter { it.isNotBlank() }.size >= 120) {
        incrementMetric(prefs, "longMessage.flagged")
    }
    scope.launch {
        val result = withContext(Dispatchers.IO) {
            if (!aiConsent) {
                ClarityRewriteResult(
                    clearerVersion = createRewriteResult(input, profile, RewriteStyle.CLEAR),
                    interpretationRisk = "",
                    changeNotes = "",
                    learningTakeaway = "",
                    teachingExplanation = fallbackTeaching(
                        input,
                        profile,
                        RewriteStyle.CLEAR,
                        "Turn on AI processing consent in Options to use live rewrites."
                    )
                )
            } else {
                runCatching { callClarityServer(input, profile, goal, messageDirection) }
                    .getOrElse {
                        ClarityRewriteResult(
                            clearerVersion = createRewriteResult(input, profile, RewriteStyle.CLEAR),
                            interpretationRisk = "",
                            changeNotes = "",
                            learningTakeaway = "",
                            teachingExplanation = fallbackTeaching(input, profile, RewriteStyle.CLEAR, friendlyRewriteFailure(it))
                        )
                    }
            }
        }
        onResult(result)
        if (result.clearerVersion.isNotBlank()) {
            incrementMetric(prefs, "rewrite.success")
        }
    }
}

fun incrementMetric(prefs: SharedPreferences, key: String, amount: Int = 1) {
    val fullKey = "metrics.$key"
    prefs.edit {
        putInt(fullKey, prefs.getInt(fullKey, 0) + amount)
        putLong("metrics.lastUpdated", System.currentTimeMillis())
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Clarity rewrite", text))
}

fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Send rewrite"))
}

fun openKeyboardSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
}

fun createRewriteResult(input: String, profile: ClarityProfileSelection, style: RewriteStyle): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) {
        return "Enter a message above, then tap Rewrite."
    }

    val lower = trimmed.lowercase()
    if (lower == "we need to talk" || lower == "we need to talk.") {
        return when (style) {
            RewriteStyle.CLEAR -> "Can we set aside a few minutes to talk about something specific? I do not want to leave this vague or stressful. I would like to explain what is on my mind and agree on next steps."
            RewriteStyle.SHORTER -> "Can we set a time to talk about something specific? I want to be clear and avoid making this feel vague."
            RewriteStyle.WARMER -> "Can we talk when you have a little time? I want to explain something clearly, and I do not want the message to sound alarming or vague."
            RewriteStyle.DIRECT -> "Can we set a time to talk about [topic]? I want to discuss what happened and decide what to do next."
        }
    }

    val clarityFrame = profile.buildProfileInstructions()
    val brief = trimmed.split(Regex("\\s+")).take(28).joinToString(" ")

    return when (style) {
        RewriteStyle.CLEAR -> """
ND-clear version:

$trimmed

Intent: communicate clearly, accounting for $clarityFrame
""".trimIndent()
        RewriteStyle.SHORTER -> """
Brief version:

$brief
""".trimIndent()
        RewriteStyle.WARMER -> """
Warmer version:

$trimmed

I want this to come across with connection and clarity, not pressure.
""".trimIndent()
        RewriteStyle.DIRECT -> """
Direct version:

$trimmed

Please let me know what works for you.
""".trimIndent()
    }
}

fun fallbackTeaching(
    input: String,
    profile: ClarityProfileSelection,
    style: RewriteStyle,
    setupOrFailureMessage: String? = null
): String {
    val specificNote = if (input.trim().lowercase().removeSuffix(".") == "we need to talk") {
        "The original phrase can create anxiety because it does not say the topic, urgency, emotional intent, or requested action. The rewrite names that a conversation is needed, lowers the threat level, and asks for a concrete time."
    } else {
        val styleNote = when (style) {
            RewriteStyle.CLEAR -> "The rewrite makes NT subtext explicit so the ND reader does not have to infer the real point."
            RewriteStyle.SHORTER -> "The rewrite reduces working-memory load and keeps the requested action easy to find."
            RewriteStyle.WARMER -> "The rewrite adds reassurance and context so warmth is stated instead of implied."
            RewriteStyle.DIRECT -> "The rewrite states the topic, request, and timing more concretely."
        }
        val profileNote = if (profile.buildProfileString() == "General ND") {
            "It also clarifies intent, timing, tone, and requested action."
        } else {
            "It also addresses ${profile.buildProfileString()} needs: ${profile.buildProfileInstructions()}"
        }
        "$styleNote $profileNote"
    }
    return listOfNotNull(setupOrFailureMessage, specificNote).joinToString("\n\n")
}
