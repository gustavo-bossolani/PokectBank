package com.pokect.bank.kids.data.models

data class FamilyMember(
    val id: String,
    val name: String,
    val level: Int,
    val coins: Int,
    val xp: Int,
    val streak: Int,
    val isCurrentUser: Boolean = false
)
