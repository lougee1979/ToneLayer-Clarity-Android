// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.clarity

import android.content.SharedPreferences
import androidx.core.content.edit

private const val KEY_ADHD = "ndprofile.adhd"
private const val KEY_AUTISM = "ndprofile.autism"
private const val KEY_AUDHD = "ndprofile.audhd"
private const val KEY_PTSD = "ndprofile.ptsd"
private const val KEY_CPTSD = "ndprofile.cptsd"

/**
 * Multi-select ND reader profile, mirroring the iOS Clarity app's
 * profileADHD/profileAutism/profileAUDHD/profilePTSD/profileCPTSD flags.
 */
data class ClarityProfileSelection(
    val adhd: Boolean = false,
    val autism: Boolean = false,
    val audhd: Boolean = false,
    val ptsd: Boolean = false,
    val cptsd: Boolean = false
) {
    /** Short label for the server's `profile` field, e.g. "ADHD, PTSD" or "General ND". */
    fun buildProfileString(): String {
        val parts = mutableListOf<String>()
        if (audhd) {
            parts += "AUDHD"
        } else {
            if (adhd) parts += "ADHD"
            if (autism) parts += "Autism"
        }
        if (ptsd) parts += "PTSD"
        if (cptsd) parts += "CPTSD"
        return if (parts.isEmpty()) "General ND" else parts.joinToString(", ")
    }

    /** Longer prose instructions, used for local (offline) rewrites and teaching text. */
    fun buildProfileInstructions(): String {
        val parts = mutableListOf<String>()
        if (audhd || (adhd && autism)) {
            parts += "AUDHD: combine ADHD and Autism communication traits — put priority and next action first, use ultra-literal language, eliminate all social subtext and implied expectations, define every vague phrase (soon, later, we should talk), reduce working-memory load, make urgency and the ask fully explicit."
        } else {
            if (adhd) {
                parts += "ADHD: reduce working-memory load, put priority and next action first, make urgency explicit, avoid buried asks and long multi-step wording."
            }
            if (autism) {
                parts += "Autism: make meaning fully literal, remove all social subtext and implied expectations, define every vague phrase (soon, later, we should talk), state the ask directly."
            }
        }
        if (ptsd) {
            parts += "PTSD: lower all threat signals, add reassurance where appropriate, avoid vague warnings or power-heavy phrasing, make emotional stakes explicit and calm."
        }
        if (cptsd) {
            parts += "CPTSD: avoid language implying punishment, withdrawal, or conditional approval. Be warm, non-threatening, and explicit about safety and intent. Address fawn and freeze response patterns."
        }
        return if (parts.isEmpty())
            "General ND: remove all ambiguity, make the ask explicit, add necessary context, state urgency, and give a concrete next step."
        else parts.joinToString(" ")
    }

    fun saveToPrefs(prefs: SharedPreferences) {
        prefs.edit {
            putBoolean(KEY_ADHD, adhd)
            putBoolean(KEY_AUTISM, autism)
            putBoolean(KEY_AUDHD, audhd)
            putBoolean(KEY_PTSD, ptsd)
            putBoolean(KEY_CPTSD, cptsd)
        }
    }

    companion object {
        fun fromPrefs(prefs: SharedPreferences): ClarityProfileSelection = ClarityProfileSelection(
            adhd = prefs.getBoolean(KEY_ADHD, false),
            autism = prefs.getBoolean(KEY_AUTISM, false),
            audhd = prefs.getBoolean(KEY_AUDHD, false),
            ptsd = prefs.getBoolean(KEY_PTSD, false),
            cptsd = prefs.getBoolean(KEY_CPTSD, false)
        )
    }
}
