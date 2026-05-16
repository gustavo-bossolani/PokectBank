package com.pokect.bank.kids.ui.screens.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant

private val goalEmojis = listOf(
    "\uD83C\uDFAE", // 🎮
    "\uD83D\uDEB2", // 🚲
    "\uD83D\uDCDA", // 📚
    "\uD83C\uDFA8", // 🎨
    "\u26BD",     // ⚽
    "\uD83C\uDFB8", // 🎸
    "\uD83E\uDDF8", // 🧸
    "\uD83C\uDFA7", // 🎧
    "\uD83D\uDCF1", // 📱
    "\uD83D\uDEF9", // 🛹
    "\uD83C\uDFAF", // 🎯
    "\uD83C\uDFC6", // 🏆
)

@Composable
fun EmojiPickerGrid(
    selectedEmoji: String,
    onEmojiSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(goalEmojis) { emoji ->
            val isSelected = emoji == selectedEmoji
            EmojiCell(
                emoji = emoji,
                isSelected = isSelected,
                onClick = { onEmojiSelected(emoji) }
            )
        }
    }
}

@Composable
private fun EmojiCell(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (isSelected) {
        GradientPresets.gradientPrimary
    } else {
        null
    }

    Box(
        modifier = modifier
            .clip(KidShapes.medium)
            .then(
                if (background != null) {
                    Modifier.background(background)
                } else {
                    Modifier.background(PokectBankSurfaceVariant)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier
                .padding(12.dp)
                .semantics { contentDescription = "Emoji: $emoji" }
        )
    }
}
