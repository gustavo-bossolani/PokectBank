package com.pokect.bank.kids.data.models

import java.time.Instant

data class WeeklyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val rewardXp: Int,
    val rewardCoins: Int,
    val endDate: Instant,
    val participants: List<String> = emptyList(),
    val hasJoined: Boolean = false
)
