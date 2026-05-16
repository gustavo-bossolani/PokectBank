package com.pokect.bank.kids.ui.screens.home

import com.pokect.bank.kids.data.models.Mission
import com.pokect.bank.kids.data.models.Transaction

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val balance: Double = 0.0,
    val goal: Double = 0.0,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 1000,
    val totalXp: Int = 0,
    val streak: Int = 0,
    val coins: Int = 0,
    val badges: List<String> = emptyList(),
    val missions: List<Mission> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val isHistoryExpanded: Boolean = false,
    val showDepositModal: Boolean = false,
    val depositAmount: Double = 5.0,
    val showConfetti: Boolean = false,
    val depositSuccess: Boolean = false,
    val error: String? = null,
    val selectedAvatar: Int = 0
) {
    fun getGoalProgressPercent(): Float =
        if (goal <= 0.0) 0f
        else ((balance / goal * 100).toFloat()).coerceIn(0f, 100f)

    fun getXpProgressPercent(): Float =
        if (xpToNextLevel <= 0) 0f
        else (currentXp.toFloat() / xpToNextLevel * 100).coerceIn(0f, 100f)

    fun getLevelTitle(): String = when (level) {
        in 1..2 -> "Aprendiz Financeiro 📚"
        in 3..4 -> "Pequeno Investidor 🌱"
        in 5..6 -> "Colecionador de Moedas 💰"
        in 7..9 -> "Guardião do Cofrinho 🛡️"
        in 10..14 -> "Super Poupador 🚀"
        in 15..19 -> "Expert em Economia 💎"
        else -> "Mestre das Finanças 🏆"
    }
}
