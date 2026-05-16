package com.pokect.bank.kids.data.models

data class AvatarOption(val id: Int, val emoji: String, val name: String) {
    companion object {
        val options = listOf(
            AvatarOption(1, "🦁", "Leão"),
            AvatarOption(2, "🐰", "Coelho"),
            AvatarOption(3, "🐻", "Urso"),
            AvatarOption(4, "🦊", "Raposa"),
            AvatarOption(5, "🐼", "Panda"),
            AvatarOption(6, "🐨", "Coala")
        )
    }
}
