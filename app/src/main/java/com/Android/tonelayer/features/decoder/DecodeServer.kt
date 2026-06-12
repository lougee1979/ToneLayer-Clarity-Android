// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.decoder

import com.Android.tonelayer.features.clarity.ClarityHttpException
import com.Android.tonelayer.features.clarity.TL_APP_TOKEN
import com.Android.tonelayer.features.clarity.TL_SERVER_URL
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class ClarityDecodeResult(
    val translation: String,
    val patterns: List<String>,
    val communicationStyle: String,
    val baseline: String,
    val tentative: Boolean
)

/**
 * Matches the real `/decode` route in `tonelayer-server/server.js`, which expects a
 * `baseline` summary object (`messageCount`, `avgLength`, `observedPatterns`) rather
 * than the raw `history` array the iOS app sends (the server doesn't read `history`).
 */
fun callDecode(
    text: String,
    contact: String,
    sensitivity: String,
    senderProfile: String,
    history: List<ClarityDecodeEntry>
): ClarityDecodeResult {
    val trimmedContact = contact.trim().ifEmpty { "Unknown" }
    val body = JSONObject().apply {
        put("text", text)
        put("contact", trimmedContact)
        put("sensitivity", sensitivity)
        put("senderProfile", senderProfile)
        if (history.isNotEmpty()) {
            val avgLength = history.map { it.text.length }.average().toInt()
            val observedPatterns = history.flatMap { it.patterns }.distinct()
            put("baseline", JSONObject().apply {
                put("messageCount", history.size)
                put("avgLength", avgLength)
                put("observedPatterns", JSONArray(observedPatterns))
            })
        }
    }

    val conn = (URL("$TL_SERVER_URL/decode").openConnection() as HttpURLConnection).apply {
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
    val translation = parsed.optString("translation", "")
    val patterns = parsed.optJSONArray("patterns")?.let { arr ->
        (0 until arr.length()).map { arr.getString(it) }
    } ?: emptyList()
    val baseline = parsed.optString("baseline", "")
    val tentative = parsed.optBoolean("tentative", false) || baseline.lowercase().contains("building")
    return ClarityDecodeResult(
        translation = translation,
        patterns = patterns,
        communicationStyle = parsed.optString("communication_style", ""),
        baseline = baseline,
        tentative = tentative
    )
}

fun friendlyDecodeFailure(error: Throwable): String {
    return when {
        error is ClarityHttpException && error.statusCode == 429 ->
            "The Clarity server is rate-limiting requests right now. Try again in a moment."
        error is ClarityHttpException && error.statusCode in 500..599 ->
            "The Clarity server is having an issue right now. Try again in a moment."
        else ->
            "Decode is unavailable right now. Check your connection and try again."
    }
}
