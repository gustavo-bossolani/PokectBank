package com.pokect.bank.kids.data.models

data class User(
    val name: String,
    val level: Int,
    val balance: Double,
    val goal: Double,
    val xp: Int,
    val xpToNextLevel: Int,
    val totalXp: Int,
    val streak: Int,
    val coins: Int,
    val badges: List<String>,
    val selectedAvatar: Int
)
