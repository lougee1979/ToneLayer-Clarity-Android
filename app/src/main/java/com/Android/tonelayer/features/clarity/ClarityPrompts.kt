// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.clarity

fun createClarityAnalysis(input: String, profile: ClarityProfileSelection): String {
    val message = input.trim().ifEmpty { "the message" }
    val blocks = mutableListOf<String>()

    if (profile.audhd || (profile.adhd && profile.autism)) {
        blocks += """
AUDHD Reader Profile
This NT wording may combine working-memory load with literal-reading and social-subtext friction.

How it may land:
- buried priority and unclear next action
- ultra-literal reading of vague phrases (soon, later, we should talk)
- hidden social expectations
- unclear urgency

Clarity should put priority and the next action first, use ultra-literal language, remove social subtext, and make urgency explicit.
""".trimIndent()
    } else {
        if (profile.adhd) {
            blocks += """
ADHD Reader Profile
This NT wording may create cognitive load if the point, timing, or expected action is implied.

How it may land:
- unclear urgency
- unclear next step
- too much social subtext
- hard to know what to do first

Clarity should state the topic, urgency, and requested action directly.
""".trimIndent()
        }
        if (profile.autism) {
            blocks += """
Autism Reader Profile
This NT wording may rely on implied emotional context or social inference.

How it may land:
- unclear purpose
- unclear seriousness
- hidden expectation
- vague timing

Clarity should make the topic, intent, and expectation explicit.
""".trimIndent()
        }
    }

    if (profile.ptsd) {
        blocks += """
PTSD Reader Profile
This NT wording may create anticipatory stress if safety, urgency, or conflict level is unclear.

How it may land:
- "am I in trouble?"
- undefined conflict level
- open-ended tension
- unclear urgency

Clarity should include true reassurance, concrete timing, and a clear reason for the conversation.
""".trimIndent()
    }

    if (profile.cptsd) {
        blocks += """
CPTSD Reader Profile
This NT wording may read as conditional approval, withdrawal, or punishment, even when none is intended.

How it may land:
- fear of punishment or withdrawal
- fawn or freeze response
- difficulty trusting reassurance
- hypervigilance for tone shifts

Clarity should be warm, non-threatening, and explicit about safety and intent.
""".trimIndent()
    }

    if (blocks.isEmpty()) {
        blocks += """
General ND Reader Profile
This NT wording may rely on ambiguity, implied urgency, or unstated next steps.

How it may land:
- buried main point
- unclear ask
- vague timing
- hidden social expectation

Clarity should remove ambiguity, make the ask explicit, add necessary context, and give a concrete next step.
""".trimIndent()
    }

    blocks += """
Current message being analyzed:
"$message"
""".trimIndent()

    return blocks.joinToString("\n\n")
}
