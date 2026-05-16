package com.pokect.bank.kids.data.models

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val cost: Int,
    val isNew: Boolean,
    val redeemed: Boolean = false
)
