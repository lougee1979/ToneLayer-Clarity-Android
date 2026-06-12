// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.decoder

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class ClarityDecodeEntry(
    val id: String,
    val timestamp: Long,
    val contact: String,
    val text: String,
    val sensitivity: String,
    val translation: String,
    val patterns: List<String>,
    val baseline: String
)

/** Mirrors the iOS `ClarityDecodeStore` — a JSON-array log capped at 500 entries. */
class ClarityDecodeStore(private val context: Context) {
    private val file: File get() = File(context.filesDir, "clarity_decode_log.json")

    fun load(): List<ClarityDecodeEntry> {
        if (!file.exists()) return emptyList()
        return runCatching {
            val array = JSONArray(file.readText())
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                ClarityDecodeEntry(
                    id = obj.getString("id"),
                    timestamp = obj.getLong("timestamp"),
                    contact = obj.getString("contact"),
                    text = obj.getString("text"),
                    sensitivity = obj.getString("sensitivity"),
                    translation = obj.getString("translation"),
                    patterns = obj.optJSONArray("patterns")?.let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }
                    } ?: emptyList(),
                    baseline = obj.optString("baseline", "")
                )
            }
        }.getOrDefault(emptyList())
    }

    fun messagesFor(contact: String): List<ClarityDecodeEntry> {
        if (contact.isBlank()) return emptyList()
        return load().filter { it.contact.equals(contact, ignoreCase = true) }
    }

    fun append(entry: ClarityDecodeEntry) {
        var entries = load() + entry
        if (entries.size > 500) entries = entries.takeLast(500)
        val array = JSONArray()
        entries.forEach { e ->
            array.put(JSONObject().apply {
                put("id", e.id)
                put("timestamp", e.timestamp)
                put("contact", e.contact)
                put("text", e.text)
                put("sensitivity", e.sensitivity)
                put("translation", e.translation)
                put("patterns", JSONArray(e.patterns))
                put("baseline", e.baseline)
            })
        }
        file.writeText(array.toString())
    }
}
