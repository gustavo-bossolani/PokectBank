package com.pokect.bank.kids.ui.screens.home

import com.pokect.bank.kids.data.models.Transaction
import com.pokect.bank.kids.util.formatDateLabel

/**
 * Groups transactions by date label ("Hoje", "Ontem", "dd/MM").
 * Test utility only — not used in production UI code.
 */
fun getGroupedTransactions(transactions: List<Transaction>): Map<String, List<Transaction>> {
    return transactions.groupBy { tx -> formatDateLabel(tx.date) }
}
