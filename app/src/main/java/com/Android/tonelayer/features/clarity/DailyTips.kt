// Copyright (c) 2026 Alden Lougee. All rights reserved.
// Proprietary and confidential. Unauthorized copying, modification,
// distribution, or derivative use is prohibited.

package com.Android.tonelayer.features.clarity

import java.util.Calendar

data class DailyTip(val title: String, val body: String)

val dailyTips: List<DailyTip> = listOf(
    DailyTip(
        "A blocked call may not feel neutral",
        "For someone with ADHD, trauma history, or rejection sensitivity, being blocked or repeatedly sent to voicemail can feel like rejection before there is any explanation. A short text like \"I cannot talk now, but I will reply later\" is usually safer."
    ),
    DailyTip(
        "Hinting creates extra work",
        "Many neurodivergent people communicate better when the request is explicit. Instead of hoping they infer the problem, name what you need, when you need it, and whether it is urgent."
    ),
    DailyTip(
        "Short can sound angry",
        "A message like \"fine\" or \"whatever\" may land as punishment or shutdown. If you mean reassurance, say it plainly: \"We are okay. I just need a little time.\""
    ),
    DailyTip(
        "Unclear urgency can trigger panic",
        "Messages like \"call me\" or \"we need to talk\" can create anxiety because the person has to guess the emotional stakes. Add context when you can."
    ),
    DailyTip(
        "Autistic processing may need precision",
        "Concrete language is often easier than social shorthand. Saying exactly what changed, what you expect, and what is optional reduces confusion."
    ),
    DailyTip(
        "PTSD can read threat quickly",
        "A nervous system shaped by trauma may detect danger before logic catches up. Calm wording, predictable timing, and clear reassurance can reduce escalation."
    ),
    DailyTip(
        "Repair beats perfect wording",
        "If your message lands badly, explain your intention without blaming the person for reacting. Repair sounds like: \"I see how that came across. What I meant was...\""
    )
)

fun todayTip(): DailyTip {
    val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return dailyTips[(day - 1) % dailyTips.size]
}
