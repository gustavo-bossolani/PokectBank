package com.pokect.bank.kids.data.models

enum class GoalColor { PRIMARY, SECONDARY, SUCCESS, WARNING }

data class SavingsGoal(
    val id: String,
    val title: String,
    val icon: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val daysLeft: Int,
    val color: GoalColor
)
