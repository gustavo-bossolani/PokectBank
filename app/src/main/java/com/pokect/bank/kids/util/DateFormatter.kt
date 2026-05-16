package com.pokect.bank.kids.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Formats a date string into a human-readable label.
 * Returns "Hoje" for today, "Ontem" for yesterday, or "dd/MM" otherwise.
 */
fun formatDateLabel(dateStr: String): String {
    return runCatching {
        val date = LocalDate.parse(dateStr.take(10))
        val today = LocalDate.now()
        when {
            date == today -> "Hoje"
            date == today.minusDays(1) -> "Ontem"
            else -> date.format(DateTimeFormatter.ofPattern("dd/MM"))
        }
    }.getOrDefault(dateStr.take(10))
}
