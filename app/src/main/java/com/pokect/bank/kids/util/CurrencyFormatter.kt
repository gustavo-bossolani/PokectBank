package com.pokect.bank.kids.util

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return currencyFormat.format(amount)
}
