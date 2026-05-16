package com.pokect.bank.kids.data.models

enum class MissionStatus { LOCKED, AVAILABLE, IN_PROGRESS, COMPLETED }
enum class MissionDifficulty { EASY, MEDIUM, HARD }
enum class MissionCategory(val label: String) {
    DAILY("Diárias"),
    WEEKLY("Semanais"),
    SPECIAL("Especiais")
}

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val xp: Int,
    val coins: Int,
    val progress: Int,
    val maxProgress: Int,
    val status: MissionStatus,
    val icon: String,
    val difficulty: MissionDifficulty,
    val category: MissionCategory = MissionCategory.DAILY
)
