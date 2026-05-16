package com.pokect.bank.kids.data.models

enum class TransactionType { DEPOSIT, WITHDRAW, REWARD, MISSION, GOAL }

data class Transaction(
    val id: String,
    val type: TransactionType,
    val title: String,
    val amount: Double,
    val date: String,
    val icon: String? = null
)
